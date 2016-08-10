package com.bpok.sina.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bpok.sina.R;
import com.bpok.sina.adapters.modle.PopItem;
import com.bpok.sina.view.ViewHolderPop;

public class PopViewAdapter extends BaseAdapter {

	// data
	public ArrayList<PopItem> popItemList;
	private Context mContext;

	public PopViewAdapter(ArrayList<PopItem> itemList, Context context) {
		super();
		this.mContext = context;
		this.popItemList = itemList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		ViewHolderPop viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.pop_view_item, null);
			viewHolder = new ViewHolderPop(convertView, mContext);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolderPop) convertView.getTag();
		}
		PopItem item = this.popItemList.get(position);
		viewHolder.tv_title.setText(item.getTitleString());
		viewHolder.iv_icon.setImageResource(item.getIconSrc());
		return convertView;
	}

	@Override
	public int getCount() {
		return this.popItemList.size();
	}

	@Override
	public Object getItem(int index) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
