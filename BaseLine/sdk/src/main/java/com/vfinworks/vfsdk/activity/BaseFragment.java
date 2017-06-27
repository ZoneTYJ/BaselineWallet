package com.vfinworks.vfsdk.activity;

import android.os.Bundle;

import java.lang.ref.WeakReference;

/**
 * Created by xiaoshengke on 2016/4/5.
 */
public class BaseFragment extends android.support.v4.app.Fragment {
    protected WeakReference<BaseActivity> activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = new WeakReference<BaseActivity>((BaseActivity) getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity = null;
    }

    public void showProgress(){
        if(!activityIsNull()) {
            BaseActivity ba = activity.get();
            if(!ba.mLoadingDialog.isShowing())
                ba.mLoadingDialog.show();
        }
    }

    public void hideProgress(){
        if(!activityIsNull())
            activity.get().mLoadingDialog.dismiss();
    }

    private boolean activityIsNull(){
        if(activity == null && activity.get() == null)
            return true;
        return false;
    }
}
