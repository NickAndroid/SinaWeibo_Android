package com.bpok.sina.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bpok.sina.R;
import com.bpok.sina.view.RoundCornerListView;

public class FragmentMore extends FragmentBase {

	private View mRootView;
	private RoundCornerListView mCornerListView;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_more, null);
		return mRootView;
	}

}
