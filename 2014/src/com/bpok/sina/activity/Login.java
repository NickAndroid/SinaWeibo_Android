package com.bpok.sina.activity;

import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.bpok.sina.R;
import com.bpok.sina.sdk.AccessTokenKeeper;
import com.bpok.sina.sdk.Constants;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.widget.LoginButton;

/**
 * @author bpok login activity of this app
 * 
 */
public class Login extends Activity {

	private LoginButton loginButton;

	/** 微博 Web 授权类，提供登陆等功能 */
	private WeiboAuth mWeiboAuth;

	/** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能 */
	private Oauth2AccessToken mAccessToken;

	/** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
	private SsoHandler mSsoHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		initLoginViews();
	}

	private void initLoginViews() {
		TextView hintView = (TextView) findViewById(R.id.token_hint);
		hintView.setMovementMethod(new ScrollingMovementMethod());

		// 创建微博实例
		mWeiboAuth = new WeiboAuth(this, com.bpok.sina.sdk.Constants.APP_KEY,
				Constants.REDIRECT_URL, Constants.SCOPE);

		// 从 SharedPreferences 中读取上次已保存好 AccessToken 等信息，
		// 第一次启动本应用，AccessToken 不可用
		mAccessToken = AccessTokenKeeper.readAccessToken(this);
		if (mAccessToken.isSessionValid()) {
			updateTokenView(true);
		}

		loginButton = (LoginButton) findViewById(R.id.btn_login);
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mSsoHandler = new SsoHandler(Login.this, mWeiboAuth);
				mSsoHandler.authorize(new AuthListener());
			}
		});
	}

	/**
	 * 当 SSO 授权 Activity 退出时，该函数被调用。
	 * 
	 * @see {@link Activity#onActivityResult}
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// SSO 授权回调
		// 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	/**
	 * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用
	 * {@link SsoHandler#authorizeCallBack} 后， 该回调才会被执行。 2. 非 SSO
	 * 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
	 * SharedPreferences 中。
	 */
	class AuthListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			// 从 Bundle 中解析 Token
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (mAccessToken.isSessionValid()) {
				// 显示 Token
				updateTokenView(false);

				// 保存 Token 到 SharedPreferences
				AccessTokenKeeper.writeAccessToken(Login.this, mAccessToken);
				Toast.makeText(Login.this,
						R.string.weibosdk_demo_toast_auth_success,
						Toast.LENGTH_SHORT).show();
				final Intent intent = new Intent(Login.this, Main.class);
				startActivity(intent);
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				finish();

			} else {
				// 以下几种情况，您会收到 Code：
				// 1. 当您未在平台上注册的应用程序的包名与签名时；
				// 2. 当您注册的应用程序包名与签名不正确时；
				// 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
				String code = values.getString("code");
				String message = getString(R.string.weibosdk_demo_toast_auth_failed);
				if (!TextUtils.isEmpty(code)) {
					message = message + "\nObtained the code: " + code;
				}
				Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onCancel() {
			Toast.makeText(Login.this,
					R.string.weibosdk_demo_toast_auth_canceled,
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(Login.this, "Auth exception : " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 显示当前 Token 信息。
	 * 
	 * @param hasExisted
	 *            配置文件中是否已存在 token 信息并且合法
	 */
	@SuppressLint("SimpleDateFormat")
	private void updateTokenView(boolean hasExisted) {
		String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
				.format(new java.util.Date(mAccessToken.getExpiresTime()));
		String format = getString(R.string.weibosdk_demo_token_to_string_format_1);
		Log.i("Login.class",
				String.format(format, mAccessToken.getToken(), date));
		String message = String.format(format, mAccessToken.getToken(), date);
		if (hasExisted) {
			message = getString(R.string.weibosdk_demo_token_has_existed)
					+ "\n" + message;
		}
		Log.i("Login.class", message);
	}

}
