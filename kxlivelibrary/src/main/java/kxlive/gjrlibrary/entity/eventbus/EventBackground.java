package kxlive.gjrlibrary.entity.eventbus;

/**
 * 后台单线程执行事件发布实体类
 * 后台队列顺序执行，一个一个调用执行，子线程发布的话就直接在里面执行，否则加入任务队列
 * @author gjr
 * @param <T>
 */
public class EventBackground<T> {
    /**
     * 公用type类型，用于区分统一name种的，事件类型
     */
    public static final int TYPE_FIRST=0;
    public static final int TYPE_SECOND=1;
    public static final int TYPE_THREE=2;
    public static final int TYPE_FOUR=3;
    public static final int TYPE_FIVE=4;
    public static final int TYPE_SIX=5;
    public static final int TYPE_SEVEN=6;
    public static final int TYPE_EIGHT=7;
    public static final int TYPE_NINE=8;
    public static final int TYPE_TEN=9;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
    /**
     * 事件传递数据
     */
    public T data;
    /**
     * 事件处理类型
     */
    public int type;
    /**
     * 事件分发目标类名称:class.getName()
     */
    public String name;
    /**
     * 事件详情描述
     */
    public String describe;


}
