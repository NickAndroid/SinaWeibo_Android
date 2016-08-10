package com.bpok.sina.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Scroller;

/**
 * @author howard
 * 
 * 
 */
public class DampView extends ScrollView {
	private static final int LEN = 0xc8;
	private static final int DURATION = 500;
	private static final int MAX_DY = 200;
	private Scroller mScroller;
	TouchTool tool;
	int left, top;
	float startX, startY, currentX, currentY;
	int imageViewH;
	int rootW, rootH;

	ImageView imageView;

	boolean scrollerType;

	public DampView(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);
		// init the callback
	}

	public DampView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScroller = new Scroller(context);
	}

	public DampView(Context context) {
		super(context);

	}

	/**
	 * @param imageView
	 *            have to callback this method
	 */
	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

	private void init() {
		left = imageView.getLeft();
		top = imageView.getBottom();
		rootW = getWidth();
		rootH = getHeight();
		imageViewH = imageView.getHeight();
		startX = currentX;
		startY = currentY;

		Log.i(VIEW_LOG_TAG, currentX + "");
		Log.i(VIEW_LOG_TAG, currentY + "");
	}

	/*
	 * handle the click event if needed
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		int action = event.getAction();
		if (!mScroller.isFinished()) {
			return super.onTouchEvent(event);
		}

		currentX = event.getX();
		currentY = event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:

			init();
			tool = new TouchTool(imageView.getLeft(), imageView.getBottom(),
					imageView.getLeft(), imageView.getBottom() + LEN);
			break;
		case MotionEvent.ACTION_MOVE:
			if (imageView.isShown() && imageView.getTop() >= 0) {
				if (tool != null) {
					int t = tool.getScrollY(currentY - startY);
					if (t >= top && t <= imageView.getBottom() + LEN) {
						android.view.ViewGroup.LayoutParams params = imageView
								.getLayoutParams();
						params.height = t;
						imageView.setLayoutParams(params);
					}
				}
				scrollerType = false;
			}
			break;
		case MotionEvent.ACTION_UP:
			scrollerType = true;
			mScroller.startScroll(imageView.getLeft(), imageView.getBottom(),
					0 - imageView.getLeft(),
					imageViewH - imageView.getBottom(), DURATION);
			invalidate();
			break;
		}

		return super.dispatchTouchEvent(event);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			int x = mScroller.getCurrX();
			int y = mScroller.getCurrY();
			imageView.layout(0, 0, x + imageView.getWidth(), y);
			invalidate();
			if (!mScroller.isFinished() && scrollerType && y > MAX_DY) {
				android.view.ViewGroup.LayoutParams params = imageView
						.getLayoutParams();
				params.height = y;
				imageView.setLayoutParams(params);
			}
		}
	}

	public class TouchTool {

		private int startX, startY;

		public TouchTool(int startX, int startY, int endX, int endY) {
			super();
			this.startX = startX;
			this.startY = startY;
		}

		public int getScrollX(float dx) {
			int xx = (int) (startX + dx / 2.5F);
			return xx;
		}

		public int getScrollY(float dy) {
			int yy = (int) (startY + dy / 2.5F);
			return yy;
		}
	}
}
