package com.bpok.sina.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bpok.sina.R;

/**
 * @author bpok
 * 
 */
public class ViewHolderPop {
	public final TextView tv_title;
	public final ImageView iv_icon;
	public final ImageView iv_tip_indi;

	public ViewHolderPop(View convertView, Context context) {
		/** find views */
		tv_title = (TextView) convertView.findViewById(R.id.tv_pop_title);
		iv_icon = (ImageView) convertView.findViewById(R.id.iv_pop_icon);
		iv_tip_indi = (ImageView) convertView.findViewById(R.id.iv_pop_indi);
	}

}
