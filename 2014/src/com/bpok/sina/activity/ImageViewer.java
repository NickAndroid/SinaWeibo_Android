package com.bpok.sina.activity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bpok.sina.R;
import com.bpok.sina.utils.ImageDownloader;
import com.bpok.sina.utils.ImageDownloader.ImageDownLoadImpl;
import com.bpok.sina.utils.ToastUtils;
import com.bpok.sina.view.DragImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ImageViewer extends Activity implements ImageDownLoadImpl {

	private ImageView iv_download, iv_back;
	private static DragImageView dragImageView;
	public static TextView tv_image_size;
	public static ProgressBar progressBar;
	public static Bitmap tmp_bp;
	private ImageDownloader imageDownloader;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private static int window_width;// 控件宽度
	private static int window_height;
	private static Activity mActivity;// 需要注入的activity

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imag_viewer);
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.aio_image_default_round)
				.showImageForEmptyUri(R.drawable.aio_image_fail_round)
				.showImageOnFail(R.drawable.aio_image_fail_round)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.build();
		imageDownloader = new ImageDownloader(this);
		/** 获取可見区域高度 **/
		WindowManager manager = getWindowManager();
		window_width = manager.getDefaultDisplay().getWidth();
		window_height = manager.getDefaultDisplay().getHeight();
		mActivity = this;
		initActionBar();
		findViews();
		getSetData();
	}

	/**
	 * style actionBar and find views here
	 */
	private void initActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.color.white));
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(R.layout.custom_action_bar_viewer);
		this.iv_back = (ImageView) findViewById(R.id.iv_action_back);
		progressBar = (ProgressBar) findViewById(R.id.pb_action_circle);
		this.iv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				quitActivity();
			}
		});

		this.iv_download = (ImageView) findViewById(R.id.iv_action_download);
		this.iv_download.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (tmp_bp == null) {
					ToastUtils.showToast(getApplicationContext(),
							"请等待当前图片缓存完成后重试");
				} else {
					imageDownloader.downloadBitmap(System.currentTimeMillis()
							+ "", tmp_bp);
				}
			}
		});
	}

	private void findViews() {
		dragImageView = (DragImageView) findViewById(R.id.iv_image_hd);
		tv_image_size = (TextView) findViewById(R.id.tv_image_size);
	}

	private void getSetData() {
		Bundle bundle = getIntent().getExtras();
		String urlString = bundle.getString("image_url_key");
		imageLoader.displayImage(urlString, dragImageView, options,
				animateFirstListener);
	}

	private static void setDragableImage(Bitmap bp) {
		dragImageView.setImageBitmap(bp);
		dragImageView.setmActivity(mActivity);
		dragImageView.setScreen_W(window_width);
		dragImageView.setScreen_H(window_height);
	}

	private void quitActivity() {
		finish();
		overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		quitActivity();
	}

	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					displayedImages.add(imageUri);
				}
				setDragableImage(loadedImage);
			}
			// 进度条
			if (progressBar != null)
				progressBar.setVisibility(View.INVISIBLE);
			// actionBar信息
			tv_image_size.setText("尺寸:" + loadedImage.getWidth() + "x"
					+ loadedImage.getHeight());
			tmp_bp = loadedImage;
		}
	}

	@Override
	public void onImageDownloaded() {
		ToastUtils.showToast(getApplicationContext(),
				"图片已经保存在SD卡pictures_siner目录下");
	}

}
