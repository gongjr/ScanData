package com.gjr.scandata.http;

/**
 * Created by gjr on 2016/5/10 19:55.
 * mail : gjr9596@gmail.com
 */
public enum AddressState {


    release("Address_release","生产环境","http://www.cclovenn.com"),
    localtest("Address_localtest","本地环境","http://192.168.1.131:8080/tacosonline");

    /**
     * 判断关键字
     */
    private String value;
    /**
     * 内容描述
     */
    private String title;

    /**
     * 内容key
     */
    private String key;

    private AddressState(String value, String title, String key) {
        this.setValue(value);
        this.setTitle(title);
        this.setKey(key);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String pKey) {
        key = pKey;
    }
}
