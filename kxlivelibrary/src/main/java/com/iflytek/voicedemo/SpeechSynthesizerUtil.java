package com.iflytek.voicedemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.speech.ApkInstaller;
import com.iflytek.speech.TtsSettings;

import kxlive.gjrlibrary.R;

/**
 * 讯飞语音合成工具类
 * 
 * @author gjr 2015-1-14
 */
public class SpeechSynthesizerUtil {
	private static String TAG = "TtsDemo";
	// 语音合成对象
	private SpeechSynthesizer mTts;

	// 默认发音人
	private String voicer = "xiaoyan";

	private String[] cloudVoicersEntries;
	private String[] cloudVoicersValue;

	// 缓冲进度
	private int mPercentForBuffering = 0;
	// 播放进度
	private int mPercentForPlaying = 0;
	// 引擎类型
	private String mEngineType = SpeechConstant.TYPE_CLOUD;
	// 语音+安装助手类
	ApkInstaller mInstaller;

	private Toast mToast;
	private SharedPreferences mSharedPreferences;
	private Context mContext;
	private Activity mActivity;
	// 判断云端合成
	private static final int TYPE_CLOUD = 0;
	// 判断本地合成
	private static final int TYPE_LOCAL = 1;
	// 判断本地和云端混合语音合成
	private static final int TYPE_MIX = 2;
	//判读当前合成类型的状态
	private  int TypeFlag=1;

	public SpeechSynthesizerUtil() {
		// TODO Auto-generated constructor stub
	}

	public SpeechSynthesizerUtil(Context context) {
		this.mContext = context;
	}

	public SpeechSynthesizerUtil(Context context, Activity activity) {
		this.mContext = context;
		this.mActivity = activity;
		init();
	}

	@SuppressLint("ShowToast")
	public void init() {

		// 初始化合成对象
		mTts = SpeechSynthesizer.createSynthesizer(mContext, mTtsInitListener);

		// 云端发音人名称列表
		cloudVoicersEntries = mContext.getResources().getStringArray(
				R.array.voicer_cloud_entries);
		cloudVoicersValue = mContext.getResources().getStringArray(
				R.array.voicer_cloud_values);

		mSharedPreferences = mContext.getSharedPreferences(
				TtsSettings.PREFER_NAME, Activity.MODE_PRIVATE);
		mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);

