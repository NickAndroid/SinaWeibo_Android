package com.bpok.sina.utils;

import android.content.Context;
import android.widget.Toast;

import com.bpok.sina.view.CustomToast;

/**
 * @author guohao
 * 
 */
public class ToastUtils {

	public static void showToast(Context context, String str) {
		CustomToast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}
}
