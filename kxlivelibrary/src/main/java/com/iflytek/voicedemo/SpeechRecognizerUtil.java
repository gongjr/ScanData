package com.iflytek.voicedemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.cloud.util.ContactManager;
import com.iflytek.cloud.util.ContactManager.ContactListener;
import com.iflytek.speech.ApkInstaller;
import com.iflytek.speech.FucUtil;
import com.iflytek.speech.IatSettings;

import kxlive.gjrlibrary.R;

/**
 * 讯飞语音识别工具类
 * 声明，本工具类需在Activity页面下，显示调用，需有比较明确页面交互响应。
 * @author gjr 2015-1-14
 */
public class SpeechRecognizerUtil {
	private Context mContext;
	// 标签
	private static String TAG = "IatDemo";
	// 语音听写对象
	private SpeechRecognizer mIat;
	// 语音听写UI
	private RecognizerDialog iatDialog;
	// 听写结果内容
	private EditText mResultText;

	private Toast mToast;
	private SharedPreferences mSharedPreferences;
	// 引擎类型
	private String mEngineType = SpeechConstant.TYPE_CLOUD;
	// 语音+安装助手类
	private ApkInstaller mInstaller;
	// 函数调用返回值
	private int ret = 0;
	//当前设置是否云端识别
	private boolean isCloud;
	//判断云端识别
	private static final int TYPE_CLOUD=0;
	//判断本地识别
	private static final int TYPE_LOCAL=1;
	//判断本地和云端混合识别
	private static final int TYPE_MIX=2;
	private Activity mActivity;
	@SuppressLint("ShowToast")
	public SpeechRecognizerUtil() {
		// TODO Auto-generated constructor stub
	}

	public SpeechRecognizerUtil(Context context) {
		this.mContext = context;
		init() ;
	}
	
	public SpeechRecognizerUtil(Context context,Activity activity) {
		this.mContext = context;
		this.mActivity =activity;
		init() ;
	}


	@SuppressLint("ShowToast")
	private void init() {
		// 初始化识别对象
		mIat = SpeechRecognizer.createRecognizer(mContext, mInitListener);
		// 初始化听写Dialog,如果只使用有UI听写功能,无需创建SpeechRecognizer
		iatDialog = new RecognizerDialog(mActivity, mInitListener);
		mSharedPreferences = mContext.getSharedPreferences(
				IatSettings.PREFER_NAME, Activity.MODE_PRIVATE);
		mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
		mInstaller = new ApkInstaller(mActivity);
	}

