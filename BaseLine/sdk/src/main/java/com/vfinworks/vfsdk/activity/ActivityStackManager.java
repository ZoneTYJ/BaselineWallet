package com.vfinworks.vfsdk.activity;
import android.app.Activity;
import android.content.Intent;

import java.util.Stack;

public class ActivityStackManager {
	private Stack<Activity> activityStack = null;
	private static ActivityStackManager instance;
	private  ActivityStackManager(){
	}
	
	public static synchronized ActivityStackManager getInstance(){
		if(instance==null){
			instance=new ActivityStackManager();
		}
		return instance;
	}
	
	/*
	 * 退出当前activity
	 */
	public synchronized void popActivity(){
		Intent resultIntent=null;
		Activity activity=activityStack.pop();
		if(activity instanceof BaseActivity){
			BaseActivity act= (BaseActivity) activity;
			if(act!=null){
				resultIntent=act.getResultIntent();
			}
		}
		if(activity!=null){
			activity.finish();
			activity=null;
		}
		if(resultIntent!=null) {
			BaseActivity baseActivity = (BaseActivity) activityStack.peek();
			baseActivity.onIntentForResult(resultIntent);
		}
	}
	
	/*
	 * 退到指定atcivity
	 */
	public synchronized void popActivityExceptOne(Class cls){
		while(true) {
			Activity activity=currentActivity();
			if(activity == null) {
				break;
			}
			if(activity.getClass().equals(cls) ){
				break;
			}
			popActivity();
		}
	}
	
	/*
	 * 退出所有的activity
	 */
	public synchronized void popAllActivity(){
		while(true) {
			Activity activity=currentActivity();
			if(activity == null) {
				break;
			}
			popActivity();
		}
	}
	
	/*
	 * 得到栈顶的activity 不退栈 
	 */
	public Activity currentActivity(){
		Activity activity = null;
		try {
			activity=activityStack.peek();
		}catch(Exception e) {
			return null;
		}
		return activity;
	}
	
	/*
	 * 入栈
	 */
	public synchronized void pushActivity(Activity activity){
		if(activityStack==null){
			activityStack=new Stack<Activity>();
		}
		activityStack.push(activity);
	}

	public synchronized void removeActivity(Activity activity){
		if(activityStack != null){
			activityStack.remove(activity);
		}
	}
}


