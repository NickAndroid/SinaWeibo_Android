package com.bpok.sina.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bpok.sina.R;
import com.bpok.sina.fragment.FragmentFloat;
import com.bpok.sina.fragment.FragmentHome;
import com.bpok.sina.fragment.FragmentMenu;
import com.bpok.sina.impl.RequestRefershImpl;
import com.bpok.sina.utils.ConfigManager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * @author guohao 该应用的主类
 * 
 */
public class Main extends SlidingFragmentActivity implements RequestRefershImpl {

	private Fragment mContent;
	private SmoothProgressBar pb;
	private ConfigManager configManager;
	// 展示用户信息的窗口
	private Fragment infoFragment;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		configManager = new ConfigManager(getApplicationContext());
		initSlidingMenu(savedInstanceState);
		findViews();
	}

	private void findViews() {
		this.pb = (SmoothProgressBar) findViewById(R.id.pb_main);
	}
	
	/**
	 * 初始化滑动菜单
	 */
	private void initSlidingMenu(Bundle savedInstanceState) {
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		if (mContent == null)
			mContent = new FragmentHome();

		// 设置主视图界面
		if (configManager.getThemeMod() == 1) {
			this.setTheme(android.R.style.Theme_Holo);
			setContentView(R.layout.activity_main_night);
		} else
			setContentView(R.layout.activity_main);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();

		// 设置滑动菜单视图界面
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new FragmentMenu()).commit();

		// 设置滑动菜单的属性值
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		getSlidingMenu().setShadowWidthRes(R.dimen.shadow_width);
		getSlidingMenu().setShadowDrawable(R.drawable.shadow);
		getSlidingMenu().setBehindOffsetRes(R.dimen.slidingmenu_offset);
		getSlidingMenu().setFadeDegree(0.35f);

	}

	/**
	 * 切换Fragment，也是切换视图的内容
	 */
	public void switchContent(Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		getSlidingMenu().showContent();

	}

	/**
	 * 显示用户信息窗口
	 */
	public void showUerFlost() {
		if (infoFragment == null) {
			infoFragment = new FragmentFloat();
			getSupportFragmentManager()
					.beginTransaction()
					.setCustomAnimations(R.anim.view_push_down_in,
							R.anim.view_push_down_out)
					.replace(R.id.content_frame_float, infoFragment).commit();
		} else {
			getSupportFragmentManager()
					.beginTransaction()
					.setCustomAnimations(R.anim.view_push_down_in,
							R.anim.view_push_down_out).show(infoFragment)
					.commit();
		}
	}

	public void removeFloat() {
		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.view_push_down_out,
						R.anim.view_push_down_out).hide(infoFragment)
				.commit();
	}

	/**
	 * 保存Fragment的状态
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			toggle();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int id = item.getItemId();
		// 跳转
		if (id == R.id.menu_post) {
			final Intent intent = new Intent();
			intent.setClass(Main.this, Post.class);
			startActivity(intent);
			overridePendingTransition(R.anim.flip_horizontal_in,
					R.anim.flip_horizontal_out);
			return true;
		}
		if (id == android.R.id.home) {
			if (configManager.getThemeMod() == 0)
				configManager.setThemeMod(1);
			else {
				configManager.setThemeMod(0);
			}
			// 切换主题
			final Intent intent = new Intent();
			intent.setClass(Main.this, Main.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRequestRefersh() {
		// TODO Auto-generated method stub
		pb.setVisibility(View.VISIBLE);
	}

	@Override
	public void onRefershComplete() {
		// TODO Auto-generated method stub
		pb.setVisibility(View.GONE);
	}
}
