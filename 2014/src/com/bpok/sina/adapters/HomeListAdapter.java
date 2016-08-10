package com.bpok.sina.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bpok.sina.R;
import com.bpok.sina.activity.ImageViewer;
import com.bpok.sina.fragment.FragmentBase;
import com.bpok.sina.utils.ConfigManager;
import com.bpok.sina.view.ViewHolderHomeList;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

/**
 * @author guohao
 * 
 */
public class HomeListAdapter extends BaseAdapter {

	// data
	public ArrayList<Status> statusList;

	// views
	private Context mContext;

	// imageLoader
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private DisplayImageOptions options_avatar;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private ConfigManager configManager;
	private FragmentBase mFragment;

	public HomeListAdapter(FragmentBase fragmentBase, StatusList statuses,
			Context context) {
		super();
		this.mContext = context;
		configManager = new ConfigManager(mContext);
		this.statusList = statuses.statusList;
		this.mFragment = fragmentBase;
		// get instance instead of new XX
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.aio_image_default_round)
				.showImageForEmptyUri(R.drawable.aio_image_fail_round)
				.showImageOnFail(R.drawable.aio_image_fail_round)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.build();
		options_avatar = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.qlink_defaut_avatar)
				.showImageForEmptyUri(R.drawable.qlink_defaut_avatar)
				.showImageOnFail(R.drawable.qlink_defaut_avatar)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.displayer(new RoundedBitmapDisplayer(50)).build();
	}

	public void updateList(StatusList statues) {
		this.statusList = statues.statusList;
		this.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		ViewHolderHomeList viewHolder = null;
		if (convertView == null) {
			if (configManager.getThemeMod() == 0) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.home_list_item_full, null);
			} else {
				// 设置为夜间模式
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.home_list_item_night, null);
			}
			viewHolder = new ViewHolderHomeList(convertView, mContext);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolderHomeList) convertView.getTag();
		}

		final Status status = this.statusList.get(position);
		viewHolder.tv_text.setText(status.text);
		viewHolder.tv_poster_name.setText(status.user.name);
		// 简单的处理时间
		viewHolder.tv_post_time.setText(status.created_at.substring(10, 20));
		viewHolder.tv_comment_count.setText(status.comments_count + "");
		viewHolder.tv_repost_count.setText(status.reposts_count + "");

		// 更新作者封面
		imageLoader.displayImage(status.user.avatar_large,
				viewHolder.iv_poster_avatar, options_avatar,
				animateFirstListener);

		// update the post img
		if (status.bmiddle_pic.length() > 10) {
			viewHolder.iv_status_img.setVisibility(View.VISIBLE);
			// 用中等尺寸图片
			final String imgUrlString = status.bmiddle_pic;
			Log.e("guohao", imgUrlString);
			imageLoader.displayImage(imgUrlString, viewHolder.iv_status_img,
					options, animateFirstListener);

			viewHolder.iv_status_img.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					final Bundle bundle = new Bundle();
					bundle.putString("image_url_key", status.original_pic);
					final Intent intent = new Intent();
					intent.setClass(mContext, ImageViewer.class);
					intent.putExtras(bundle);
					mFragment.startNewActivity(intent);
				}
			});
		} else {
			viewHolder.iv_status_img.setVisibility(View.GONE);
		}

		// 当转发微博的时候
		final Status ret_status = status.retweeted_status;
		if (ret_status != null) {
			try {
				viewHolder.reLayout.setVisibility(View.VISIBLE);
				viewHolder.tv_ret_post_time.setText(ret_status.created_at
						.substring(10, 20));
				viewHolder.tv_ret_poster_name
						.setText(ret_status.user.screen_name);
				viewHolder.tv_ret_text.setText(ret_status.text);
				String avaString = ret_status.user.avatar_large;
				imageLoader.displayImage(avaString,
						viewHolder.iv_ret_poster_avatar, options_avatar,
						animateFirstListener);
				String status_imgString = ret_status.bmiddle_pic;
				if (status_imgString != null && status_imgString.length() > 10) {
					viewHolder.iv_ret_status_img.setVisibility(View.VISIBLE);
					imageLoader.displayImage(status_imgString,
							viewHolder.iv_ret_status_img, options,
							animateFirstListener);
					viewHolder.iv_ret_status_img
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									final Bundle bundle = new Bundle();
									bundle.putString("image_url_key",
											ret_status.original_pic);
									final Intent intent = new Intent();
									intent.setClass(mContext, ImageViewer.class);
									intent.putExtras(bundle);
									mFragment.startNewActivity(intent);
								}
							});
				} else {
					viewHolder.iv_ret_status_img.setVisibility(View.GONE);
				}
			} catch (NullPointerException e) {
				viewHolder.reLayout.setVisibility(View.GONE);
			}
		} else {
			viewHolder.reLayout.setVisibility(View.GONE);
		}

		return convertView;
	}

	@Override
	public int getCount() {
		return statusList.size();
	}

	@Override
	public Object getItem(int index) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

}
