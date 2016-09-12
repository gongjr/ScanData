package com.gjr.scandata.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kxlive.gjrlibrary.utils.KLog;


/**
 * 图片加载器，线程池管理httpClient下载，不能优先级加载图片
 * 无内存缓存,只进行图片硬盘缓存,与管理
 * @author gjr
 */
public class ImagesLoader {
    private static final String ImageDownLoader_Log = ImageFileUtils
            .makeLogTag(ImagesLoader.class);

    /**
     * 保存正在下载或等待下载的URL和相应失败下载次数（初始为0），防止滚动时多次下载
     */
    private Hashtable<String, Integer> taskCollection;

    /**
     * 线程池
     */
    private ExecutorService threadPool;
    /**
     * 缓存文件目录 （如无SD卡，则data目录下）
     */
    private File cacheFileDir;
    /**
     * 缓存文件夹
     */
    private final String DIR_CACHE;
    /**
     * 缓存文件夹最大容量限制（100M）
     */
    private static final long DIR_CACHE_LIMIT = 1000 * 1024 * 1024;
    /**
     * 图片下载失败重试次数
     */
    private static final int IMAGE_DOWNLOAD_FAIL_TIMES = 0;

    public ImagesLoader(Context context, String dir_name) {
        DIR_CACHE = dir_name;

        taskCollection = new Hashtable<String, Integer>();
        // 创建线程数
        threadPool = Executors.newFixedThreadPool(6);
        cacheFileDir = ImageFileUtils.createFileDir(context, DIR_CACHE);
    }

