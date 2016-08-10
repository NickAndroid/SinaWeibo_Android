package com.bpok.sina.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bpok.sina.R;
import com.bpok.sina.sdk.AccessTokenKeeper;
import com.bpok.sina.sdk.Constants;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;

/**
 * @author howard splash and do some mess work background
 * 
 */
public class Welcome extends Activity {

	/** 微博 Web 授权类，提供登陆等功能 */
	private WeiboAuth mWeiboAuth;

	/** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能 */
	private Oauth2AccessToken mAccessToken;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_welcome);
		initFontType();
		new CountDownTimer(1000, 1000) {
			@Override
			public void onTick(long arg0) {
			}

			@Override
			public void onFinish() {
				getLoginState();
			}
		}.start();

		// 初始化imageLoader
		initImageLoader(getApplicationContext());
	}

	private void initFontType() {
		TextView splash = (TextView) findViewById(R.id.splash_text);
		splash.setText("CopyRight@bpok 2014");
	}

	private void getLoginState() {

		Intent intent;
		// 创建微博实例
		mWeiboAuth = new WeiboAuth(this, com.bpok.sina.sdk.Constants.APP_KEY,
				Constants.REDIRECT_URL, Constants.SCOPE);

		// 从 SharedPreferences 中读取上次已保存好 AccessToken 等信息，
		// 第一次启动本应用，AccessToken 不可用
		mAccessToken = AccessTokenKeeper.readAccessToken(this);
		if (mAccessToken.isSessionValid()) {
			intent = new Intent(Welcome.this, Main.class);
		} else {
			intent = new Intent(Welcome.this, Login.class);
		}

		startActivity(intent);
		overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
		Welcome.this.finish();
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
}
