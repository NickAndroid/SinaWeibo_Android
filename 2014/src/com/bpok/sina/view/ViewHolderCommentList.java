package com.bpok.sina.view;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bpok.sina.R;

/**
 * @author bpok
 * 
 */
public class ViewHolderCommentList {
	public final TextView tv_text;
	public final TextView tv_poster_name;
	public final TextView tv_repost_text;
	public final TextView tv_repost_name;
	public final TextView tv_post_time;
	public final RoundCornerImageView iv_poster_avatar;
	public final ImageView iv_status_img;

	public ViewHolderCommentList(View convertView, Context context) {
		/** find views */
		tv_text = (TextView) convertView.findViewById(R.id.tv_text);
		// 设置超链接
		tv_text.setAutoLinkMask(Linkify.ALL);
		tv_text.setMovementMethod(LinkMovementMethod.getInstance());
		tv_text.setLinkTextColor(context.getResources().getColor(
				(R.color.light_blue)));
		tv_repost_text = (TextView) convertView
				.findViewById(R.id.tv_repost_text);
		tv_repost_text.setAutoLinkMask(Linkify.ALL);
		tv_repost_text.setMovementMethod(LinkMovementMethod.getInstance());
		tv_repost_text.setLinkTextColor(context.getResources().getColor(
				(R.color.light_blue)));
		tv_repost_name = (TextView) convertView
				.findViewById(R.id.tv_repost_name);

		tv_post_time = (TextView) convertView.findViewById(R.id.tv_post_time);
		tv_poster_name = (TextView) convertView
				.findViewById(R.id.tv_poster_name);
		iv_poster_avatar = (RoundCornerImageView) convertView
				.findViewById(R.id.iv_poster_avatar);
		iv_status_img = (ImageView) convertView
				.findViewById(R.id.iv_status_img);

	}

}
