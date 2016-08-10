package com.bpok.sina.loaders;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.bpok.sina.sdk.AccessTokenKeeper;
import com.bpok.sina.utils.ConfigManager;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.utils.LogUtil;

public class UserInfoASyncTask extends AsyncTask<Void, Void, String> {

	private String TAG = "UserInfoASyncTask.class";

	/** 当前 Token 信息 */
	private Oauth2AccessToken mAccessToken;
	/** 用户信息接口 */
	private UsersAPI mUsersAPI;

	private Context mContext;

	private UserInfoReadyImpl userInfoReadyImpl;

	public UserInfoASyncTask(Context context,
			UserInfoReadyImpl userInfoReadyImpl) {
		super();
		this.mContext = context;
		this.userInfoReadyImpl = userInfoReadyImpl;
		// 获取当前已保存过的 Token
		mAccessToken = AccessTokenKeeper.readAccessToken(context);
		// 获取用户信息接口
		mUsersAPI = new UsersAPI(mAccessToken);
	}

	@Override
	protected String doInBackground(Void... arg0) {

		long uid = Long.parseLong(mAccessToken.getUid());
		mUsersAPI.show(uid, mListener);
		return "";
	}

	/**
	 * 微博 OpenAPI 回调接口。
	 */
	private RequestListener mListener = new RequestListener() {
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				LogUtil.i(TAG, response);
				// 调用 User#parse 将JSON串解析成User对象
				User user = User.parse(response);
				if (user != null) {
					new ConfigManager(mContext)
							.setStoreUserNameConfig(user.screen_name);
					if (userInfoReadyImpl != null) {
						userInfoReadyImpl.onUserInfoReady(user);
					}

				} else {
					Log.i(TAG, "user==null");
				}
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
			LogUtil.e(TAG, e.getMessage());
			ErrorInfo info = ErrorInfo.parse(e.getMessage());
			LogUtil.e(TAG, info.toString());
		}
	};

	/**
	 * @author bpok
	 * 
	 *         用户信息获取成功的回调接口
	 * 
	 */
	public interface UserInfoReadyImpl {
		public abstract void onUserInfoReady(User user);
	}
}
