package com.vfinworks.vfsdk.interfacemanager;

import android.content.Context;
import android.os.Handler;

import com.vfinworks.vfsdk.context.BaseContext;

/**
 * Created by tangyijian on 2016/5/5.
 */
abstract public class SignInterface {
    public static final int SIGNOK = 1; //加签成功后的handler
    /**
     * 设置加签方法,修改baseContext中的sign和signType
     * 使用baseContext.setsign和使用baseContext.setSignType
     * 加签完后，用sendHandler发送
     * @param context 上下文
     * @param baseContext 加签后返回的对象
     * @param source 需要加签的参数
     * @param handler
     */
    abstract public void sign(final Context context,final BaseContext baseContext, final String source,Handler handler);

    public void sendHandler(final BaseContext baseContext,final Handler handler){
        handler.obtainMessage(SIGNOK,baseContext).sendToTarget();
    }
}
