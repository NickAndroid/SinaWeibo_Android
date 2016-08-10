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
import com.bpok.sina.activity.WriteComment;
import com.bpok.sina.fragment.FragmentBase;
import com.bpok.sina.view.ViewHolderCommentList;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.CommentList;

public class CommentListAdapter extends BaseAdapter {

	// data
	public ArrayList<Comment> commentList;

	// views
	private Context mContext;

	// imageLoader
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private DisplayImageOptions options_avatar;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private FragmentBase mFragment;

	public CommentListAdapter(FragmentBase fragmentBase, CommentList comments,
			Context context) {
		super();
		this.mContext = context;
		this.commentList = comments.commentList;
		this.mFragment = fragmentBase;
		// get instance instead of new XX
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.aio_image_default_round)
				.showImageForEmptyUri(R.drawable.aio_image_fail_round)
				.showImageOnFail(R.drawable.aio_image_fail_round)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.build();
		options_avatar = new DisplayImageOptions.Builder().cacheInMemory(true)
				.showImageOnLoading(R.drawable.qlink_defaut_avatar)
				.showImageForEmptyUri(R.drawable.qlink_defaut_avatar)
				.showImageOnFail(R.drawable.qlink_defaut_avatar)
				.cacheOnDisk(true).considerExifParams(true)
				.displayer(new RoundedBitmapDisplayer(50)).build();
	}

	public void updateList(CommentList comments) {
		this.commentList = comments.commentList;
		this.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		ViewHolderCommentList viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.comment_list_item_full, null);
			viewHolder = new ViewHolderCommentList(convertView, mContext);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolderCommentList) convertView.getTag();
		}

		final Comment comment = this.commentList.get(position);
		viewHolder.tv_poster_name.setText(comment.user.screen_name);
		viewHolder.tv_text.setText(comment.text);
		viewHolder.tv_repost_name.setText(comment.status.user.screen_name);
		viewHolder.tv_repost_text.setText(comment.status.text);
		viewHolder.tv_poster_name.setText(comment.user.screen_name);
		viewHolder.tv_post_time.setText(comment.created_at.substring(10, 20));

		// 更新作者封面
		imageLoader.displayImage(comment.user.avatar_large,
				viewHolder.iv_poster_avatar, options_avatar,
				animateFirstListener);

		// update the post img
		if (comment.status.bmiddle_pic.length() > 10) {
			viewHolder.iv_status_img.setVisibility(View.VISIBLE);
			// 用中等尺寸图片
			String imgUrlString = comment.status.bmiddle_pic;
			Log.e("guohao", imgUrlString);
			imageLoader.displayImage(comment.status.bmiddle_pic,
					viewHolder.iv_status_img, options, animateFirstListener);

			viewHolder.iv_status_img.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					final Bundle bundle = new Bundle();
					bundle.putString("image_url_key",
							comment.status.original_pic);
					final Intent intent = new Intent();
					intent.setClass(mContext, ImageViewer.class);
					intent.putExtras(bundle);
					Log.e("m", mFragment + "");
					mFragment.startNewActivity(intent);
				}
			});

		} else {
			viewHolder.iv_status_img.setVisibility(View.GONE);
		}

		// onClick
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// go to comment activity
				final Intent intent = new Intent();
				intent.setClass(mContext, WriteComment.class);
				mFragment.startNewActivity(intent);
			}
		});

		return convertView;
	}

	@Override
	public int getCount() {
		return commentList.size();
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
