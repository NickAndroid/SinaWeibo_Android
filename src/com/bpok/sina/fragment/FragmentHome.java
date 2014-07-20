package com.bpok.sina.fragment;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bpok.sina.R;
import com.bpok.sina.adapters.HomeListAdapter;
import com.bpok.sina.adapters.NaviAdapter;
import com.bpok.sina.adapters.modle.MenuModle;
import com.bpok.sina.impl.RequestRefershImpl;
import com.bpok.sina.sdk.AccessTokenKeeper;
import com.bpok.sina.utils.ConfigManager;
import com.bpok.sina.utils.TextFileReader;
import com.bpok.sina.utils.ToastUtils;
import com.bpok.sina.view.UpDownRefershListView;
import com.bpok.sina.view.UpDownRefershListView.RefreshListener;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.utils.LogUtil;

public class FragmentHome extends FragmentBase implements
		ActionBar.OnNavigationListener {

	/** 当前 Token 信息 */
	private Oauth2AccessToken mAccessToken;
	/** 用于获取微博信息流等操作的API */
	private StatusesAPI mStatusesAPI;

	/** views */
	private Context mContext;
	private View mRootView;
	private UpDownRefershListView mListView;

	// adapters
	private HomeListAdapter mHomeListAdapter;
	// for test
	private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;

	// handler
	private Handler handler = new Handler();

	// refersh listener
	private MyRefreshListener refreshListener;

	// 当前标示id
	private int current_since_id = 20;
	// 后台获取的记录条数
	public final int total_status_count = 100;
	// 每次显示给用户的条数
	private final int page_status_count = 20;
	// current data
	private StatusList currentList;
	// 所有数据
	public StatusList allList;

	// impl
	private RequestRefershImpl requestRefershImpl;

	private String TAG = FragmentHome.class.getName();
	protected static final String STATE_PAUSE_ON_SCROLL = "STATE_PAUSE_ON_SCROLL";
	protected static final String STATE_PAUSE_ON_FLING = "STATE_PAUSE_ON_FLING";
	protected boolean pauseOnScroll = false;
	protected boolean pauseOnFling = true;
	private static FragmentHome fragmentHome;

	// config
	private ConfigManager configManager;

	// wait when it is ready
	@SuppressLint("HandlerLeak")
	private Handler readyHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				initApi();
				findViews();
				new GetAllStatusAsyncTask().execute();
				getAllListDataFrmFile("all_response.json");
				break;
			}
		}

	};

	/**
	 * @return 返回实例
	 */
	public static FragmentHome getInstance() {
		if (fragmentHome == null) {
			fragmentHome = new FragmentHome();
		}
		return fragmentHome;
	}

	@Override
	public void onAttach(Activity activity) {
		this.mContext = activity.getApplicationContext();
		super.onAttach(activity);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		configManager = new ConfigManager(getActivity());
		if (configManager.getThemeMod() == 0)
			mRootView = inflater.inflate(R.layout.fragment_home, null);
		else {
			mRootView = inflater.inflate(R.layout.fragment_home_night, null);
		}
		// 等待一秒再去加载费事的操作，否则进入该fragment菜单滑动卡顿
		readyHandler.sendEmptyMessageDelayed(0, 2000);
		initActionBar(inflater);
		// activity需要实现相关接口
		this.requestRefershImpl = (RequestRefershImpl) getActivity();
		return mRootView;
	}

	/**
	 * style actionBar and find views here
	 */
	private void initActionBar(LayoutInflater layoutInflater) {

		ActionBar actionBar = getActivity().getActionBar();
		if (configManager.getThemeMod() == 0) {
			actionBar.setBackgroundDrawable(getResources().getDrawable(
					R.color.white));
			actionBar.setIcon(R.drawable.hpz);
		} else {
			actionBar.setBackgroundDrawable(getResources().getDrawable(
					R.color.bg_dark_tab));
			actionBar.setIcon(R.drawable.hqa);
		}

		// actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		/** 过滤类型ID，0：全部、1：原创、2：图片、3：视频、4：音乐 */
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		List<MenuModle> menuList = new ArrayList<MenuModle>();
		MenuModle item_1 = new MenuModle();
		item_1.setTitle("全部");
		item_1.setIcon_sel(R.drawable.ffo);
		MenuModle item_2 = new MenuModle();
		item_2.setTitle("原创");
		item_2.setIcon_sel(R.drawable.ffm);
		MenuModle item_3 = new MenuModle();
		item_3.setTitle("图片");
		item_3.setIcon_sel(R.drawable.grg);
		MenuModle item_4 = new MenuModle();
		item_4.setTitle("视频");
		item_4.setIcon_sel(R.drawable.grb);
		MenuModle item_5 = new MenuModle();
		item_5.setTitle("音乐");
		item_5.setIcon_sel(R.drawable.gri);
		menuList.add(item_1);
		menuList.add(item_2);
		menuList.add(item_3);
		menuList.add(item_4);
		menuList.add(item_5);
		actionBar.setListNavigationCallbacks(new NaviAdapter(menuList,
				getActivity()), this);
	}

	private void findViews() {
		mListView = (UpDownRefershListView) mRootView
				.findViewById(R.id.lv_home);
		refreshListener = new MyRefreshListener();
		mListView.setRefreshListener(refreshListener);
		mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader
				.getInstance(), pauseOnScroll, pauseOnFling));
		initListDataFrmFile("response.json");
	}

	private void initApi() {
		// 获取当前已保存过的 Token
		mAccessToken = AccessTokenKeeper.readAccessToken(mContext);
		// 对statusAPI实例化
		mStatusesAPI = new StatusesAPI(mAccessToken);
	}

	/**
	 * 下拉刷新时执行，获取最新动态
	 */
	private void getNewStatues(int featureType) {
		// 显示一个进度条
		requestRefershImpl.onRequestRefersh();
		if (mStatusesAPI == null) {
			initApi();
		}
		mStatusesAPI.friendsTimeline(0L, 0L, page_status_count, 1, false,
				featureType, false, mListener);
		// 后台更新所有status数据
		new GetAllStatusAsyncTask().execute();
		// 每次下拉刷新重置status的id起点
		this.current_since_id = page_status_count;
		Log.e("id", "重置了since_id");
		Log.e("test read", "json---------6");
	}

	/**
	 * updtate the listContent 更新列表，用于下拉刷新
	 */
	private void updateListData(StatusList statuses) {
		Log.e("test read", "json----2");
		// test setAdapter
		if (statuses != null && statuses.total_number > 0) {
			if (mHomeListAdapter == null) {
				mHomeListAdapter = new HomeListAdapter(this, statuses,
						getActivity());
				// google cards anmi
				if (swingBottomInAnimationAdapter == null)
					swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
							mHomeListAdapter);
				swingBottomInAnimationAdapter.setListView(mListView);
				// nullPointer happens.....
				if (mListView == null) {
					mListView = (UpDownRefershListView) mRootView
							.findViewById(R.id.lv_home);
				}
				mListView.setAdapter(mHomeListAdapter);
			} else {
				mHomeListAdapter.statusList = statuses.statusList;
				mHomeListAdapter.notifyDataSetChanged();
			}
		}
		mListView.onPulldownRefreshComplete();
		requestRefershImpl.onRefershComplete();
		this.currentList = statuses;
	}

	/**
	 * @param FILE_NAME
	 *            从存储中获取缓存的json数据
	 */
	private void initListDataFrmFile(String FILE_NAME) {
		String response = new TextFileReader(mContext).read(FILE_NAME);
		if (response != null) {
			StatusList statusList = StatusList.parse(response);
			updateListData(statusList);
			this.currentList = statusList;
		}
		// 无论是否获取到了缓存的status都请求刷新一次
		refreshListener.pullDownRefresh();
	}

	/**
	 * @param FILE_NAME
	 *            从存储中获取缓存的json数据
	 */
	private void getAllListDataFrmFile(String FILE_NAME) {
		String response = new TextFileReader(mContext).read(FILE_NAME);
		if (response != null) {
			StatusList statusList = StatusList.parse(response);
			this.allList = statusList;
		}
	}

	/**
	 * @param statusList
	 *            add the list content -------->>>foot
	 */
	private void extendsStatus() {
		// 如果用户获取到的状态条数过少会触发异常,暂时先捕获，有时间在研究
		try {
			// 判断所有数据条数是否多余获取的条数
			if (current_since_id + page_status_count <= total_status_count) {
				for (int i = current_since_id; i < current_since_id
						+ page_status_count; i++) {
					Status status = allList.statusList.get(i);
					currentList.statusList.add(status);
				}
			} else {
				ToastUtils.showToast(getActivity(), "就这么多了");
			}
			mHomeListAdapter.updateList(currentList);
			current_since_id += page_status_count;
			mListView.onPullupRefreshComplete();
		} catch (Exception e) {
			ToastUtils.showToast(getActivity(), "就这么多了呢");
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
	 * 下拉刷新获取最新微博 OpenAPI 回调接口。
	 */
	private RequestListener mListener = new RequestListener() {
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				LogUtil.i(TAG, response);
				if (response.startsWith("{\"statuses\"")) {
					// 调用 StatusList#parse 解析字符串成微博列表对象
					StatusList statuses = StatusList.parse(response);
					if (statuses != null && statuses.total_number > 0) {
						Log.i("statues", statuses.statusList.get(0).text);
						// 缓存到存储
						cacheDataToStorge(response, "response.json");
						updateListData(statuses);
						// 每次下拉刷新都赋给当前list
						currentList = statuses;
					}
				} else if (response.startsWith("{\"created_at\"")) {
					// 调用 Status#parse 解析字符串成微博对象
					Status status = Status.parse(response);
					Log.i(TAG, "发送一送微博成功, id = " + status.id);
				} else {
					Log.i(TAG, response);
				}
			}
			requestRefershImpl.onRefershComplete();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			LogUtil.e(TAG, e.getMessage());
			requestRefershImpl.onRefershComplete();
			ErrorInfo info = ErrorInfo.parse(e.getMessage());
			Log.i(TAG, info.toString());
		}
	};

	/**
	 * 上拉获取更多微博 OpenAPI 回调接口。
	 */
	private RequestListener mListener_all_status = new RequestListener() {
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				LogUtil.i(TAG, response);
				if (response.startsWith("{\"statuses\"")) {
					StatusList statuses = StatusList.parse(response);
					if (statuses != null && statuses.total_number > 0) {
						// 存储
						cacheDataToStorge(response, "all_response.json");
						if (configManager.getFeatureType() == 0)
							allList = statuses;
					}
				} else {
					Log.i(TAG, response);
				}
			}
			requestRefershImpl.onRefershComplete();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			LogUtil.e(TAG, e.getMessage());
			requestRefershImpl.onRefershComplete();
			ErrorInfo info = ErrorInfo.parse(e.getMessage());
			Log.i(TAG, info.toString());
		}
	};

	private class MyRefreshListener implements RefreshListener {
		// 处理下拉刷新
		@Override
		public void pullDownRefresh() {
			// 显示进度条
			requestRefershImpl.onRequestRefersh();
			new Thread(new Runnable() {
				@Override
				public void run() {
					handler.post(new Runnable() {
						@Override
						public void run() {
							getNewStatues(configManager.getFeatureType());
						}
					});
				}
			}).start();
		}

		// 处理上拉刷新
		@Override
		public void pullUpRefresh() {
			new Thread(new Runnable() {
				@Override
				public void run() {
					handler.post(new Runnable() {
						@Override
						public void run() {
							extendsStatus();
						}
					});
				}
			}).start();
		}
	}

	private final class GetAllStatusAsyncTask extends
			AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			mStatusesAPI
					.friendsTimeline(0L, 0L, total_status_count, 1, false,
							configManager.getFeatureType(), false,
							mListener_all_status);
			return null;
		}

	}

	/** 过滤类型ID，0：全部、1：原创、2：图片、3：视频、4：音乐 */
	@Override
	public boolean onNavigationItemSelected(int position, long arg1) {
		// TODO Auto-generated method stub
		if (configManager == null) {
			configManager = new ConfigManager(getActivity());
		}
		switch (position) {
		case 0:
			configManager.setFeatureType(0);
			getNewStatues(configManager.getFeatureType());
			break;
		case 1:
			configManager.setFeatureType(1);
			getNewStatues(configManager.getFeatureType());
			break;
		case 2:
			configManager.setFeatureType(2);
			getNewStatues(configManager.getFeatureType());
			break;
		case 3:
			configManager.setFeatureType(3);
			getNewStatues(configManager.getFeatureType());
			break;
		case 4:
			configManager.setFeatureType(4);
			getNewStatues(configManager.getFeatureType());
			break;
		}
		return false;
	}
}
