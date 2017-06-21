package com.kids.commonframe.base;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

/**
 * 观察者管理器
 * 用于管理不同界面数据刷新
 * */
public class ObservableManager {
	private List<ObservableInterface> observablesList;
	private static ObservableManager instance;
    private ObservableManager(){
    	observablesList = new ArrayList<ObservableInterface>();
    }
	public static ObservableManager getInstance(Context context) {
		if (instance == null) {
			synchronized (ObservableManager.class) {
				if (instance == null) {
					instance = new ObservableManager();
				}
			}
		}
		return instance;
	}
	
    public void registerDataSetObserver(ObservableInterface observer) {
    	if ( observer == null) {
    		return;
    	}
    	if ( !observablesList.contains(observer)) {
    		observablesList.add(observer);
    	}
    }
    public void unregisterDataSetObserver(ObservableInterface observer) {
    	if ( observer == null) {
    		return;
    	}
    	observablesList.remove(observer);
    }
    public void notifyDataSetChanged(Intent data) {
    	for (ObservableInterface callBack : observablesList) {
    		callBack.dataChanged(data);
    	}
    }
	public interface ObservableInterface {
		/**
		 * 数据发生改变时更新其他界面，会回调该方法
		 * @param data  携带数据会放入Intent中
		 */
		void dataChanged(Intent data);
	}
}
