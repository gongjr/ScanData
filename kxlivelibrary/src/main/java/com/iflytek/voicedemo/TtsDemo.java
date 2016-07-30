package com.iflytek.voicedemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.iflytek.speech.TtsSettings;

import kxlive.gjrlibrary.R;

public class TtsDemo extends Activity implements OnClickListener {
	
	// 云端/本地选择按钮
	private RadioGroup mRadioGroup;
	
	private Toast mToast;
	private SharedPreferences mSharedPreferences;
	
	private SpeechSynthesizerUtil SynthesizerUtil;
	
	@SuppressLint("ShowToast")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ttsdemo);
		initLayout();
		SynthesizerUtil=new SpeechSynthesizerUtil(getApplicationContext(), this);
				
		mSharedPreferences = getSharedPreferences(TtsSettings.PREFER_NAME, Activity.MODE_PRIVATE);
		mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
	}

	/**
	 * 初始化Layout。
	 */
	private void initLayout() {
		findViewById(R.id.tts_play).setOnClickListener(this);
		
		findViewById(R.id.tts_cancel).setOnClickListener(this);
		findViewById(R.id.tts_pause).setOnClickListener(this);
		findViewById(R.id.tts_resume).setOnClickListener(this);
		findViewById(R.id.image_tts_set).setOnClickListener(this);

		findViewById(R.id.tts_btn_person_select);
		findViewById(R.id.tts_btn_person_select).setOnClickListener(this);
		
		mRadioGroup=((RadioGroup) findViewById(R.id.tts_rediogroup));
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.tts_radioCloud) {
                    SynthesizerUtil.tts_change(0);
                } else if (checkedId == R.id.tts_radioLocal)
                    SynthesizerUtil.tts_change(1);
				}
		} );
	}	

	@Override
	public void onClick(View view) {
        if(view.getId()==R.id.image_tts_set) {
                SynthesizerUtil.tts_set();
            }// 开始合成
		else if(view.getId()==R.id.tts_play) {
            String msg = ((EditText) findViewById(R.id.tts_text)).getText().toString();
            SynthesizerUtil.tts_play(msg);
        }// 取消合成
		else if(view.getId()==R.id.tts_cancel) {
            SynthesizerUtil.tts_cancel();
        }// 暂停播放
		else if(view.getId()==R.id.tts_pause) {
            SynthesizerUtil.tts_pause();
        }// 继续播放
		else if(view.getId()==R.id.tts_resume) {
            SynthesizerUtil.tts_resume();
        }// 选择发音人
		else if(view.getId()==R.id.tts_btn_person_select) {
            SynthesizerUtil.tts_btn_person_select();
        }
	}

	


	private void showTip(final String str)
	{
		mToast.setText(str);
		mToast.show();
	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 退出时释放连接
		SynthesizerUtil.onDestroy();
	}
	
//	@Override
//	protected void onResume() {
//		//移动数据统计分析
//		SynthesizerUtil.onStartData();
//		super.onResume();
//	}
//	
//	@Override
//	protected void onPause() {
//		//移动数据统计分析
//		SynthesizerUtil.onEndData();
//		super.onPause();
//	}
}
