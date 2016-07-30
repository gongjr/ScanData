package com.gjr.scandata.biz.bean;

/**
 * 商品信息
 * Created by gjr on 2016/7/20 10:03.
 * mail : gjr9596@gmail.com
 */
public class HttpGoodsInfo {

    /**
     * id : 10000039
     * qcName : 101
     * qcTypeCode : 10000031
     * qcCode : 101.101
     * qcStandard : 外观外观外观外观外观外观外观外观
     * picUrl : 10000042.jpg
     * state : 1
     * createTime : 1468571047000
     * editTime : 1468571047000
     * realPicUrl : http://www.cclovenn.com/baseFile/HjpQc/10000042.jpg
     */

    private String id;
    private String qcName;
    private String qcTypeCode;
    private String qcCode;
    private String qcStandard;
    private String picUrl;
    private String state;
    private long createTime;
    private long editTime;
    private String realPicUrl;

    public void setId(String id) {
        this.id = id;
    }

    public void setQcName(String qcName) {
        this.qcName = qcName;
    }

    public void setQcTypeCode(String qcTypeCode) {
        this.qcTypeCode = qcTypeCode;
    }

    public void setQcCode(String qcCode) {
        this.qcCode = qcCode;
    }

    public void setQcStandard(String qcStandard) {
        this.qcStandard = qcStandard;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setEditTime(long editTime) {
        this.editTime = editTime;
    }

    public void setRealPicUrl(String realPicUrl) {
        this.realPicUrl = realPicUrl;
    }

    public String getId() {
        return id;
    }

    public String getQcName() {
        return qcName;
    }

    public String getQcTypeCode() {
        return qcTypeCode;
    }

    public String getQcCode() {
        return qcCode;
    }

    public String getQcStandard() {
        return qcStandard;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public String getState() {
        return state;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getEditTime() {
        return editTime;
    }

    public String getRealPicUrl() {
        return realPicUrl;
    }

    public GoodsInfo ToGoodsInfo(){
        GoodsInfo lGoodsInfo=new GoodsInfo();
        lGoodsInfo.setGoodsInfoId(id);
        lGoodsInfo.setQcName(qcName);
        lGoodsInfo.setQcTypeCode(qcTypeCode);
        lGoodsInfo.setQcCode(qcCode);
        lGoodsInfo.setQcStandard(qcStandard);
        lGoodsInfo.setPicUrl(picUrl);
        lGoodsInfo.setState(state);
        lGoodsInfo.setCreateTime(createTime);
        lGoodsInfo.setEditTime(editTime);
        lGoodsInfo.setRealPicUrl(realPicUrl);
        return lGoodsInfo;
    }
}
