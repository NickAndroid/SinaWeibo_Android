package com.bpok.sina.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bpok.sina.R;
import com.bpok.sina.activity.Main;
import com.bpok.sina.adapters.MenuListAdapter;
import com.bpok.sina.adapters.modle.MenuModle;
import com.bpok.sina.loaders.ImageLoader;
import com.bpok.sina.loaders.UserInfoASyncTask;
import com.bpok.sina.loaders.UserInfoASyncTask.UserInfoReadyImpl;
import com.bpok.sina.utils.ConfigManager;
import com.sina.weibo.sdk.openapi.models.User;

public class FragmentMenu extends Fragment implements OnItemClickListener,
		UserInfoReadyImpl {

	private View mRootView;
	private Context mContext;
	private ListView mMenuListView;
	private ArrayList<MenuModle> menuList;
	private MenuListAdapter menuListAdapter;
	private ImageView mImageVeiw;
	private TextView tv_user_name;
	private ConfigManager configManager;

	@Override
	public void onAttach(Activity activity) {
		mContext = activity.getApplicationContext();
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		configManager = new ConfigManager(getActivity());
		if (configManager.getThemeMod() == 0) {
			mRootView = inflater.inflate(R.layout.fragment_menu, null);
		} else {
			mRootView = inflater.inflate(R.layout.fragment_menu_night, null);
		}
		findViews();
		initData();
		new UserInfoASyncTask(mContext, this).execute();
		return mRootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void findViews() {
		this.mMenuListView = (ListView) mRootView.findViewById(R.id.lv_menu);
		this.tv_user_name = (TextView) mRootView
				.findViewById(R.id.tv_user_name_menu);
		this.tv_user_name.setText(configManager.getUserName());
		this.mMenuListView.setOnItemClickListener(this);
		this.mImageVeiw = (ImageView) mRootView
				.findViewById(R.id.iv_user_logo_hd);
	}

	private void initData() {

		menuList = new ArrayList<MenuModle>();
		MenuModle menuModle1 = new MenuModle("好友动态",
				R.drawable.skin_tab_icon_plugin_selected,
				R.drawable.skin_tab_icon_plugin_normal, R.drawable.new_dot);
		MenuModle menuModle2 = new MenuModle("评论消息",
				R.drawable.skin_tab_icon_conversation_selected,
				R.drawable.skin_tab_icon_conversation_normal,
				R.drawable.new_dot);
		MenuModle menuModle3 = new MenuModle("我的",
				R.drawable.skin_tab_icon_contact_selected,
				R.drawable.skin_tab_icon_contact_normal, R.drawable.new_dot);
//		MenuModle menuModle4 = new MenuModle("更多",
//				R.drawable.tab_more_selected, R.drawable.tab_more,
//				R.drawable.new_dot);
		menuList.add(menuModle1);
		menuList.add(menuModle2);
		menuList.add(menuModle3);
	//	menuList.add(menuModle4);

		menuListAdapter = new MenuListAdapter(menuList, mContext);
		mMenuListView.setAdapter(menuListAdapter);
	}

	// 切换Fragment视图内ring
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;

		if (getActivity() instanceof Main) {
			Main fca = (Main) getActivity();
			fca.switchContent(fragment);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Fragment newContent = null;
		switch (position) {
		case 0:
			newContent = new FragmentHome();
			break;
		case 1:
			newContent = new FragmentComment();
			break;
		case 2:
			newContent = new FragmentUser();
			break;
		case 3:
			// newContent = new FragmentMore();
			break;
		case 4:
			break;
		}
		if (newContent != null)
			switchFragment(newContent);
	}

	@Override
	public void onUserInfoReady(User user) {
		new ImageLoader(mContext).displayImage(user.avatar_hd, mImageVeiw,
				R.drawable.logo);
		this.tv_user_name.setText(user.screen_name);
		configManager.setStoreUserNameConfig(user.screen_name);
	}

}
