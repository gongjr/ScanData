package com.gjr.scandata.biz.bean;

import org.litepal.crud.DataSupport;

/**
 * 商品类型
 * Created by gjr on 2016/7/20 10:05.
 * mail : gjr9596@gmail.com
 */
public class GoodsType extends DataSupport {

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

    private String goodsTypeId;
    private String qcTypeCode;
    private String qcTypeName;
    private String preQcTypeCode;
    private String qcTypeLevel;
    private long createTime;
    private String preQcType;
    private long editTime;

    public String getGoodsTypeId() {
        return goodsTypeId;
    }

    public void setGoodsTypeId(String pGoodsTypeId) {
        goodsTypeId = pGoodsTypeId;
    }

    public void setQcTypeCode(String qcTypeCode) {
        this.qcTypeCode = qcTypeCode;
    }

    public void setQcTypeName(String qcTypeName) {
        this.qcTypeName = qcTypeName;
    }

    public void setPreQcTypeCode(String preQcTypeCode) {
        this.preQcTypeCode = preQcTypeCode;
    }

    public void setQcTypeLevel(String qcTypeLevel) {
        this.qcTypeLevel = qcTypeLevel;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setPreQcType(String preQcType) {
        this.preQcType = preQcType;
    }

    public void setEditTime(long editTime) {
        this.editTime = editTime;
    }

    public String getQcTypeCode() {
        return qcTypeCode;
    }

    public String getQcTypeName() {
        return qcTypeName;
    }

    public String getPreQcTypeCode() {
        return preQcTypeCode;
    }

    public String getQcTypeLevel() {
        return qcTypeLevel;
    }

    public long getCreateTime() {
        return createTime;
    }

    public Object getPreQcType() {
        return preQcType;
    }

    public long getEditTime() {
        return editTime;
    }

    public long getId(){
        return getBaseObjId();
    }
}
