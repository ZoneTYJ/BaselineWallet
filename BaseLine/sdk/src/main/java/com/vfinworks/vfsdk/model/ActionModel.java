package com.vfinworks.vfsdk.model;

/**
 * Created by xiaoshengke on 2017/2/22.
 */

public class ActionModel {
    public String operationName;
    public String title;
    public int resId;

    public ActionModel(String operationName, String title, int resId) {
        this.operationName = operationName;
        this.title = title;
        this.resId = resId;
    }
}
