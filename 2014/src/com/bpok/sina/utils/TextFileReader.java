package com.bpok.sina.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import android.content.Context;

public class TextFileReader {
	private Context mContext;

	/**
	 * @param mContext
	 */
	public TextFileReader(Context mContext) {
		super();
		this.mContext = mContext;
	}

	public String read(String filename) {
		try {
			FileInputStream fis = null;
			InputStreamReader reader = null;
			BufferedReader br = null;
			try {
				fis = mContext.openFileInput(filename);
				reader = new InputStreamReader(fis, "utf-8");
				br = new BufferedReader(reader);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			StringBuffer sb = new StringBuffer("");
			String lineString;
			try {
				while ((lineString = br.readLine()) != null) {
					sb.append(lineString);
					sb.append("\n");
				}
				fis.close();
				br.close();
				return sb.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
