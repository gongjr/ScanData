package com.gjr.scandata.biz.bean;

/**
 * 商品类型
 * Created by gjr on 2016/7/20 10:05.
 * mail : gjr9596@gmail.com
 */
public class HttpGoodsType {

    /**
     * id : 10000025
     * qcTypeCode : 101.101
     * qcTypeName : 101
     * preQcTypeCode : 0,如果为二级目录,存放其对应的一级目录的id
     * qcTypeLevel : 1-目录等级(1,一级目录|2,二级目录)
     * createTime : 1468568276000
     * preQcType : null
     * editTime : 1468222676000
     */

    private String id;
    private String qcTypeCode;
    private String qcTypeName;
    private String preQcTypeCode;
    private String qcTypeLevel;
    private long createTime;
    private String preQcType;
    private long editTime;

    public String getId() {
        return id;
    }

    public void setId(String pId) {
        id = pId;
    }

    public String getQcTypeCode() {
        return qcTypeCode;
    }

    public void setQcTypeCode(String pQcTypeCode) {
        qcTypeCode = pQcTypeCode;
    }

    public String getQcTypeName() {
        return qcTypeName;
    }

    public void setQcTypeName(String pQcTypeName) {
        qcTypeName = pQcTypeName;
    }

    public String getPreQcTypeCode() {
        return preQcTypeCode;
    }

    public void setPreQcTypeCode(String pPreQcTypeCode) {
        preQcTypeCode = pPreQcTypeCode;
    }

    public String getQcTypeLevel() {
        return qcTypeLevel;
    }

    public void setQcTypeLevel(String pQcTypeLevel) {
        qcTypeLevel = pQcTypeLevel;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long pCreateTime) {
        createTime = pCreateTime;
    }

    public String getPreQcType() {
        return preQcType;
    }

    public void setPreQcType(String pPreQcType) {
        preQcType = pPreQcType;
    }

    public long getEditTime() {
        return editTime;
    }

    public void setEditTime(long pEditTime) {
        editTime = pEditTime;
    }

    public GoodsType toGoodsType(){
        GoodsType lGoodsType=new GoodsType();
        lGoodsType.setGoodsTypeId(id);
        lGoodsType.setQcTypeCode(qcTypeCode);
        lGoodsType.setQcTypeName(qcTypeName);
        lGoodsType.setPreQcTypeCode(preQcTypeCode);
        lGoodsType.setQcTypeLevel(qcTypeLevel);
        lGoodsType.setCreateTime(createTime);
        lGoodsType.setPreQcType(preQcType);
        lGoodsType.setEditTime(editTime);
        return lGoodsType;
    }
}
