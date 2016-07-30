package kxlive.gjrlibrary.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.inputmethod.InputMethodManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * 系统工具
 * 2015年5月11日
 */
public class Tools {

	private static final String TAG = Tools.class.getName();
	
	/**
	 * 判断有没有sd卡
	 * @return
	 */
	public  boolean hasSDCard() {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}
		return true;
	}

	/**
	 * 获得根目录路径
	 * @return
	 */
	public String getRootFilePath() {
		if (hasSDCard()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath()
					+ "/";// filePath:/sdcard/
		} else {
			return Environment.getDataDirectory().getAbsolutePath() + "/data/"; // filePath:
																				// /data/data/
		}
	}

	/**
	 * 将定义的view装换成 bitmap格式
	 * @param view
	 * @param start_location
	 * @return
	 */
	public Bitmap convertViewToBitmap(View view,int[] start_location) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//		int[] start_location = new int[2];
//		view.getLocationInWindow(start_location);
		Log.i("location-xy", "layout--x:" + start_location[0] + "  y:" + start_location[1]);
		int r=start_location[0]+view.getMeasuredWidth();
		int b=start_location[1]+view.getMeasuredHeight();
		Log.i("location-xy", "layout--start_location[0]+view.getMeasuredWidth():" + r + "  start_location[1]+view.getMeasuredHeight():" + b);
