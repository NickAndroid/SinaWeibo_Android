package com.bpok.sina.adapters;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bpok.sina.R;
import com.bpok.sina.adapters.modle.MenuModle;
import com.bpok.sina.utils.ConfigManager;

public class MenuListAdapter extends BaseAdapter {

	private ViewHolder viewHolder = null;
	private List<MenuModle> listContent = null;
	private Context mContent = null;
	private ConfigManager configManager;

	public MenuListAdapter(List<MenuModle> listContent, Context mContext) {
		super();
		this.listContent = listContent;
		this.mContent = mContext;
		configManager = new ConfigManager(mContext);
	}

	@Override
	public View getView(int position, View mView, ViewGroup viewGroup) {
		if (mView == null) {
			// 如果空实例化，否则拿来用
			// 实例化一个ViewHolder
			viewHolder = new ViewHolder();
			if (configManager.getThemeMod() == 0) {
				mView = LayoutInflater.from(mContent).inflate(
						R.layout.menu_item_normal, null);
			} else {
				mView = LayoutInflater.from(mContent).inflate(
						R.layout.menu_item_night, null);
			}
			viewHolder.tv_title = (TextView) mView.findViewById(R.id.tv_title);
			viewHolder.iv_icon = (ImageView) mView.findViewById(R.id.iv_icon);
			viewHolder.iv_indicator = (ImageView) mView
					.findViewById(R.id.iv_indicator);
			mView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) mView.getTag();
		}

		// 设置holder内容
		viewHolder.tv_title.setText(listContent.get(position).getTitle());
		viewHolder.iv_icon.setBackgroundResource(listContent.get(position)
				.getIcon_sel());
		viewHolder.iv_indicator.setBackgroundResource(listContent.get(position)
				.getIndicator());

		return mView;
	}

	@Override
	public int getCount() {
		return listContent.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * @author bpok 初始化一个ViewHolder拿到tag
	 * 
	 */
	final static class ViewHolder {
		TextView tv_title = null;
		ImageView iv_icon = null;
		ImageView iv_indicator = null;// 消息指示器的inageView
	}

}

// 后台获取信息列表数目，显示在菜单的list上，如果需要后台任务的话，比如获取动态数目
final class MySyncTask extends AsyncTask<String, String, String> {

	@Override
	protected String doInBackground(String... params) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onCancelled()
	 */
	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onCancelled(java.lang.Object)
	 */
	@Override
	protected void onCancelled(String result) {
		super.onCancelled(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
	 */
	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
	}

}
