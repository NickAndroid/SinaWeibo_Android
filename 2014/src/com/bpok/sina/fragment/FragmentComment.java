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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bpok.sina.R;
import com.bpok.sina.adapters.CommentListAdapter;
import com.bpok.sina.impl.RequestRefershImpl;
import com.bpok.sina.sdk.AccessTokenKeeper;
import com.bpok.sina.utils.TextFileReader;
import com.bpok.sina.view.UpDownRefershListView;
import com.bpok.sina.view.UpDownRefershListView.RefreshListener;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.models.CommentList;
import com.sina.weibo.sdk.utils.LogUtil;

public class FragmentComment extends FragmentBase {

	/** 当前 Token 信息 */
	private Oauth2AccessToken mAccessToken;
	/** 微博评论接口 */
	private CommentsAPI mCommentsAPI;
	/** views */
	private Context mContext;
	private View mRootView;
	private UpDownRefershListView mCommentListView;
	// adapters
	private CommentListAdapter mCommentListAdapter;
	private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;

	// handler
	private Handler handler = new Handler();
	// refersh listener
	private MyRefreshListener refreshListener;

	private String TAG = getClass().getName();
	private FragmentComment fragmentComment;
	protected static final String STATE_PAUSE_ON_SCROLL = "STATE_PAUSE_ON_SCROLL";
	protected static final String STATE_PAUSE_ON_FLING = "STATE_PAUSE_ON_FLING";
	protected boolean pauseOnScroll = false;
	protected boolean pauseOnFling = true;

	// impl
	private RequestRefershImpl requestRefershImpl;

	// wait when it is ready
	@SuppressLint("HandlerLeak")
	private Handler readyHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				findViews();
				initListDataFrmFile("comment.json");
				break;
			}
		}

	};

	@Override
	public void onAttach(Activity activity) {
		this.mContext = activity.getApplicationContext();
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_home, null);
		initToken();
		initActionBar(inflater);
		this.fragmentComment = this;
		this.requestRefershImpl = (RequestRefershImpl) getActivity();
		readyHandler.sendEmptyMessageDelayed(0, 1000);
		return mRootView;
	}

	/**
	 * style actionBar and find views here
	 */
	private void initActionBar(LayoutInflater layoutInflater) {

		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(R.layout.custom_action_bar_user_comment);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}

	private void findViews() {
		mCommentListView = (UpDownRefershListView) mRootView
				.findViewById(R.id.lv_home);
		refreshListener = new MyRefreshListener();
		mCommentListView.setRefreshListener(refreshListener);
		mCommentListView.setOnScrollListener(new PauseOnScrollListener(
				ImageLoader.getInstance(), pauseOnScroll, pauseOnFling));
	}

	private void initToken() {
		// 获取当前已保存过的 Token
		mAccessToken = AccessTokenKeeper.readAccessToken(mContext);
		// 获取微博评论信息接口
		mCommentsAPI = new CommentsAPI(mAccessToken);

	}

	/**
	 * @param FILE_NAME
	 *            从存储中获取缓存的json数据
	 */
	private void initListDataFrmFile(String FILE_NAME) {
		String response = new TextFileReader(mContext).read(FILE_NAME);
		if (response != null) {
			CommentList commentList = CommentList.parse(response);
			updateComment(commentList);
		}
		// 无论是否获取到了缓存的status都请求刷新一次
		refreshListener.pullDownRefresh();
	}

	private void updateCommentLine() {
		mCommentsAPI.timeline(0L, 0L, 30, 1, false, mListener);
	}

	private void updateComment(CommentList commentList) {
		// 第一次调用，adapter为空
		if (mCommentListAdapter == null)
			mCommentListAdapter = new CommentListAdapter(fragmentComment,
					commentList, mContext);
		mCommentListView.setAdapter(mCommentListAdapter);
		mCommentListView.onPulldownRefreshComplete();
		// 隐藏进度条
		requestRefershImpl.onRefershComplete();
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
	 * 微博 OpenAPI 回调接口。
	 */
	private RequestListener mListener = new RequestListener() {
		@Override
		public void onComplete(String response) {
			LogUtil.i(TAG, response);
			if (!TextUtils.isEmpty(response)) {
				CommentList comments = CommentList.parse(response);
				if (comments != null && comments.total_number > 0) {
					if (mCommentListAdapter == null)
						mCommentListAdapter = new CommentListAdapter(
								fragmentComment, comments, mContext);
					if (swingBottomInAnimationAdapter == null)
						swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
								mCommentListAdapter);
					swingBottomInAnimationAdapter.setListView(mCommentListView);
					// anmi
					mCommentListView.setAdapter(mCommentListAdapter);
					// save data
					cacheDataToStorge(response, "comment.json");
					updateComment(comments);
				}
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
			LogUtil.e(TAG, e.getMessage());
		}
	};

	private class MyRefreshListener implements RefreshListener {
		// 处理下拉刷新
		@Override
		public void pullDownRefresh() {
			requestRefershImpl.onRequestRefersh();
			new Thread(new Runnable() {
				@Override
				public void run() {
					handler.post(new Runnable() {
						@Override
						public void run() {
							updateCommentLine();
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
							mCommentListView.onPullupRefreshComplete();
						}
					});
				}
			}).start();
		}
	}

}
