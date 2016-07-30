package com.gjr.scandata.biz.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 商品信息
 * Created by gjr on 2016/7/20 10:03.
 * mail : gjr9596@gmail.com
 */
public class GoodsInfo extends DataSupport implements Serializable,Parcelable {

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
    private static final long serialVersionUID = 1L;
    private String goodsInfoId;
    private String qcName;
    private String qcTypeCode;
    private String qcCode;
    private String qcStandard;
    private String picUrl;
    private String state;
    private long createTime;
    private long editTime;
    private String realPicUrl;

    public GoodsInfo() {
    }

    protected GoodsInfo(Parcel in) {
        goodsInfoId = in.readString();
        qcName = in.readString();
        qcTypeCode = in.readString();
        qcCode = in.readString();
        qcStandard = in.readString();
        picUrl = in.readString();
        state = in.readString();
        createTime = in.readLong();
        editTime = in.readLong();
        realPicUrl = in.readString();
    }

    public static final Creator<GoodsInfo> CREATOR = new Creator<GoodsInfo>() {
        @Override
        public GoodsInfo createFromParcel(Parcel in) {
            return new GoodsInfo(in);
        }

        @Override
        public GoodsInfo[] newArray(int size) {
            return new GoodsInfo[size];
        }
    };

    public String getGoodsInfoId() {
        return goodsInfoId;
    }

    public void setGoodsInfoId(String pGoodsInfoId) {
        goodsInfoId = pGoodsInfoId;
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

    public long getId(){
        return getBaseObjId();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(goodsInfoId);
        dest.writeString(qcName);
        dest.writeString(qcTypeCode);
        dest.writeString(qcCode);
        dest.writeString(qcStandard);
        dest.writeString(picUrl);
        dest.writeString(state);
        dest.writeLong(createTime);
        dest.writeLong(editTime);
        dest.writeString(realPicUrl);
    }
}
