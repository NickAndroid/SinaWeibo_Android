package com.bpok.sina.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bpok.sina.R;

public class UpDownRefershListView extends ListView {

	int firstVisibleItemIndex;// 屏幕显示的第一个item的索引值
	int lastVisibleItemIndex;// 屏幕能见的最后一个item的索引值
	private View header;
	private ImageView headerArrow;
	private ProgressBar headerProgressBar;
	private TextView headerTitle;
	private TextView headerLastUpdated;
	private View footer;
	private ImageView footerArrow;
	private ProgressBar footerProgressBar;
	private TextView footerTitle;
	private TextView footerLastUpdated;

	private int headerWidth;
	private int headerHeight;

	private Animation animation;
	private Animation reverseAnimation;

	private static final int PULL_TO_REFRESH = 0;
	private static final int RELEASE_TO_REFERESH = 1;
	private static final int REFERESHING = 2;
	private static final int DONE = 3;
	private static final float RATIO = 3;
	private static boolean isBack = false;
	private boolean refereshEnable;// 是否可以进行刷新
	private int state;// 当前刷新状态

	boolean isRecorded;
	float startY;
	float firstTempY = 0;
	float secondTempY = 0;
	RefreshListener rListener;

	int pulltype;

	public UpDownRefershListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public UpDownRefershListView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 初始化listview
	 * 
	 * @param context
	 */
	private void init(Context context) {

		animation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(150);
		animation.setFillAfter(true);
		animation.setInterpolator(new LinearInterpolator());

		reverseAnimation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setDuration(150);
		reverseAnimation.setFillAfter(true);
		reverseAnimation.setInterpolator(new LinearInterpolator());

		LayoutInflater inflater = LayoutInflater.from(context);
		header = inflater.inflate(R.layout.header, null);
		headerArrow = (ImageView) header.findViewById(R.id.arrow);
		headerProgressBar = (ProgressBar) header.findViewById(R.id.progerssbar);
		headerTitle = (TextView) header.findViewById(R.id.title);
		headerLastUpdated = (TextView) header.findViewById(R.id.updated);
		headerArrow.setMinimumWidth(70);
		headerArrow.setMaxHeight(50);

		footer = inflater.inflate(R.layout.header, null);
		footerArrow = (ImageView) footer.findViewById(R.id.arrow);
		footerArrow.startAnimation(reverseAnimation);// 把箭头方向反转过来
		footerProgressBar = (ProgressBar) footer.findViewById(R.id.progerssbar);
		footerTitle = (TextView) footer.findViewById(R.id.title);
		footerLastUpdated = (TextView) footer.findViewById(R.id.updated);
		footerTitle.setText("查看更多");
		footerLastUpdated.setText("释放查看更多内容");
		footerArrow.setMinimumWidth(70);
		footerArrow.setMaxHeight(500);

		measureView(header);

		headerWidth = header.getMeasuredWidth();
		headerHeight = header.getMeasuredHeight();

		header.setPadding(0, -1 * headerHeight, 0, 0);// 设置 与界面上边距的距离
		header.invalidate();// 控件重绘

		footer.setPadding(0, -1 * headerHeight, 0, 0);// 设置与界面上边距的距离
		footer.invalidate();// 控件重绘

		addHeaderView(header);
		addFooterView(footer);
		state = DONE;
		refereshEnable = false;
	}

