package com.kids.commonframe.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kids.commonframe.R;
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;


public class LoadingLayout extends FrameLayout  implements OnClickListener{
	enum Status{
		LOADING,RETRY,NOTHING
	}
	private TextView loadingReTry;
	private View viewLoadingReTry;
	private TextView noDataText;
	private ProgressBar loadingProgress;
	private LinearLayout retryLayout;
	private ImageView loadingIcon;

	private LoadingLayoutInterface retryInterface;
	public LoadingLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public LoadingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public LoadingLayout(Context context) {
		super(context);
		init(context);
	}
	private void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.loading_layout, null);

		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		loadingReTry = (TextView)view.findViewById(R.id.loadingReTry);
		viewLoadingReTry = view.findViewById(R.id.rl_refresh);
		noDataText = (TextView)view.findViewById(R.id.noDataText);
		loadingProgress = (ProgressBar)view.findViewById(R.id.loadingProgress);
		retryLayout = (LinearLayout)view.findViewById(R.id.retryLayout);
		loadingIcon = (ImageView) view.findViewById(R.id.loadingIcon);
		loadingReTry.setOnClickListener(this);
		viewLoadingReTry.setOnClickListener(this);
		addView(view);
		this.setVisibility(View.GONE);
	}

	public void setOnRetryClickListener(LoadingLayoutInterface retryInterface) {
		this.retryInterface = retryInterface;
	}

	private void setStatus(Status status, int drawId) {
		this.setVisibility(View.VISIBLE);
		switch (status) {
			case LOADING:
				retryLayout.setVisibility(View.GONE);
				loadingProgress.setVisibility(View.VISIBLE);
				loadingIcon.setVisibility(View.GONE);
				break;
			case RETRY:
				loadingProgress.setVisibility(View.GONE);
				retryLayout.setVisibility(View.VISIBLE);
				loadingReTry.setVisibility(View.VISIBLE);
				viewLoadingReTry.setVisibility(View.VISIBLE);
				noDataText.setVisibility(View.VISIBLE);
				if ( drawId < 0 ) {
					loadingIcon.setVisibility(View.GONE);
				}
				else {
					loadingIcon.setImageResource(drawId);
					loadingIcon.setVisibility(View.VISIBLE);
				}
				break;
			case NOTHING:
				loadingProgress.setVisibility(View.GONE);
				retryLayout.setVisibility(View.VISIBLE);
				loadingReTry.setVisibility(View.GONE);
				viewLoadingReTry.setVisibility(View.GONE);
				noDataText.setVisibility(View.VISIBLE);
				if ( drawId < 0 ) {
					loadingIcon.setVisibility(View.GONE);
				}
				else {
					loadingIcon.setImageResource(drawId);
					loadingIcon.setVisibility(View.VISIBLE);
				}
				break;
			default:
				break;
		}
	}
	/**
	 * 切换loading状态
	 */
	public void setStatusLoading() {
		setStatus(Status.LOADING,-1);
	}
	/**
	 * 重试状态
	 */
	public void setStatusRetry() {
		setStatusRetry(-1);
	}
	public void setStatusRetry(int drawId) {
		setStatus(Status.RETRY,drawId);
	}
	/**
	 * 没有数据状态
	 */
	public void setStatusNothing() {
		setStatusNothing(-1);
	}
	public void setStatusNothing(int drawId) {
		setStatus(Status.NOTHING,drawId);
	}

	public void onSuccess (int returnCount,String noDataHint) {
		onSuccess(returnCount,noDataHint,-1);
	}
	public void onSuccess (int returnCount,String noDataHint,int drawableId) {
		if ( returnCount >0 ) {
			setVisibility(View.GONE);
		}
		else {
			setVisibility(View.VISIBLE);
			setStatusNothing(drawableId);
			noDataText.setText(noDataHint);
		}
	}
	public void onFailure(String message) {
		onFailure(message,-1);
	}

	public void onFailure(String message,int drawableId) {
		setVisibility(View.VISIBLE);
		setStatus(Status.RETRY,drawableId);
		noDataText.setText(getContext().getString(R.string.page_network_error));
	}

	@Override
	public void onClick(View v) {
		if ( retryInterface != null ) {
			retryInterface.retryOnClick(v);
		}
	}
}
