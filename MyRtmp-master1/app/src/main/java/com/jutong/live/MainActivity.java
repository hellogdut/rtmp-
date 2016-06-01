package com.jutong.live;

import android.annotation.TargetApi;
import android.app.Activity;
import android.hardware.Camera.CameraInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.text.TextWatcher;
import android.text.Editable;

import com.example.myrtmp.R;

public class MainActivity extends Activity implements OnClickListener,
		Callback, LiveStateChangeListener {

	private Button button01;
	private EditText text01;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private boolean isStart;
	private LivePusher livePusher;
	private String url = "rtmp://192.168.1.140/live1/room1";
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {

			case -100:
				Toast.makeText(MainActivity.this, "视频预览开始失败", 0).show();
				livePusher.stopPusher();
				break;
			case -101:
				Toast.makeText(MainActivity.this, "音频录制失败", 0).show();
				livePusher.stopPusher();
				break;
			case -102:
				Toast.makeText(MainActivity.this, "音频编码器配置失败", 0).show();
				livePusher.stopPusher();
				break;
			case -103:
				Toast.makeText(MainActivity.this, "视频频编码器配置失败", 0).show();
				livePusher.stopPusher();
				break;
			case -104:
				Toast.makeText(MainActivity.this, "流媒体服务器/网络等问题", 0).show();
				livePusher.stopPusher();
				break;
			}
			button01.setText("推流");
			isStart = false;
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		button01 = (Button) findViewById(R.id.button_first);
		button01.setOnClickListener(this);
		findViewById(R.id.button_take).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						livePusher.switchCamera();
					}
				});
		text01 = (EditText) findViewById(R.id.editText_url);
		text01.setText(url);
		text01.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {
				url = s.toString();
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			public void onTextChanged(CharSequence s, int start, int before, int count){}
		});

		mSurfaceView = (SurfaceView) this.findViewById(R.id.surface);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		livePusher = new LivePusher(this, 1080,720, 1024000, 25,
				CameraInfo.CAMERA_FACING_BACK);
		livePusher.setLiveStateChangeListener(this);
		livePusher.prepare(mSurfaceHolder);

	}

	// @Override
	// public void onRequestPermissionsResult(int requestCode,
	// String[] permissions, int[] grantResults) {
	// super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	// }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		livePusher.relase();
	}

	@Override
	public void onClick(View v) {
		if (isStart) {
			button01.setText("推流");
			isStart = false;
			livePusher.stopPusher();
		} else {
			button01.setText("停止");
			isStart = true;
			livePusher.startPusher(url);// TODO: 设置流媒体服务器地址

		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		System.out.println("MAIN: CREATE");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		System.out.println("MAIN: CHANGE");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		System.out.println("MAIN: DESTORY");
	}

	/**
	 * 可能运行在子线程
	 */
	@Override
	public void onErrorPusher(int code) {
		System.out.println("code:" + code);
		mHandler.sendEmptyMessage(code);
	}

	/**
	 * 可能运行在子线程
	 */
	@Override
	public void onStartPusher() {
		Log.d("MainActivity", "开始推流");
	}

	/**
	 * 可能运行在子线程
	 */
	@Override
	public void onStopPusher() {
		Log.d("MainActivity", "结束推流");
	}

}