	private void measureView(View v) {
		ViewGroup.LayoutParams lp = v.getLayoutParams();
		if (lp == null) {
			lp = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int measureWidth = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
		int measureHeight;
		if (lp.height > 0) {
			measureHeight = MeasureSpec.makeMeasureSpec(lp.height,
					MeasureSpec.EXACTLY);
		} else {
			measureHeight = MeasureSpec.makeMeasureSpec(lp.height,
					MeasureSpec.UNSPECIFIED);
		}
		v.measure(measureWidth, measureHeight);
	}

	public interface RefreshListener {
		public void pullDownRefresh();

		public void pullUpRefresh();
	}

	public void setRefreshListener(RefreshListener l) {
		rListener = l;
		refereshEnable = true;
	}

	/**
	 * 处理下拉刷新完成后事项
	 */
	public void onPulldownRefreshComplete() {
		state = DONE;
		onHeaderStateChange();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		headerLastUpdated.setText("最后刷新时间：" + sdf.format(new Date()));
	}

	/**
	 * 处理上拉刷新完成后事项
	 */
	public void onPullupRefreshComplete() {
		state = DONE;
		onFooterStateChange();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		footerLastUpdated.setText("最后刷新时间：" + sdf.format(new Date()));

	}

	/**
	 * 所有的拉动事件皆由此驱动
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		lastVisibleItemIndex = getLastVisiblePosition() - 1;// 因为加有一尾视图，所以这里要咸一
		int totalCounts = getCount() - 1;// 因为给listview加了一头一尾）视图所以这里要减二
		if (refereshEnable) {

			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:

				firstTempY = ev.getY();
				isRecorded = false;
				if (getFirstVisiblePosition() == 0) {
					if (!isRecorded) {
						startY = ev.getY();
						isRecorded = true;
					}
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (getFirstVisiblePosition() == 0) {
					firstTempY = secondTempY;
					secondTempY = ev.getY();
					if (!isRecorded) {
						startY = secondTempY;
						isRecorded = true;
					}
					if (state != REFERESHING) {
						if (state == DONE) {
							if (secondTempY - startY > 0) {
								// 刷新完成 /初始状态--》 进入 下拉刷新
								state = PULL_TO_REFRESH;
								onHeaderStateChange();
							}
						}
						if (state == PULL_TO_REFRESH) {
							if ((secondTempY - startY) / RATIO > headerHeight
									&& secondTempY - firstTempY > 3) {
								// 下啦刷新 --》 松开刷新
								state = RELEASE_TO_REFERESH;
								onHeaderStateChange();
							} else if (secondTempY - startY <= -5) {
								// 下啦刷新 --》 回到 刷新完成
								state = DONE;
								onHeaderStateChange();
							}
						}
						if (state == RELEASE_TO_REFERESH) {
							if (firstTempY - secondTempY > 5) {
								// 松开刷新 --》回到下拉刷新
								state = PULL_TO_REFRESH;
								isBack = true;// 从松开刷新 回到的下拉刷新
								onHeaderStateChange();
							} else if (secondTempY - startY <= -5) {
								// 松开刷新 --》 回到 刷新完成
								state = DONE;
								onHeaderStateChange();
							}
						}

						if (state == PULL_TO_REFRESH
								|| state == RELEASE_TO_REFERESH) {
							header.setPadding(0, (int) ((secondTempY - startY)
									/ RATIO - headerHeight), 0, 0);
						}
					} else {
					}
				}
				if (getLastVisiblePosition() == getCount() - 2
						|| getLastVisiblePosition() == getCount() - 1) {
					firstTempY = secondTempY;
					secondTempY = ev.getY();
					if (!isRecorded) {
						startY = secondTempY;
						isRecorded = true;
					}

					if (state != REFERESHING) {// 不是正在刷新状态
						if (state == DONE) {
							if (startY - secondTempY > 0) {
								// 刷新完成/初始状态 --》 进入 下拉刷新
								state = PULL_TO_REFRESH;
								onFooterStateChange();
							}
						}
						if (state == PULL_TO_REFRESH) {
							if ((startY - secondTempY) / RATIO > headerHeight
									&& firstTempY - secondTempY >= 9) {
								state = RELEASE_TO_REFERESH;
								onFooterStateChange();
							} else if (startY - secondTempY <= 0) {
								state = DONE;
								onFooterStateChange();
							}
						}
						if (state == RELEASE_TO_REFERESH) {
							if (firstTempY - secondTempY < -5) {
								state = PULL_TO_REFRESH;
								isBack = true;
								onFooterStateChange();
							} else if (secondTempY - startY >= 0) {
								state = DONE;
								onFooterStateChange();
							}
						}
						if ((state == PULL_TO_REFRESH || state == RELEASE_TO_REFERESH)
								&& secondTempY < startY) {
							footer.setPadding(
									0,
									0,
									0,
									(int) ((startY - secondTempY) / RATIO - headerHeight));
						}
					} else {
					}
				}
				break;

			case MotionEvent.ACTION_UP:
				System.out.println("state=" + state);
				if (state != REFERESHING) {
					if (state == PULL_TO_REFRESH) {
						state = DONE;
						if (getFirstVisiblePosition() == 0)
							onHeaderStateChange();
						if (getLastVisiblePosition() == getCount() - 1
								|| getLastVisiblePosition() == getCount() - 2)// 上拉
							onFooterStateChange();
					}

					if (state == RELEASE_TO_REFERESH) {
						state = REFERESHING;
						if (getFirstVisiblePosition() == 0) {
							onHeaderStateChange();
							onPullDownRefresh();
						}
						if (getLastVisiblePosition() == getCount() - 1
								|| getLastVisiblePosition() == getCount() - 2) {
							onFooterStateChange();
							onPullUpRefresh();
						}
					}
				}
				break;
			}
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 更改尾视图显示状态
	 */
	private void onHeaderStateChange() {
		switch (state) {
		case PULL_TO_REFRESH:
			headerProgressBar.setVisibility(View.GONE);
			headerArrow.setVisibility(View.VISIBLE);
			headerTitle.setVisibility(View.VISIBLE);
			headerLastUpdated.setVisibility(View.VISIBLE);

			headerTitle.setText("下拉刷新");
			headerArrow.clearAnimation();
			if (isBack) {
				headerArrow.startAnimation(animation);
				isBack = false;
			}
			break;

		case RELEASE_TO_REFERESH:
			headerProgressBar.setVisibility(View.GONE);
			headerArrow.setVisibility(View.VISIBLE);
			headerTitle.setVisibility(View.VISIBLE);
			headerLastUpdated.setVisibility(View.VISIBLE);

			headerTitle.setText(" 松开刷新");
			headerArrow.clearAnimation();
			headerArrow.startAnimation(reverseAnimation);
			break;

		case REFERESHING:
			headerProgressBar.setVisibility(View.VISIBLE);
			headerArrow.setVisibility(View.GONE);
			headerTitle.setVisibility(View.VISIBLE);
			headerLastUpdated.setVisibility(View.VISIBLE);

			headerTitle.setText("正在刷新");
			headerArrow.clearAnimation();

			header.setPadding(0, 0, 0, 0);
			break;
		case DONE:
			headerProgressBar.setVisibility(View.GONE);
			headerArrow.setVisibility(View.VISIBLE);
			headerTitle.setVisibility(View.VISIBLE);
			headerLastUpdated.setVisibility(View.VISIBLE);
			headerTitle.setText("下拉刷新");
			headerArrow.clearAnimation();
			header.setPadding(0, -1 * headerHeight, 0, 0);
			break;
		}
	}

	/**
	 * 更改尾视图显示状态
	 */
	private void onFooterStateChange() {
		switch (state) {
		case PULL_TO_REFRESH:
			footerProgressBar.setVisibility(View.GONE);
			footerArrow.setVisibility(View.VISIBLE);
			footerTitle.setVisibility(View.VISIBLE);
			footerLastUpdated.setVisibility(View.VISIBLE);

			footerTitle.setText("上拉查看更多");
			footerArrow.clearAnimation();
			if (isBack) {
				footerArrow.startAnimation(reverseAnimation);
				isBack = false;
			}
			break;

		case RELEASE_TO_REFERESH:
			footerProgressBar.setVisibility(View.GONE);
			footerArrow.setVisibility(View.VISIBLE);
			footerTitle.setVisibility(View.VISIBLE);
			footerLastUpdated.setVisibility(View.VISIBLE);

			footerTitle.setText(" 松开刷新");
			footerArrow.clearAnimation();
			footerArrow.startAnimation(animation);
			break;

		case REFERESHING:
			footerProgressBar.setVisibility(View.VISIBLE);
			footerArrow.setVisibility(View.GONE);
			footerTitle.setVisibility(View.VISIBLE);
			footerLastUpdated.setVisibility(View.VISIBLE);

			footerTitle.setText("正在刷新");
			footerArrow.clearAnimation();

			footer.setPadding(0, 0, 0, 0);
			break;
		case DONE:
			footerProgressBar.setVisibility(View.GONE);
			footerArrow.setVisibility(View.VISIBLE);
			footerTitle.setVisibility(View.VISIBLE);
			footerLastUpdated.setVisibility(View.VISIBLE);

			footerTitle.setText("上拉查看更多");
			footerArrow.clearAnimation();

			footer.setPadding(0, -1 * headerHeight, 0, 0);
			break;
		}
	}

	/**
	 * 下拉刷新的实现方法
	 */
	private void onPullDownRefresh() {
		if (rListener != null) {
			rListener.pullDownRefresh();
		}
	}

	/**
	 * 上拉刷新的实现方法
	 */
	private void onPullUpRefresh() {
		if (rListener != null)
			rListener.pullUpRefresh();
	}

}
