package com.bpok.sina.fragment;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bpok.sina.R;
import com.bpok.sina.activity.Main;
import com.bpok.sina.adapters.HomeListAdapter;
import com.bpok.sina.impl.RequestRefershImpl;
import com.bpok.sina.sdk.AccessTokenKeeper;
import com.bpok.sina.utils.TextFileReader;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.utils.LogUtil;

public class FragmentUser extends FragmentBase {

	/** views */
	private View mRootView;
	private View headerView;
	private RelativeLayout layout;
	private ListView userStatusListView;
	private HomeListAdapter listAdapter;
	private Context mContext;
	/** 当前 Token 信息 */
	private Oauth2AccessToken mAccessToken;
	/** 用于获取微博信息流等操作的API */
	private StatusesAPI mStatusesAPI;
	// impl
	private RequestRefershImpl requestRefershImpl;

	private Main baseActivity;
	// Tag
	private String TAG = FragmentUser.class.getName();

	// wait when it is ready
	@SuppressLint("HandlerLeak")
	private Handler readyHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				getStatusFrmFile("user_status.json");
				initApi();
				getUserStatus();
				break;
			}
		}

	};

	@Override
	public void onAttach(Activity activity) {
		baseActivity = (Main) activity;
		mContext = activity.getApplicationContext();
		requestRefershImpl = (RequestRefershImpl) activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_user_list, null);
		// headerView
		try {
			this.headerView = inflater.inflate(R.layout.user_list_header_view,
					null);
		} catch (InflateException e) {
			/* header is already there, just return view as it is */
		}
		initActionBar(inflater);
		// 获取用户信息
		findViews();
		// 等待一秒再去加载费事的操作，否则进入该fragment菜单滑动卡顿
		readyHandler.sendEmptyMessageDelayed(0, 2000);
		return mRootView;
	}

	private void initApi() {
		// 获取当前已保存过的 Token
		mAccessToken = AccessTokenKeeper.readAccessToken(mContext);
		// 对statusAPI实例化
		mStatusesAPI = new StatusesAPI(mAccessToken);
	}

	/**
	 * style actionBar and find views here
	 */
	private void initActionBar(LayoutInflater layoutInflater) {

		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(R.layout.custom_action_bar_user_center);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}

	private void findViews() {
		// listView
		this.userStatusListView = (ListView) mRootView
				.findViewById(R.id.lv_user_fra);
		if (headerView != null)
			userStatusListView.addHeaderView(headerView);
	}
	
	

	@Override
	public void onDestroyView() {
		// 引起的InflateExp 这里解决
		FragmentFloat f = (FragmentFloat) getFragmentManager()
				.findFragmentById(R.id.fragment_float_view);
		if (f != null)
			try{
			getFragmentManager().beginTransaction().remove(f).commit();}
		catch(IllegalStateException e){
			
		}
		super.onDestroyView();
	}

	private void getUserStatus() {
		mStatusesAPI.userTimeline(0L, 0L, 50, 1, false, 0, false, mListener);
		// 显示进度条
		requestRefershImpl.onRequestRefersh();
	}

	private void getStatusFrmFile(String FILE_NAME) {
		String response = new TextFileReader(mContext).read(FILE_NAME);
		if (response != null) {
			// 从字符串解析
			StatusList statusList = StatusList.parse(response);
			inflatetList(statusList);
		}

	}

	/**
	 * @param response
	 * @param FILE_NAME
	 */
	private void cacheDataToStorge(String response, String FILE_NAME) {
		if (response != null) {
			try {
				FileOutputStream fos = mContext.openFileOutput(FILE_NAME,
						Context.MODE_PRIVATE);
				try {
					fos.write(response.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取微博 OpenAPI 回调接口。
	 */
	private RequestListener mListener = new RequestListener() {
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				if (response.startsWith("{\"statuses\"")) {
					// 调用 StatusList#parse 解析字符串成微博列表对象
					StatusList statuses = StatusList.parse(response);
					if (statuses != null && statuses.total_number > 0) {
						inflatetList(statuses);
						// 存储到设备
						cacheDataToStorge(response, "user_status.json");
					}
				} else if (response.startsWith("{\"created_at\"")) {
				} else {
					Log.i(TAG, response);
				}
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
			LogUtil.e(TAG, e.getMessage());
			requestRefershImpl.onRefershComplete();
		}
	};

	private void inflatetList(StatusList statuses) {
		listAdapter = new HomeListAdapter(this, statuses, mContext);
		userStatusListView.setAdapter(listAdapter);

		// 隐藏进度条
		requestRefershImpl.onRefershComplete();
	}

}
