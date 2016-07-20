package com.bpok.sina.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bpok.sina.R;
import com.bpok.sina.loaders.UserInfoASyncTask;
import com.bpok.sina.loaders.UserInfoASyncTask.UserInfoReadyImpl;
import com.bpok.sina.utils.ConfigManager;
import com.bpok.sina.view.DampView;
import com.sina.weibo.sdk.openapi.models.User;

public class FragmentFloat extends FragmentBase implements UserInfoReadyImpl {

	/** views */
	private View mRootView;
	private DampView mDampView;
	private ImageView mImageView;
	private ConfigManager configManager;
	private ImageView user_avatar;
	private TextView user_mod, user_fans_num, user_following_num;

	@Override
	public void onAttach(Activity activity) {
		configManager = new ConfigManager(activity.getApplicationContext());
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_user_float, null);
		findViews();
		new UserInfoASyncTask(getActivity(), this).execute();
		return mRootView;
	}

	private void findViews() {
		this.mDampView = (DampView) mRootView.findViewById(R.id.dampview);
		this.mImageView = (ImageView) mRootView
				.findViewById(R.id.iv_damp_image);
		this.mDampView.setImageView(mImageView);

		// user image
		this.user_avatar = (ImageView) mRootView
				.findViewById(R.id.iv_user_avatar_hd);
		this.user_mod = (TextView) mRootView.findViewById(R.id.user_mood_hd);
		this.user_fans_num = (TextView) mRootView
				.findViewById(R.id.tv_user_follower_count);
		this.user_following_num = (TextView) mRootView
				.findViewById(R.id.tv_following_count);

		// init info frm storge
		this.user_mod.setText(configManager.getUserMod());
		this.user_following_num.setText("关注数:"
				+ configManager.getUserFollowingCount());
		this.user_fans_num.setText("粉丝数:" + configManager.getUserFansCount());

	}

	@Override
	public void onUserInfoReady(User user) {
		// 展示用户信息
		com.nostra13.universalimageloader.core.ImageLoader.getInstance()
				.displayImage(user.avatar_hd, user_avatar);
		user_mod.setText(user.description);
		user_fans_num.setText("粉丝数:" + user.followers_count);
		user_following_num.setText("关注数:" + user.friends_count);
		// store
		configManager.setStoreUserNameConfig(user.screen_name);
		configManager.setUserMod(user.description);
		configManager.setUserFansCount(user.followers_count);
		configManager.setUserFollowingCount(user.friends_count);
	}
}
