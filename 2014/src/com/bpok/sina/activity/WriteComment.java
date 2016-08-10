package com.bpok.sina.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.bpok.sina.R;
import com.bpok.sina.sdk.AccessTokenKeeper;
import com.bpok.sina.utils.ToastUtils;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;

public class WriteComment extends Activity {

	private ProgressBar pBar;
	private EditText eText;
	/** 当前 Token 信息 */
	private Oauth2AccessToken mAccessToken;
	/** 用于获取微博信息流等操作的API */
	private StatusesAPI mStatusesAPI;
	/** 用于获取评论列表的API */
	private CommentsAPI mCommentAPI;

	private Bitmap pic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);
		findViews();
		initToken();
		initActionBar();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.flip_horizontal_in,
				R.anim.flip_horizontal_out);
	}

	void findViews() {
		this.eText = (EditText) findViewById(R.id.et);
	}

	/**
	 * style actionBar and find views here
	 */
	private void initActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.color.white));
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(R.layout.custom_action_bar_comment);
		this.pBar = (ProgressBar) findViewById(R.id.pb_action_circle);
	}

	private void initToken() {
		// 获取当前已保存过的 Token
		mAccessToken = AccessTokenKeeper.readAccessToken(this);
		// 对statusAPI实例化
		mStatusesAPI = new StatusesAPI(mAccessToken);
	}

	/**
	 * 微博 OpenAPI 回调接口。
	 */
	private RequestListener mListener = new RequestListener() {
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				if (response.startsWith("{\"created_at\"")) {
					ToastUtils.showToast(getApplicationContext(), "发表成功");
					finish();
					overridePendingTransition(R.anim.flip_horizontal_in,
							R.anim.flip_horizontal_out);
				} else {
				}
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
			ErrorInfo info = ErrorInfo.parse(e.getMessage());
			Log.e("error_sina_weibo_exp", info.toString());
		}
	};

}
