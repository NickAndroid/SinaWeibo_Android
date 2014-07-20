package com.bpok.sina.view;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bpok.sina.R;

public class CustomToast extends Toast {

	public CustomToast(Context context) {
		super(context);
	}

	public static Toast makeText(Context context, CharSequence text,
			int duration) {
		Toast result = new Toast(context);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.custom_toast, null);

		TextView textView = (TextView) layout.findViewById(R.id.toast);
		DisplayMetrics dm2 = context.getResources().getDisplayMetrics();
		textView.setWidth(dm2.widthPixels);
		textView.setText(text);

		result.setView(layout);
		result.setGravity(Gravity.CENTER, 0, 0);
		result.setDuration(duration);

		return result;
	}

}
