package com.bpok.sina.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.bpok.sina.R;

public class FragmentBase extends Fragment {
	public void startNewActivity(final Intent intent) {
		getActivity().startActivity(intent);
		getActivity().overridePendingTransition(R.anim.push_up_in,
				R.anim.push_up_out);
	}
}
