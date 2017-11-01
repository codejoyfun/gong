package com.kids.commonframe.base;

import android.app.Activity;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by myChaoFile on 16/3/21.
 */
public class ActivityManager {
    private Set<Activity> activityList;

    private ActivityManager() {
        activityList = new LinkedHashSet<Activity>();
    }

    private static ActivityManager instance;

    public static ActivityManager getInstance() {
        if (instance == null) {
            synchronized (ActivityManager.class) {
                if (instance == null) {
                    instance = new ActivityManager();
                }
            }
        }
        return instance;
    }

    public void addActivity(Activity activity) {
        try {
            // 新加进来的Activity进行主题的设置
            synchronized (this) {
                activityList.add(activity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getActSize() {
        return activityList.size();
    }

    public Activity getActivity(String simpleName) {
        Activity activity = null;
        synchronized (this) {
            if (activityList != null && activityList.size() > 0) {
                for (Activity a : activityList) {
                    if (simpleName.equals(a.getClass().getSimpleName())) {
                        activity = a;
                        break;
                    }
                }
            }
        }
        return activity;
    }

    /**
     * 获取栈中上一个activity的名字
     * 用于查找avtivity的跳转来源
     */
    public String preActvityName() {
        if (activityList != null && activityList.size() > 1) {
            Object[] stack = activityList.toArray();
            Activity activity = (Activity) stack[stack.length - 2];
            if (activity != null) {
                return activity.getClass().getSimpleName();
            }
        }
        return null;
    }

    public void finishAll() {
        synchronized (this) {
            if (activityList != null && activityList.size() > 0) {
                for (Activity a : activityList) {
                    a.finish();
                }
            }
        }
    }

    // 有Activity手动finish的时候需要将其引用在集合中删除
    public void removeActivity(Activity activity) {
        synchronized (this) {
            if (!activityList.isEmpty() && activityList.contains(activity))
                activityList.remove(activity);
        }
    }

    public void returnHomePage(Class classInstance) {
        synchronized (this) {
            if (activityList != null && activityList.size() > 0) {
                for (Activity a : activityList) {
                    if (!classInstance.isInstance(a)) {
                        a.finish();
                    }
                }
            }
        }
    }
}
