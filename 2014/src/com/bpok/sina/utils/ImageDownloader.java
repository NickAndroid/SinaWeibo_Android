package com.bpok.sina.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

/**
 * @author Administrator 下载bitmap的类
 * 
 */
public class ImageDownloader {

	private ImageDownLoadImpl mDownLoadImpl;

	public ImageDownloader(ImageDownLoadImpl mDownLoadImpl) {
		super();
		this.mDownLoadImpl = mDownLoadImpl;
	}

	public void downloadBitmap(String saveName, Bitmap bitmap) {
		String dirPath = Environment.getExternalStorageDirectory().getPath()
				+ "/pictures_siner";
		String path = Environment.getExternalStorageDirectory().getPath()
				+ "/pictures_siner/" + saveName + ".PNG";
		File dirFile = new File(dirPath);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		mDownLoadImpl.onImageDownloaded();
	}

	public interface ImageDownLoadImpl {
		public abstract void onImageDownloaded();
	}

}