//		view.layout(start_location[0], start_location[1], r, b);
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}

	/**
	 * 将px值转换为dip或dp值，保证尺寸大小不变
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 * 
	 * @param dipValue
	 * 
	 * @param scale
	 * 
	 * @return
	 */

	public int dip2px(Context context, float dipValue) {

		final float scale = context.getResources().getDisplayMetrics().density;

		return (int) (dipValue * scale + 0.5f);

	}

	/**
	 * 
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * 
	 * @param fontScale
	 * 
	 * @return
	 */

	public int px2sp(Context context, float pxValue) {

		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;

		return (int) (pxValue / fontScale + 0.5f);

	}

	/**
	 * 
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param spValue
	 * 
	 * @param fontScale
	 * 
	 * @return
	 */

	public int sp2px(Context context, float spValue) {

		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;

		return (int) (spValue * fontScale + 0.5f);

	}

	public  boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD;
	}

	public  boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB;
	}

	/**
	 * 检查是否存在SDCard
	 * 
	 * @return
	 */
	public  boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	private  final int STROKE_WIDTH = 4;

	/**
	 * 从assets资源中获取图片
	 * @param context
	 * @param filename
	 * @return
	 */
	public Bitmap getBitmap(Context context, String filename) {
		Bitmap image = null;
		AssetManager am = context.getResources().getAssets();
		try {

			InputStream is = am.open(filename);
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	/**
	 * @param context
	 * @param filename
	 * @return
	 */
	public Bitmap toRoundBitmap(Context context, String filename) {
		Bitmap bitmap = getBitmap(context, filename);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			left = 0;
			bottom = width;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(4);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);

		// 画白色圆圈
		paint.reset();
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(STROKE_WIDTH);
		paint.setAntiAlias(true);
		canvas.drawCircle(width / 2, width / 2, width / 2 - STROKE_WIDTH / 2,
				paint);
		return output;
	}
	
	/**
	 * 用来判断服务是否运行.
	 * @param context
	 * @param className 判断的服务名字
	 * @return true在运行  false没有运行
	 */
	public static boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30);
		if (!(serviceList.size() > 0)) {
			mContext = null;
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			Logger.d(TAG, "className: " + serviceList.get(i).service.getClassName());
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				mContext = null;
				isRunning = true;
				break;
			}
		}
		mContext = null;
		return isRunning;
	}
	
	/**
	 * 判断网络是否可用
	 * @param mContext
	 * @return
	 */
	public static boolean isNetworkAvailable(Context mContext) {
		ConnectivityManager connect = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connect == null) {
			mContext = null;
			return false;
		} else{
			NetworkInfo[] info = connect.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						mContext = null;
						return true;
					}
				}
			}
		}
		mContext = null;
		return false;
	}
	
	/**
	 * 检测某Activity是否在当前Task的栈顶
	 */
	public static boolean isTopActivy(String cmdName, Context context) {
		Log.d(TAG, "cmdName: " + cmdName);
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
		String cmpNameTemp = null;
		if (null != runningTaskInfos) {
			cmpNameTemp = runningTaskInfos.get(0).topActivity.toString();
			Log.d(TAG, "topAct: " + cmpNameTemp);
		}
		if (null == cmpNameTemp){
			return false;
		}
	    Log.d(TAG, "cmpNameTemp.equals(cmdName): " + cmpNameTemp.equals(cmdName));
	    context = null;
	    
		return cmpNameTemp.equals(cmdName);
	}
	
	/**
	 * 隐藏软键盘
	 * @param mContext
	 */
	public void hideInputMethod(Activity act){
		((InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE))
		.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		act = null;
	}
	
	/**
	 * 显示软键盘
	 * @param mContext
	 */
	public void showInputMethod(Activity act){
		((InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE))
		.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED);
		act = null;
	}

	/**
	 * 显示输入法
	 * @param v
	 */
	public void showInputMethod(View v, Context mContext){
		InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
	}
	
	/**
	 * 根据给定的字符串，编码返回二维码
	 * @param codeStr
	 * @return
	 */
	public static Bitmap qcodeZxingDecode(String codeStr){
		int width = 200, height = 200;
		try {
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			BitMatrix bitMatrix = new QRCodeWriter().encode(codeStr, BarcodeFormat.QR_CODE,
					width, height, hints);
			int[] pixels = new int[width * height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * width + x] = 0xff000000;
					} else {
						pixels[y * width + x] = 0xffffffff;
					}
				}
			}

			Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 初始化二维码hint对象
	 * @return
	 */
	public static Map<DecodeHintType,Object> qcodeInitDecode(){
		Map<DecodeHintType,Object> hints = new EnumMap<DecodeHintType,Object>(DecodeHintType.class);
		Collection<BarcodeFormat> decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
		decodeFormats.addAll(EnumSet.of(BarcodeFormat.QR_CODE));
		hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
		hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
		return hints;
	}

	/**
	 * 加载图片 kxlive_official.png
	 * @param fileName
	 * @param context
	 * @return
	 * @throws java.io.IOException
	 */
	public static BinaryBitmap qcodeLoadImage(String fileName, Context context) throws IOException {
	    Bitmap bitmap = BitmapFactory.decodeStream(context.getResources().getAssets().open(fileName));
	    int lWidth = bitmap.getWidth();
	    int lHeight = bitmap.getHeight();
	    int[] lPixels = new int[lWidth * lHeight];
	    bitmap.getPixels(lPixels, 0, lWidth, 0, 0, lWidth, lHeight);
	    return new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(lWidth, lHeight, lPixels)));
	}

	/**
	 * 加载图片 kxlive_official.png
	 * @param fileName
	 * @param context
	 * @return
	 * @throws java.io.IOException
	 */
	public static BinaryBitmap qcodeLoadImage(Bitmap bitmap, Context context) throws IOException {
	    int lWidth = bitmap.getWidth();
	    int lHeight = bitmap.getHeight();
	    int[] lPixels = new int[lWidth * lHeight];
	    bitmap.getPixels(lPixels, 0, lWidth, 0, 0, lWidth, lHeight);
	    return new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(lWidth, lHeight, lPixels)));
	}

	/**
	 * 根据图片的资源ID解析
	 * @param fileName
	 * @param mContext
	 * @return
	 */
	private static BinaryBitmap qcodeLoadImage(int resId, Context mContext) throws IOException {
	    Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resId);
	    int lWidth = bitmap.getWidth();
	    int lHeight = bitmap.getHeight();
	    int[] lPixels = new int[lWidth * lHeight];
	    bitmap.getPixels(lPixels, 0, lWidth, 0, 0, lWidth, lHeight);
	    return new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(lWidth, lHeight, lPixels)));
	}

	/**
	 * 根据路径解析图片
	 */
	public static String qcodeDecodeProccess(String fileName, Context mContext){
		Result lResult = null;
		try {
			lResult = new MultiFormatReader().decode(qcodeLoadImage(fileName, mContext), qcodeInitDecode());
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(lResult==null){
			return null;
		}

		return  lResult.getText();
	}

	/**
	 * 根据资源id解析图片
	 */
	public static String qcodeDecodeProccess(int resId, Context mContext){
		Result lResult = null;
		try {
			lResult = new MultiFormatReader().decode(qcodeLoadImage(resId, mContext), qcodeInitDecode());
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(lResult==null){
			return null;
		}

		return  lResult.getText();
	}

	/**
	 * 根据Bitmap对象解析二维码图片
	 */
	public static String qcodeDecodeProccess(Bitmap bitmap, Context mContext){
		Result lResult = null;
		try {
			lResult = new MultiFormatReader().decode(qcodeLoadImage(bitmap, mContext), qcodeInitDecode());
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(lResult==null){
			return null;
		}

		return  lResult.getText();
	}

	/**
	 * 根据给定的字符串， 生成二维码
	 * @param urlStr
	 * @return
	 */
	public static Bitmap createBitmap(String urlStr){
		int width = 200, height = 200;
		try {
			if (urlStr.length() < 1) {
				return null;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			BitMatrix bitMatrix = new QRCodeWriter().encode(urlStr, BarcodeFormat.QR_CODE,
					width, height, hints);
			int[] pixels = new int[width * height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * width + x] = 0xff000000;
					} else {
						pixels[y * width + x] = 0xffffffff;
					}
				}
			}

			Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			
			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();
		}
		
		return null;
	} 
	
	/**
	 * 声称随机数作为手机号
	 */
	private static final int RandomPhoneLength = 9;
	public static String makeRandomPhone(){
		String rPhone = "";
	    for(int i=0; i<RandomPhoneLength; i++){
	    	int x=(int)(Math.random()*9);
	    	rPhone = rPhone + x;
	    }	
	    return rPhone;
	}
	
	public static String getVersionName(Context context) {
	    return getPackageInfo(context).versionName;
	}
	 
	//版本号
	public static int getVersionCode(Context context) {
	    return getPackageInfo(context).versionCode;
	}
	 
	private static PackageInfo getPackageInfo(Context context) {
	    PackageInfo pi = null;
	 
	    try {
	        PackageManager pm = context.getPackageManager();
	        pi = pm.getPackageInfo(context.getPackageName(),
	                PackageManager.GET_CONFIGURATIONS);
	 
	        return pi;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	 
	    return pi;
	}
}