		mInstaller = new ApkInstaller(mActivity);
	}

	/**
	 * 设置语音合成的类型，0云端合成，1本地离线合成
	 */
	public void tts_change(int type) {
		TypeFlag=type;
		switch (type) {
		case TYPE_CLOUD:
			mEngineType = SpeechConstant.TYPE_CLOUD;
			break;
		case TYPE_LOCAL:
			mEngineType = SpeechConstant.TYPE_LOCAL;
			/**
			 * 选择本地合成 判断是否安装语音+,未安装则跳转到提示安装页面
			 */
            if (!SpeechUtility.getUtility().checkServiceInstalled()) {
				mInstaller.install();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 进入语音合成参数设置页面
	 */
	public void tts_set() {
		if (SpeechConstant.TYPE_CLOUD.equals(mEngineType)) {
			Intent intent = new Intent(mContext, TtsSettings.class);
			mActivity.startActivity(intent);
		} else {
			// 本地设置跳转到语音+中
			if (!SpeechUtility.getUtility().checkServiceInstalled()) {
				mInstaller.install();
			} else {
				SpeechUtility.getUtility().openEngineSettings(null);
			}
		}
	}

	/**
	 * 开始合成,实时显示合成缓冲进度，播放进度
	 */
	public void tts_play(String msg) {
		// 设置参数
		setParam();
		int code = mTts.startSpeaking(msg, mTtsListener);
		if (code != ErrorCode.SUCCESS) {
			if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
				// 未安装则跳转到提示安装页面
				mInstaller.install();
			} else {
				showTip("语音合成失败,错误码: " + code);
			}
		}
	}

	
	/**
	 * 暂停播放，操作播放器，保存当前播放进度信息
	 */
	public void tts_pause() {
		mTts.pauseSpeaking();
		showTip("暂停播放");
	}

	/**
	 * 继续播放，操作播放器，接上次的暂停任务继续，
	 */
	public void tts_resume() {
		mTts.resumeSpeaking();
		showTip("继续播放");
	}
	
	/**
	 * 暂停当前播放任务，取消播放任务，不保存当前状态，销毁资源
	 */
	public void tts_cancel() {
		mTts.stopSpeaking();
		showTip("取消合成播放");
	}
	
	/**
	 *  选择发音人
	 */
	public void tts_btn_person_select() {
		showPresonSelectDialog();
		showTip("取消合成播放");
	}

	private int selectedNum=0;
	/**
	 * 发音人选择。
	 */
	private void showPresonSelectDialog() {
		switch (TypeFlag) {
		// 选择在线合成
		case TYPE_CLOUD:			
			new AlertDialog.Builder(mActivity).setTitle("在线合成发音人选项")
			.setSingleChoiceItems(cloudVoicersEntries, // 单选框有几项,各是什么名字
					selectedNum, // 默认的选项
					new DialogInterface.OnClickListener() { // 点击单选框后的处理
				public void onClick(DialogInterface dialog,
						int which) { // 点击了哪一项
					voicer = cloudVoicersValue[which];
					if ("catherine".equals(voicer) || "henry".equals(voicer) || "vimary".equals(voicer)) {
						 ((EditText) mActivity.findViewById(R.id.tts_text)).setText(R.string.text_tts_source_en);
					}else {
						((EditText) mActivity.findViewById(R.id.tts_text)).setText(R.string.text_tts_source);
					}
					selectedNum = which;
					dialog.dismiss();
				}
			}).show();
			break;
			
		// 选择本地合成
		case TYPE_LOCAL:
			if (!SpeechUtility.getUtility().checkServiceInstalled()) {
				mInstaller.install();
			}else {
				SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_TTS);				
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 退出时释放连接
	 */
	public void onDestroy() {
		mTts.stopSpeaking();
		mTts.destroy();
	}

//	/**
//	 * 开启移动数据统计分析
//	 */
//	public void onStartData() {
//		FlowerCollector.onResume(mContext);
//		FlowerCollector.onPageStart("TtsDemo");
//	}
//
//	/**
//	 * 关闭移动数据统计分析
//	 */
//	public void onEndData() {
//		FlowerCollector.onPageEnd("TtsDemo");
//		FlowerCollector.onPause(mContext);
//	}
	
	/**
	 * 初期化监听。
	 */
	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			Log.d(TAG, "InitListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				showTip("初始化失败,错误码：" + code);
			}
		}
	};

	/**
	 * 合成回调监听。
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener() {
		@Override
		public void onSpeakBegin() {
			showTip("开始播放");
		}

		@Override
		public void onSpeakPaused() {
			showTip("暂停播放");
		}

		@Override
		public void onSpeakResumed() {
			showTip("继续播放");
		}

		@Override
		public void onBufferProgress(int percent, int beginPos, int endPos,
				String info) {
			mPercentForBuffering = percent;
			showTip(String.format(
					mContext.getString(R.string.tts_toast_format),
					mPercentForBuffering, mPercentForPlaying));
		}

		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
			mPercentForPlaying = percent;
			showTip(String.format(
					mContext.getString(R.string.tts_toast_format),
					mPercentForBuffering, mPercentForPlaying));
		}

		@Override
		public void onCompleted(SpeechError error) {
			if (error == null) {
				showTip("播放完成");
			} else if (error != null) {
				showTip(error.getPlainDescription(true));
			}
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

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
		mTts.setParameter(SpeechConstant.PARAMS, null);
		// 设置合成
		if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
			mTts.setParameter(SpeechConstant.ENGINE_TYPE,
					SpeechConstant.TYPE_CLOUD);
			// 设置发音人
			mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);

			// 设置语速
			mTts.setParameter(SpeechConstant.SPEED,
					mSharedPreferences.getString("speed_preference", "50"));

			// 设置音调
			mTts.setParameter(SpeechConstant.PITCH,
					mSharedPreferences.getString("pitch_preference", "50"));

			// 设置音量
			mTts.setParameter(SpeechConstant.VOLUME,
					mSharedPreferences.getString("volume_preference", "50"));

			// 设置播放器音频流类型
			mTts.setParameter(SpeechConstant.STREAM_TYPE,
					mSharedPreferences.getString("stream_preference", "3"));
		} else {
			mTts.setParameter(SpeechConstant.ENGINE_TYPE,
					SpeechConstant.TYPE_LOCAL);
			// 设置发音人 voicer为空默认通过语音+界面指定发音人。
			mTts.setParameter(SpeechConstant.VOICE_NAME, "");
		}
	}

}
