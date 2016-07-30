package kxlive.gjrlibrary.http;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * Created by gjr on 2016/3/21.
 */
public class ImageInfo {
    /**
     * 参数名称
     */
    private String mName;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 文件的 mime，需要根据文档查询
     */
    private String mime;
    /**
     * 文件字节流
     */
    private byte[] value;
    /**
     * 图片文件
     */
    private Bitmap mBitmap ;

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public byte[] getValue() {
        if (value==null&&mBitmap!=null){
        ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos) ;
        return bos.toByteArray();
        }
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }
}
