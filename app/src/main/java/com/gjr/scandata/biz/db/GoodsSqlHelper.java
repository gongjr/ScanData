package com.gjr.scandata.biz.db;

import android.database.Cursor;

import com.gjr.scandata.biz.bean.GoodsInfo;
import com.gjr.scandata.biz.bean.GoodsType;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import kxlive.gjrlibrary.utils.KLog;

/**
 * Created by gjr on 2016/7/25 15:47.
 * mail : gjr9596@gmail.com
 */
public class GoodsSqlHelper {

    private static GoodsSqlHelper mGoodsSqlHelper;

    public  static GoodsSqlHelper getInstances(){
        if (mGoodsSqlHelper==null)mGoodsSqlHelper=new GoodsSqlHelper();
        return mGoodsSqlHelper;
    }
    /**
     * 根据qcTypeLevel获取对应目录等级的类型信息
     * @return List<GoodsType>
     */
    public List<GoodsType> sqlGoodsTypeByQcTypeLevel_1(){
        List<GoodsType> mCompList = DataBinder.binder.findWithWhere(GoodsType.class,"qctypelevel = ?","1");
        return mCompList;
    }

    /**
     * 根据一级目录的id查询对应的二级目录
     * @param id
     * @return List<GoodsType>
     */
    public List<GoodsType> sqlGoodsTypeByPreQcTypeCode(String id){
        KLog.i("id:"+id);
        List<GoodsType> mCompList = DataBinder.binder.findWithWhere(GoodsType.class,"preqctypecode = ?",id);
        KLog.i("size:"+mCompList.size());

        return mCompList;
    }

    /**
     * 根据二级目录的id查询对应的商品信息
     * @param id
     * @return List<GoodsInfo>
     */
    public List<GoodsInfo> sqlGoodsInfoByQcTypeCode(String id){
        List<GoodsInfo> mCompList = DataBinder.binder.findWithWhere(GoodsInfo.class,"qctypecode = ?",id);
        return mCompList;
    }

    /**
     * 根据商品qcCode查询对应商品详情
     * @param qcCode
     * @return List<GoodsInfo>
     */
    public List<GoodsInfo> sqlGoodsInfoByQcCode(String qcCode){
        List<GoodsInfo> mCompList = DataBinder.binder.findWithWhere(GoodsInfo.class,"qccode = ?",qcCode);
        return mCompList;
    }

    /**
     * 根据所有商品
     * @return List<GoodsInfo>
     */
    public List<GoodsInfo> sqlGoodsInfoALL(){
        List<GoodsInfo> mCompList = DataBinder.binder.findAll(GoodsInfo.class);
        return mCompList;
    }

    /**
     * 根据商品id查询对应商品详情
     * @param goodsTypeId
     * @return List<GoodsType>
     */
    public List<GoodsType> sqlGoodsTypeByGoodsTypeId(String goodsTypeId){
        List<GoodsType> mCompList = DataBinder.binder.findWithWhere(GoodsType.class,"goodstypeid = ?",goodsTypeId);
        return mCompList;
    }

    /**
     * 更新商品id对应商品类型
     * @param pGoodsType
     * @param id
     * @return size
     */
    public int sqlUpdateGoodsType(GoodsType pGoodsType,long id){
        int size = DataBinder.binder.update(pGoodsType,id);
        return size;
    }

    /**
     * 根据id查询详情
     * @param goodsInfoId
     * @return List<GoodsInfo>
     */
    public List<GoodsInfo> sqlGoodsInfoByGoodsInfoId(String goodsInfoId){
        List<GoodsInfo> mCompList = DataBinder.binder.findWithWhere(GoodsInfo.class,"goodsinfoid = ?",goodsInfoId);
        return mCompList;
    }

    /**
     * 更新商品id对应商品详情
     * @param pGoodsInfo
     * @param id
     * @return size
     */
    public int sqlUpdateGoodsInfo(GoodsInfo pGoodsInfo,long id){
        int size = DataBinder.binder.update(pGoodsInfo,id);
        return size;
    }

    /**
     * 根据商品关键字,查询相关信息
     * @param Key
     * @return List<GoodsInfo>
     */
    public List<GoodsInfo> sqlGoodsInfoByKey(String Key){
        List<GoodsInfo> mCompList = new ArrayList<GoodsInfo>();
        Cursor mCursor=null;
        mCursor = DataSupport.findBySQL("select distinct * from goodsinfo "
                + " where qcname like '%" + Key + "%'"
                + " or " + "qccode like '%" + Key + "%'");

        if(mCursor!=null){
            while(mCursor.moveToNext()){
                GoodsInfo lGoodsInfo = new GoodsInfo();
                int idx_goodsInfoId = mCursor.getColumnIndex("goodsinfoid");
                int idx_qcName = mCursor.getColumnIndex("qcname");
                int idx_qcTypeCode = mCursor.getColumnIndex("qctypecode");
                int idx_qcCode = mCursor.getColumnIndex("qccode");
                int idx_qcStandard = mCursor.getColumnIndex("qcstandard");
                int idx_picUrl = mCursor.getColumnIndex("picurl");
                int idx_state = mCursor.getColumnIndex("state");
                int idx_createTime = mCursor.getColumnIndex("createtime");
                int idx_editTime = mCursor.getColumnIndex("edittime");
                int idx_realPicUrl = mCursor.getColumnIndex("realpicurl");

                if(idx_goodsInfoId!=-1)
                    lGoodsInfo.setGoodsInfoId(mCursor.getString(idx_goodsInfoId));
                if(idx_qcName!=-1)
                    lGoodsInfo.setQcName(mCursor.getString(idx_qcName));
                if(idx_qcTypeCode!=-1)
                    lGoodsInfo.setQcTypeCode(mCursor.getString(idx_qcTypeCode));
                if(idx_qcCode!=-1)
                    lGoodsInfo.setQcCode(mCursor.getString(idx_qcCode));
                if(idx_qcStandard!=-1)
                    lGoodsInfo.setQcStandard(mCursor.getString(idx_qcStandard));
                if(idx_picUrl!=-1)
                    lGoodsInfo.setPicUrl(mCursor.getString(idx_picUrl));
                if(idx_state!=-1)
                    lGoodsInfo.setState(mCursor.getString(idx_state));
                if(idx_createTime!=-1)
                    lGoodsInfo.setCreateTime(mCursor.getLong(idx_createTime));
                if(idx_editTime!=-1)
                    lGoodsInfo.setEditTime(mCursor.getLong(idx_editTime));
                if(idx_realPicUrl!=-1)
                    lGoodsInfo.setRealPicUrl(mCursor.getString(idx_realPicUrl));

                mCompList.add(lGoodsInfo);
            }
            mCursor.close();
        }
        return mCompList;
    }
}