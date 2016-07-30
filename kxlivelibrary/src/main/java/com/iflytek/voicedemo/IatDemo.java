package com.iflytek.voicedemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.speech.JsonParser;

import kxlive.gjrlibrary.R;

public class IatDemo extends Activity implements OnClickListener{
	private static String TAG = "IatDemo";
	// 听写结果内容
	private EditText mResultText;
	private Toast mToast;
	private SpeechRecognizerUtil RecognizerUtil;
	private Activity mActivity;
	
	@SuppressLint("ShowToast")
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.iatdemo);
		initLayout();
		mActivity=this;
		RecognizerUtil=new SpeechRecognizerUtil(getApplicationContext(),mActivity);
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);	
		mResultText = ((EditText)findViewById(R.id.iat_text));
	}

	/**
	 * 初始化Layout。
	 */
	private void initLayout(){
		findViewById(R.id.iat_recognize).setOnClickListener(this);
		findViewById(R.id.iat_upload_contacts).setOnClickListener(this);
		findViewById(R.id.iat_upload_userwords).setOnClickListener(this);	
		findViewById(R.id.iat_stop).setOnClickListener(this);
		findViewById(R.id.iat_cancel).setOnClickListener(this);
		findViewById(R.id.image_iat_set).setOnClickListener(this);
		//选择云端or本地
		RadioGroup group = (RadioGroup)this.findViewById(R.id.radioGroup);
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId==R.id.iatRadioCloud) {
                    RecognizerUtil.iat_change(0);
                    findViewById(R.id.iat_upload_contacts).setEnabled(true);
                    findViewById(R.id.iat_upload_userwords).setEnabled(true);
                }
				else if(checkedId==R.id.iatRadioLocal) {
                    RecognizerUtil.iat_change(1);
                    findViewById(R.id.iat_upload_contacts).setEnabled(false);
                    findViewById(R.id.iat_upload_userwords).setEnabled(false);
                }
                else if(checkedId==R.id.iatRadioMix) {
                    RecognizerUtil.iat_change(2);
                    findViewById(R.id.iat_upload_contacts).setEnabled(false);
                    findViewById(R.id.iat_upload_userwords).setEnabled(false);
                }
			}
		});
	}

	@Override
	public void onClick(View view) {
		// 进入参数设置页面
		if(view.getId()==R.id.image_iat_set) {
            RecognizerUtil.iat_set();
        }// 开始听写
		else if(view.getId()==R.id.iat_recognize) {
            mResultText.setText(null);// 清空显示内容
            RecognizerUtil.iat_recognize(recognizerDialogListener, recognizerListener);
        }// 停止听写
		else if(view.getId()==R.id.iat_stop) {
            RecognizerUtil.iat_stop();
        }// 取消听写
        else if(view.getId()==R.id.iat_cancel) {
            RecognizerUtil.iat_cancel();
        }// 上传联系人
        else if(view.getId()==R.id.iat_upload_contacts) {
            RecognizerUtil.iat_upload_contacts();
        }// 上传用户词表
        else if(view.getId()==R.id.iat_upload_userwords) {
            RecognizerUtil.iat_upload_userwords();
        }
	}

	
	/**
	 * 听写监听器。
	 */
	private RecognizerListener recognizerListener=new RecognizerListener(){

		@Override
		public void onBeginOfSpeech() {	
			showTip("开始说话");
		}


		@Override
		public void onError(SpeechError error) {
			showTip(error.getPlainDescription(true));
		}

		@Override
		public void onEndOfSpeech() {
			showTip("结束说话");
		}



		@Override
		public void onResult(RecognizerResult results, boolean isLast) {		
			Log.d(TAG, results.getResultString());
			String text = JsonParser.parseIatResult(results.getResultString());
			mResultText.append(text);
			mResultText.setSelection(mResultText.length());
			if(isLast) {
				//TODO 最后的结果
			}
		}

		@Override
		public void onVolumeChanged(int volume) {
			showTip("当前正在说话，音量大小：" + volume);
		}


		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
		}
	};

	/**
	 * 听写UI监听器
	 */
	private RecognizerDialogListener recognizerDialogListener=new RecognizerDialogListener(){
		public void onResult(RecognizerResult results, boolean isLast) {
			String text = JsonParser.parseIatResult(results.getResultString());
			mResultText.append(text);
			mResultText.setSelection(mResultText.length());
		}

		/**
		 * 识别回调错误.
		 */
		public void onError(SpeechError error) {
			showTip(error.getPlainDescription(true));
		}

	};
	
	private void showTip(final String str)
	{
		mToast.setText(str);
		mToast.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecognizerUtil.onDestroy();
		// 退出时释放连接
		
	}
	
//	@Override
//	protected void onResume() {
//		//移动数据统计分析
//		RecognizerUtil.onStartData();
//		super.onResume();
//	}
//	@Override
//	protected void onPause() {
//		//移动数据统计分析
//		RecognizerUtil.onEndData();
//		super.onPause();
//	}
}
