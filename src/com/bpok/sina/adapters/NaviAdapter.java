package com.bpok.sina.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bpok.sina.R;
import com.bpok.sina.adapters.modle.MenuModle;

public class NaviAdapter extends BaseAdapter {

	private List<MenuModle> contentList;
	private Context mContext;
	private ViewHolder viewHolder;

	/**
	 * @param contentList
	 * @param mContext
	 */
	public NaviAdapter(List<MenuModle> contentList, Context mContext) {
		super();
		this.contentList = contentList;
		this.mContext = mContext;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.navi_item, null);
			viewHolder.tv_title = (TextView) convertView
					.findViewById(R.id.tv_line_one);
			viewHolder.iv_icon = (ImageView) convertView
					.findViewById(R.id.iv_icon);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		MenuModle content = contentList.get(position);
		viewHolder.tv_title.setText(content.getTitle());
		viewHolder.iv_icon.setImageResource(content.getIcon_sel());
		return convertView;
	}

	@Override
	public int getCount() {
		return contentList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	final class ViewHolder {
		private TextView tv_title;
		private ImageView iv_icon;
	}
}
