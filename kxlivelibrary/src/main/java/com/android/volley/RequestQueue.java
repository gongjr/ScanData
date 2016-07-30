/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RequestQueue类作为volley的核心类，可以说是连接请求与响应的桥梁
 *  一个拥有线程池的请求队列
 * 调用add()分发，将添加一个用于分发的请求
 * worker线程从缓存或网络获取响应，然后将该响应提供给主线程
 *
 * A request dispatch queue with a thread pool of dispatchers.
 * Calling {@link #add(com.android.volley.Request)} will enqueue the given Request for dispatch,
 * resolving from either cache or network on a worker thread, and then delivering
 * a parsed response on the main thread.
 */
public class RequestQueue {

    /**
     *  任务完成的回调接口
     * Callback interface for completed requests.
     */
    public static interface RequestFinishedListener<T> {
        /** Called when a request has finished processing. */
        public void onRequestFinished(Request<T> request);
    }

    /**
     * 原子操作的Integer的类,线程安全的加减操作接口，记录队列中当前的请求数目
     * Used for generating monotonically-increasing sequence numbers for requests.
     */
    private AtomicInteger mSequenceGenerator = new AtomicInteger();

    /**
     * 等候缓存队列，重复请求集结map,每个queue里面都是相同的请求。
     * 为什么需要这个map呢？map的key其实是request的url，如果我们有多个请求的url都是相同的，也就是说请求的资源是相同的，
     * volley就把这些请求放入一个队列，在用url做key将队列放入map中。
     * 因为这些请求都是相同的，可以说结果也是相同的。那么我们只要获得一个请求的结果，其他相同的请求，从缓存中取就可以了。
     * 所以等候缓存队列的作用就是，当其中的一个request获得响应，我们就将这个队列放入缓存队列mCacheQueue中，让这些request去缓存获取结果就好了。
     * 这是volley处理重复请求的思路。
     * Staging area for requests that already have a duplicate request in flight.
     * <ul>
     *     <li>containsKey(cacheKey) indicates that there is a request in flight for the given cache
     *          key.</li>
     *     <li>get(cacheKey) returns waiting requests for the given cache key. The in flight request
     *          is <em>not</em> contained in that list. Is null if no requests are staged.</li>
     * </ul>
     */
    private final Map<String, Queue<Request<?>>> mWaitingRequests =
            new HashMap<String, Queue<Request<?>>>();

    /**
     * The set of all requests currently being processed by this RequestQueue. A Request
     * will be in this set if it is waiting in any queue or currently being processed by
     * any dispatcher.
     * 队列当前拥有的所有请求的集合
     * 请求在队列中或者正被调度中，都会在这个集合里
     * 也就是下面mCacheQueue缓存队列与mNetworkQueue网络队列的总和
     */
    private final Set<Request<?>> mCurrentRequests = new HashSet<Request<?>>();

    /**
     * 缓存队列
     * The cache triage queue. */
    private final PriorityBlockingQueue<Request<?>> mCacheQueue =
        new PriorityBlockingQueue<Request<?>>();

    /**
     * 网络队列，PriorityBlockingQueue-阻塞优先级队列
     * 线程安全，有阻塞功能，也就是说当队列里面没有东西的时候，线程试图从队列取请求，这个线程就会阻塞
     * 根据Request实现compareTo接口可知:请求优先级高,则在队列中优先级高,
     * 如果优先级相同,则根据mSequence序列号,来判断,先进先出
     * The queue of requests that are actually going out to the network. */
    private final PriorityBlockingQueue<Request<?>> mNetworkQueue =
        new PriorityBlockingQueue<Request<?>>();

    /**
     * 默认用于调度的线程池数目
     * Number of network request dispatcher threads to start. */
    private static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 3;

    /**
     * 缓存接口，面向对象的思想，把缓存看成一个实体,此处只声明缓存实体的实现规则接口,
     * 具体实现的时候,可以是用默认实现了Cache接口的DiskBasedCache类实体,也可以自定义扩展
     * Cache interface for retrieving and storing responses. */
    private final Cache mCache;

    /**
     * 网络接口，面向对象的思想，把网络看成一个实体,此处只声明网络实体的实现规则接口,
     * 具体实现的时候,可以是默认实现了Network接口的BasicNetwork类实体,也可以自定义扩展
     * Network interface for performing requests. */
    private final Network mNetwork;

    /**
     * 响应分发器接口,负责把响应发给对应的请求，分发器存在的意义主要是为了耦合更加低并且能在主线程中操作UI
     * 将网络响应的分发操作看成一个实体,此处声明实体的规则接口
     * 具体实现时:可以是默认实现ResponseDelivery接口的ExecutorDelivery实体
     * Response delivery mechanism. */
    private final ResponseDelivery mDelivery;

    /**
     * 网络调度器数组,NetworkDispatcher继承了Thread类，其本质是多个线程，
     * 所有线程都将被开启进入死循环，不断从mNetworkQueue网络队列取出请求，然后去网络Network请求数据
     * The network dispatchers. */
    private NetworkDispatcher[] mDispatchers;

    /**
     * 缓存调度器CacheDispatcher继承了Thread类,本质是一个线程，这个线程将会被开启进入一个死循环，
     * 不断从mCacheQueue缓存队列取出请求，然后去缓存Cache中查找结果,如果没有请求则阻塞
     * The cache dispatcher. */
    private CacheDispatcher mCacheDispatcher;

    /**
     * 任务完成监听器队列
     * 这个队列保留了很多监听器，这些监听器都是监听RequestQueue请求队列的，而不是监听单独的某个请求。
     * RequestQueue中每个请求完成后，都会回调这个监听队列里面的所有监听器。
     * 这是RequestQueue的统一管理的体现
     */
    private List<RequestFinishedListener> mFinishedListeners =
            new ArrayList<RequestFinishedListener>();

    /**
     * Creates the worker pool. Processing will not begin until {@link #start()} is called.
     *
     * @param cache A Cache to use for persisting responses to disk
     * @param network A Network interface for performing HTTP requests
     * @param threadPoolSize Number of network dispatcher threads to create
     * @param delivery A ResponseDelivery interface for posting responses and errors
     */
    public RequestQueue(Cache cache, Network network, int threadPoolSize,
            ResponseDelivery delivery) {
        mCache = cache;
        mNetwork = network;
        mDispatchers = new NetworkDispatcher[threadPoolSize];
        mDelivery = delivery;
    }

    /**
     * Creates the worker pool. Processing will not begin until {@link #start()} is called.
     *
     * @param cache A Cache to use for persisting responses to disk
     * @param network A Network interface for performing HTTP requests
     * @param threadPoolSize Number of network dispatcher threads to create
     */
    public RequestQueue(Cache cache, Network network, int threadPoolSize) {
        this(cache, network, threadPoolSize,
                new ExecutorDelivery(new Handler(Looper.getMainLooper())));
    }

    /**
     * Creates the worker pool. Processing will not begin until {@link #start()} is called.
     *
     * @param cache A Cache to use for persisting responses to disk
     * @param network A Network interface for performing HTTP requests
     */
    public
    RequestQueue(Cache cache, Network network) {
        this(cache, network, DEFAULT_NETWORK_THREAD_POOL_SIZE);
    }

    /**
     * Starts the dispatchers in this queue.
     */
    public void start() {
        stop();  // Make sure any currently running dispatchers are stopped.
        // Create the cache dispatcher and start it.
        mCacheDispatcher = new CacheDispatcher(mCacheQueue, mNetworkQueue, mCache, mDelivery);
        mCacheDispatcher.start();

        // Create network dispatchers (and corresponding threads) up to the pool size.
        for (int i = 0; i < mDispatchers.length; i++) {
            NetworkDispatcher networkDispatcher = new NetworkDispatcher(mNetworkQueue, mNetwork,
                    mCache, mDelivery);
            mDispatchers[i] = networkDispatcher;
            networkDispatcher.start();
        }
    }

    /**
     * Stops the cache and network dispatchers.
     */
    public void stop() {
        if (mCacheDispatcher != null) {
            mCacheDispatcher.quit();
        }
        for (int i = 0; i < mDispatchers.length; i++) {
            if (mDispatchers[i] != null) {
                mDispatchers[i].quit();
            }
        }
    }

    /**
     * Gets a sequence number.
     */
    public int getSequenceNumber() {
        return mSequenceGenerator.incrementAndGet();
    }

    /**
     * Gets the {@link com.android.volley.Cache} instance being used.
     */
    public Cache getCache() {
        return mCache;
    }

    /**
     * 一个简单的过滤接口,在cancelAll()方法里面被使用
     * A simple predicate or filter interface for Requests, for use by
     * {@link com.android.volley.RequestQueue#cancelAll(com.android.volley.RequestQueue.RequestFilter)}.
     */
    public interface RequestFilter {
        public boolean apply(Request<?> request);
    }

    /**
     *  根据过滤器规则，取消相应请求
     * Cancels all requests in this queue for which the given filter applies.
     * @param filter The filtering function to use
     */
    public void cancelAll(RequestFilter filter) {
        synchronized (mCurrentRequests) {
            for (Request<?> request : mCurrentRequests) {
                if (filter.apply(request)) {
                    request.cancel();
                }
            }
        }
    }

    /**
     * 根据标记取消相应请求
     * Cancels all requests in this queue with the given tag. Tag must be non-null
     * and equality is by identity.
     */
    public void cancelAll(final Object tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Cannot cancelAll with a null tag");
        }
        cancelAll(new RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return request.getTag() == tag;
            }
        });
    }

    /**
     * Adds a Request to the dispatch queue.
     * @param request The request to service
     * @return The passed-in request
     */
    public <T> Request<T> add(Request<T> request) {
        // Tag the request as belonging to this queue and add it to the set of current requests.
        request.setRequestQueue(this);
        synchronized (mCurrentRequests) {
            mCurrentRequests.add(request);
        }
        //设置请求序号,自动+1获取
        // Process requests in the order they are added.
        request.setSequence(getSequenceNumber());
        request.addMarker("add-to-queue");
        //如果该请求不缓存，直接添加到网络队列退出,跳过会出队列的判断
        // If the request is uncacheable, skip the cache queue and go straight to the network.
        if (!request.shouldCache()) {
            mNetworkQueue.add(request);
            return request;
        }

        // Insert request into stage if there's already a request with the same cache key in flight.
        synchronized (mWaitingRequests) {
            String cacheKey = request.getCacheKey();
            if (mWaitingRequests.containsKey(cacheKey)) {
                // There is already a request in flight. Queue up.
                //如果已经有一个请求在工作，则排队等候
                Queue<Request<?>> stagedRequests = mWaitingRequests.get(cacheKey);
                if (stagedRequests == null) {
                    stagedRequests = new LinkedList<Request<?>>();
                }
                stagedRequests.add(request);
                mWaitingRequests.put(cacheKey, stagedRequests);
                if (VolleyLog.DEBUG) {
                    VolleyLog.v("Request for cacheKey=%s is in flight, putting on hold.", cacheKey);
                }
            } else {
                // Insert 'null' queue for this cacheKey, indicating there is now a request in
                // flight.为该key插入null,表明现在有一个请求在工作
                mWaitingRequests.put(cacheKey, null);
                mCacheQueue.add(request);
            }
            return request;
        }
    }

    /**
     * Called from {@link com.android.volley.Request#finish(String)}, indicating that processing of the given request
     * has finished.
     *
     * <p>Releases waiting requests for <code>request.getCacheKey()</code> if
     *      <code>request.shouldCache()</code>.</p>
     */
    <T> void finish(Request<T> request) {
        // Remove from the set of requests currently being processed.
        synchronized (mCurrentRequests) {
            mCurrentRequests.remove(request);
        }
        synchronized (mFinishedListeners) {
          for (RequestFinishedListener<T> listener : mFinishedListeners) {
            listener.onRequestFinished(request);
          }
        }

        if (request.shouldCache()) {//如果该请求要被缓存
            synchronized (mWaitingRequests) {
                String cacheKey = request.getCacheKey();
                Queue<Request<?>> waitingRequests = mWaitingRequests.remove(cacheKey);
                //移除等候缓存队列中的相同请求
                if (waitingRequests != null) {
                    if (VolleyLog.DEBUG) {
                        VolleyLog.v("Releasing %d waiting requests for cacheKey=%s.",
                                waitingRequests.size(), cacheKey);
                    }
                    // Process all queued up requests. They won't be considered as in flight, but
                    // that's not a problem as the cache has been primed by 'request'.
                    //这里需要注意，一个request完成以后，会将waitingRequests里面所有相同的请求，
                    // 都加入到mCacheQueue缓存队列中，这就意味着，这些请求从缓存中取出结果就好了，
                    // 这样就避免了频繁相同网络请求的开销。这也是Volley的亮点之一。
                    mCacheQueue.addAll(waitingRequests);
                }
            }
        }
    }

    public  <T> void addRequestFinishedListener(RequestFinishedListener<T> listener) {
      synchronized (mFinishedListeners) {
        mFinishedListeners.add(listener);
      }
    }

    /**
     * Remove a RequestFinishedListener. Has no effect if listener was not previously added.
     */
    public  <T> void removeRequestFinishedListener(RequestFinishedListener<T> listener) {
      synchronized (mFinishedListeners) {
        mFinishedListeners.remove(listener);
      }
    }
}
