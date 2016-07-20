package com.bpok.sina.view;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bpok.sina.R;

/**
 * @author bpok
 * 
 */
public class ViewHolderHomeList {
	public final TextView tv_text, tv_ret_text;
	public final TextView tv_poster_name, tv_ret_poster_name;
	public final TextView tv_post_time, tv_ret_post_time;
	public final TextView tv_comment_count;
	public final TextView tv_repost_count;
	public final RoundCornerImageView iv_poster_avatar, iv_ret_poster_avatar;
	public final ImageView iv_status_img, iv_ret_status_img;
	public final RelativeLayout reLayout;

	public ViewHolderHomeList(View convertView, Context context) {
		/** find views */
		tv_text = (TextView) convertView.findViewById(R.id.tv_text);
		// 设置超链接
		tv_text.setAutoLinkMask(Linkify.ALL);
		tv_text.setMovementMethod(LinkMovementMethod.getInstance());
		tv_text.setLinkTextColor(context.getResources().getColor(
				(R.color.light_blue)));

		tv_comment_count = (TextView) convertView
				.findViewById(R.id.tv_comment_count);
		tv_post_time = (TextView) convertView.findViewById(R.id.tv_create_time);
		tv_poster_name = (TextView) convertView
				.findViewById(R.id.tv_poster_name);
		tv_repost_count = (TextView) convertView
				.findViewById(R.id.tv_repost_count);
		iv_poster_avatar = (RoundCornerImageView) convertView
				.findViewById(R.id.iv_poster_avatar);
		iv_status_img = (ImageView) convertView
				.findViewById(R.id.iv_status_img);

		tv_ret_text = (TextView) convertView
				.findViewById(R.id.tv_retw_text_body);
		// 设置超链接
		tv_ret_text.setAutoLinkMask(Linkify.ALL);
		tv_ret_text.setMovementMethod(LinkMovementMethod.getInstance());
		tv_ret_text.setLinkTextColor(context.getResources().getColor(
				(R.color.light_blue)));

		tv_ret_post_time = (TextView) convertView
				.findViewById(R.id.tv_retw_create_at);
		tv_ret_poster_name = (TextView) convertView
				.findViewById(R.id.tv_retw_user_name);
		iv_ret_poster_avatar = (RoundCornerImageView) convertView
				.findViewById(R.id.iv_retw_user_avatar);
		iv_ret_status_img = (ImageView) convertView
				.findViewById(R.id.iv_retw_img);
		reLayout = (RelativeLayout) convertView.findViewById(R.id.retw_layout);
	}

}