	/**
	 * 设置语音设别的类型，
	 */
	public void iat_change(int type) {

		switch (type) {
		case TYPE_CLOUD:
			mEngineType = SpeechConstant.TYPE_CLOUD;
			isCloud = true;
			break;
		case TYPE_LOCAL:
			mEngineType = SpeechConstant.TYPE_LOCAL;
			isCloud = false;
			/**
			 * 选择本地听写 判断是否安装语音+,未安装则跳转到提示安装页面
			 */
			if (!SpeechUtility.getUtility().checkServiceInstalled()) {
				mInstaller.install();
			} else {
				String result = FucUtil.checkLocalResource();
				if (!TextUtils.isEmpty(result)) {
					showTip(result);
				}
			}
			break;
		case TYPE_MIX:
			mEngineType = SpeechConstant.TYPE_MIX;
			isCloud = false;
			/**
			 * 选择本地听写 判断是否安装语音+,未安装则跳转到提示安装页面
			 */
			if (!SpeechUtility.getUtility().checkServiceInstalled()) {
				mInstaller.install();
			} else {
				String result = FucUtil.checkLocalResource();
				if (!TextUtils.isEmpty(result)) {
					showTip(result);
				}
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 进入语音识别参数设置页面
	 */
	public void iat_set() {
		Intent intents = new Intent(mContext, IatSettings.class);
		mActivity.startActivity(intents);
	}

	/**
	 * 开始听写
	 */
	public void iat_recognize(RecognizerDialogListener recognizerDialogListener, RecognizerListener recognizerListener) {
		// 设置参数
		setParam();
		boolean isShowDialog = mSharedPreferences.getBoolean(mContext
				.getResources().getString(R.string.pref_key_iat_show), true);
		if (isShowDialog) {
			// 显示听写对话框
			iatDialog.setListener(recognizerDialogListener);
			iatDialog.show();
			showTip(mContext.getResources().getString(R.string.text_begin));
		} else {
			// 不显示听写对话框
			ret = mIat.startListening(recognizerListener);
			if (ret != ErrorCode.SUCCESS) {
				showTip("听写失败,错误码：" + ret);
			} else {
				showTip(mContext.getResources().getString(R.string.text_begin));
			}
		}
	}

	/**
	 * 停止听写
	 */
	public void iat_stop() {
		mIat.stopListening();
		showTip("停止听写");
	}

	/**
	 * 取消听写
	 */
	public void iat_cancel() {
		mIat.cancel();
		showTip("取消听写");
	}

	/**
	 * 退出时释放连接
	 */
	public void onDestroy() {
		mIat.cancel();
		mIat.destroy();
	}

//	/**
//	 * 开启移动数据统计分析
//	 */
//	public void onStartData() {
//		FlowerCollector.onResume(mContext);
//		FlowerCollector.onPageStart("IatDemo");
//	}
//
//	/**
//	 * 关闭移动数据统计分析
//	 */
//	public void onEndData() {
//		FlowerCollector.onPageEnd("IatDemo");
//		FlowerCollector.onPause(mContext);
//	}

	/**
	 * 上传联系人
	 */
	public void iat_upload_contacts() {
		if (isCloud) {
			Toast.makeText(
					mContext,
					mContext.getResources().getString(
							R.string.text_upload_contacts), Toast.LENGTH_SHORT)
					.show();
			ContactManager mgr = ContactManager.createManager(mContext,
					mContactListener);
			mgr.asyncQueryAllContactsName();
		} else {
			showTip("您的语音识别设置离线听写，上传请修改设置，进行云端设别");
		}
	}

	/**
	 * 上传用户词表
	 */
	public void iat_upload_userwords() {
		if (isCloud) {
			Toast.makeText(
					mContext,
					mContext.getResources().getString(
							R.string.text_upload_userwords), Toast.LENGTH_SHORT)
					.show();
			String contents = FucUtil.readFile(mContext, "userwords", "utf-8");
			// 指定引擎类型
			mIat.setParameter(SpeechConstant.ENGINE_TYPE,
					SpeechConstant.TYPE_CLOUD);
			mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
			ret = mIat.updateLexicon("userword", contents, lexiconListener);
			if (ret != ErrorCode.SUCCESS)
				showTip("上传热词失败,错误码：" + ret);
		} else {
			showTip("您的语音识别设置离线听写，上传请修改设置，进行云端设别");
		}
	}

	/**
	 * 初始化监听器。
	 */
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d(TAG, "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				showTip("初始化失败,错误码：" + code);
			}
		}
	};

	/**
	 * 上传联系人/词表监听器。
	 */
	private LexiconListener lexiconListener = new LexiconListener() {

		@Override
		public void onLexiconUpdated(String lexiconId, SpeechError error) {
			if (error != null) {
				showTip(error.toString());
			} else {
				showTip(mContext.getResources().getString(
						R.string.text_upload_success));
			}
		}
	};



	/**
	 * 获取联系人监听器。
	 */
	private ContactListener mContactListener = new ContactListener() {
		@Override
		public void onContactQueryFinish(String contactInfos, boolean changeFlag) {
			// 注：实际应用中除第一次上传之外，之后应该通过changeFlag判断是否需要上传，否则会造成不必要的流量.
			// if(changeFlag) {
			// 指定引擎类型
			mIat.setParameter(SpeechConstant.ENGINE_TYPE,
					SpeechConstant.TYPE_CLOUD);
			mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
			ret = mIat.updateLexicon("contact", contactInfos, lexiconListener);
			if (ret != ErrorCode.SUCCESS)
				showTip("上传联系人失败：" + ret);
			// }
		}

	};

	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}

	/**
	 * 参数设置
	 * @return
	 */
	private void setParam() {
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);

		// 设置听写引擎
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
		// 设置返回结果格式
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

		String lag = mSharedPreferences.getString("iat_language_preference",
				"mandarin");
		if (lag.equals("en_us")) {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
		} else {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mIat.setParameter(SpeechConstant.ACCENT, lag);
		}
		// 设置语音前端点
		mIat.setParameter(SpeechConstant.VAD_BOS,
				mSharedPreferences.getString("iat_vadbos_preference", "4000"));
		// 设置语音后端点
		mIat.setParameter(SpeechConstant.VAD_EOS,
				mSharedPreferences.getString("iat_vadeos_preference", "1000"));
		// 设置标点符号
		mIat.setParameter(SpeechConstant.ASR_PTT,
				mSharedPreferences.getString("iat_punc_preference", "1"));
		// 设置音频保存路径
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
				Environment.getExternalStorageDirectory()
						+ "/iflytek/wavaudio.pcm");
	}
}
