package com.kids.commonframe.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.R;

public class IPullToRefreshListView extends PullToRefreshListView {
	private final int MIN_DATA_SEIZE = 10;
	private Context context;
    private boolean  footRefreshing;
    private FooterRefreshListener footerFreshListener;
    private View footView;
    private LayoutInflater layoutInflater;
    private ListView listView;
	public IPullToRefreshListView(Context context) {
		super(context);
		init( context );
	}

	public IPullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init( context );
	}

	public IPullToRefreshListView(Context context, Mode mode) {
		super(context, mode);
		init( context );
	}

	public IPullToRefreshListView(Context context, Mode mode, AnimationStyle style) {
		super(context, mode, style);
		init( context );
	}
	private void init (Context context) {
		this.context = context;
		this.setOnLastItemVisibleListener(new HanderLastItemListener());
		listView = this.getRefreshableView();
		layoutInflater = LayoutInflater.from(context);
		footView = layoutInflater.inflate(R.layout.pull_to_refresh_foot_layout,  null );
	}
	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
	}

	private class HanderLastItemListener implements OnLastItemVisibleListener{
	    @Override
	   public void onLastItemVisible() {
//	    	System.out.println("footRefreshing:"+footRefreshing);
		  if ( !footRefreshing && footerFreshListener != null ) {
			  setRefreshingStart();
			  footerFreshListener.footerRefreshing();
		  }
	    }
	}
	public void setRefreshingStart () {
		 footRefreshing = true; 
	}
	public void setRefreshingEnd () {
		footRefreshing = false;
	}
	//－－－－－－－－－－－－－－－－－－实现方法－－－－－－－－－－－－－－－－－
	/**
	 * 初始化调用一次设置尾部
	 * @param dataSize 适配器数据集合大小
	 * @param dataAllSize 所有数据大小
	 */
	public void initFooterView(int dataSize ,int dataAllSize) {
//		System.out.println("dataSize:"+dataSize+",dataAllSize:"+dataAllSize+",listView.getFooterViewsCount():"+listView.getFooterViewsCount());
			if ( dataSize >= MIN_DATA_SEIZE && dataSize<dataAllSize ) {
					listView.addFooterView( footView );
			}
			else if ( dataSize < MIN_DATA_SEIZE && listView.getFooterViewsCount() == 1) {
				listView.removeFooterView(footView);
			}
	}
	public void setOnFooterRefreshListener (FooterRefreshListener footerRefreshListener) {
	    this.footerFreshListener = footerRefreshListener;	
	}
	public void onFooterRefreshComplete(int dataAllSize) {
		setRefreshingEnd();
		Adapter adapter = listView.getAdapter();
		if ( adapter!= null && adapter.getCount()>= dataAllSize) {
			if ( listView.getFooterViewsCount() > 1 ) {
			listView.removeFooterView( footView );
			}
		}
		else if ( adapter!= null && adapter.getCount()< dataAllSize ){
			System.out.println(listView.getFooterViewsCount());
			if ( listView.getFooterViewsCount() == 0 ) {
			listView.addFooterView( footView );
			}
		}
	}
	
	public interface FooterRefreshListener  {
		 void footerRefreshing();
	}
}