    /**
     * 异步下载图片，并按指定宽度和高度压缩图片
     *
     * @param url
     * @param width
     * @param height
     * @param listener 图片下载完成后调用接口
     */
    public void loadImage(final String url, final int width, final int height,
                          AsyncImageLoaderListener listener) {
        Log.i(ImageDownLoader_Log, "download:" + url);
        final ImageHandler handler = new ImageHandler(listener);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String urlKey =getURLName(url);
                Bitmap bitmap=null;
                if (ImageFileUtils.isFileExists(cacheFileDir, urlKey)
                        && ImageFileUtils.getFileSize(new File(cacheFileDir, urlKey)) > 0) {
                    KLog.i("跳过下载-本地目录 "+cacheFileDir+" 已存在图片:"+urlKey);
                    taskCollection.remove(url);
                    Holder holder = new Holder();
                    holder.image = bitmap;
                    holder.url = url;
                    holder.taskSize=taskCollection.size();
                    Message msg = handler.obtainMessage();
                    msg.obj = holder;
                    handler.sendMessage(msg);
                }else{
                    bitmap = downloadImage(url, width, height);
                    taskCollection.remove(url);
                    Holder holder = new Holder();
                    holder.image = bitmap;
                    holder.url = url;
                    holder.taskSize=taskCollection.size();
                    Message msg = handler.obtainMessage();
                    msg.obj = holder;
                    handler.sendMessage(msg);
                    // 加入文件缓存前，需判断缓存目录大小是否超过限制，超过则清空缓存再加入
//                long cacheFileSize = ImageFileUtils.getFileSize(cacheFileDir);
//                if (cacheFileSize > DIR_CACHE_LIMIT) {
//                    Log.i(ImageDownLoader_Log, cacheFileDir
//                            + " size has exceed limit." + cacheFileSize);
//                    ImageFileUtils.delFile(cacheFileDir, false);
//                    taskCollection.clear();
//                }
//                String urlKey =getURLName(url);
//                    if (ImageFileUtils.isFileExists(cacheFileDir, urlKey)
//                            && ImageFileUtils.getFileSize(new File(cacheFileDir, urlKey)) > 0) {
//                        KLog.i("本地目录 "+cacheFileDir+" 已存在图片:"+urlKey);
//                        boolean isD=ImageFileUtils.deleteFile(cacheFileDir, urlKey);
//                        KLog.i("删除"+urlKey+isD);
//                    }
                    if (bitmap!=null)
                    ImageFileUtils.savaBitmap(cacheFileDir, urlKey, bitmap);
//              确定bitmap不再使用时回收位图
                    if(bitmap!=null){
                        bitmap.recycle();
                    }
                }
            }
        };
        // 记录该url，防止滚动时多次下载，0代表该url下载失败次数
        taskCollection.put(url, 0);
        threadPool.execute(runnable);
    }

    /**
     *
     * @param url
     * @return 若缓存中没找到，则返回null
     */
    public Bitmap getBitmapCache(String url) {
        if (url != null) {
            // 去处url中特殊字符作为文件缓存的名称
            String urlKey = url.replaceAll("[^\\w]", "");
            if (ImageFileUtils.isFileExists(cacheFileDir, urlKey)
                    && ImageFileUtils.getFileSize(new File(cacheFileDir, urlKey)) > 0) {
                // 从文件缓存中获取Bitmap
                Bitmap bitmap = BitmapFactory.decodeFile(cacheFileDir.getPath()
                        + File.separator + urlKey);
                return bitmap;
            }
        }
        return null;
    }

    /**
     * 下载图片，并按指定高度和宽度压缩
     *
     * @param url
     * @param width
     * @param height
     * @return
     */
    private Bitmap downloadImage(String url, int width, int height) {
        Bitmap bitmap = null;
        HttpClient httpClient = new DefaultHttpClient();
        try {
            httpClient.getParams().setParameter(
                    CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            HttpPost httpPost = new HttpPost(url);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = httpResponse.getEntity();
                byte[] byteIn = EntityUtils.toByteArray(entity);
                BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
                bmpFactoryOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(byteIn, 0, byteIn.length,
                        bmpFactoryOptions);
                int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight
                        / height);
                KLog.i("loader--:" + bmpFactoryOptions.outHeight + "--/--" + height);
                int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth
                        / width);
                if (heightRatio > 1 && widthRatio > 1) {
                    bmpFactoryOptions.inSampleSize = heightRatio > widthRatio ? heightRatio
                            : widthRatio;
                }
                bmpFactoryOptions.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeByteArray(byteIn, 0,
                        byteIn.length, bmpFactoryOptions);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpClient != null && httpClient.getConnectionManager() != null) {
                httpClient.getConnectionManager().shutdown();
            }
        }
        // 下载失败，再重新下载
        if (taskCollection.get(url) != null) {
            int times = taskCollection.get(url);
            if (bitmap == null
                    && times < IMAGE_DOWNLOAD_FAIL_TIMES) {
                times++;
                taskCollection.put(url, times);
                bitmap = downloadImage(url, width, height);
                Log.i(ImageDownLoader_Log, "Re-download " + url + ":" + times);
            }
        }
        return bitmap;
    }

    /**
     * 取消正在下载的任务
     */
    public synchronized void cancelTasks() {
        if (threadPool != null) {
            threadPool.shutdownNow();
            threadPool = null;
        }
    }

    /**
     * 获取任务列表
     *
     * @return
     */
    public Hashtable<String, Integer> getTaskCollection() {
        return taskCollection;
    }

    /**
     * 异步加载图片接口
     */
    public interface AsyncImageLoaderListener {
        void onImageLoader(Bitmap bitmap, String url,int size);
    }

    /**
     * 异步加载完成后，图片处理
     */
    static class ImageHandler extends Handler {

        private AsyncImageLoaderListener listener;

        public ImageHandler(AsyncImageLoaderListener listener) {
            this.listener = listener;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Holder holder = new Holder();
            holder = (Holder) msg.obj;
            listener.onImageLoader(holder.image, holder.url,holder.taskSize);
        }

    }

    static class Holder {
        Bitmap image;
        String url;
        int taskSize;
    }

    /**
     * 根据url,解析本地硬盘双重缓存路径
     * @param url
     * @return
     */
    public String getLocalURL(String url){
        String lastName="file:///mnt/sdcard";
        String[] names = url.split("/");
        for (int i=0;i<names.length;i++){
            KLog.i(names[i]);
            if (i==names.length-2||i==names.length-1){
                lastName+="/"+names[i];
            }
        }
        return lastName;
    }

    /**
     * 根据url,获取文件名
     * @param url
     * @return
     */
    public String getURLName(String url){
        String lastName="";
        String[] names = url.split("/");
        for (int i=0;i<names.length;i++){
            if (i==names.length-1){
                lastName=names[i];
            }
        }
        return lastName;
    }

    public void clearCacheDir(){
        ImageFileUtils.delFile(cacheFileDir, false);
    }

    public void clearCacheDir(String url){
        String urlKey =getURLName(url);
        if (ImageFileUtils.isFileExists(cacheFileDir, urlKey)
                && ImageFileUtils.getFileSize(new File(cacheFileDir, urlKey)) > 0) {
            KLog.i("本地目录 "+cacheFileDir+" 存在图片:"+urlKey);
            boolean isD=ImageFileUtils.deleteFile(cacheFileDir, urlKey);
            KLog.i("删除"+urlKey+isD);
        }
    }
}
