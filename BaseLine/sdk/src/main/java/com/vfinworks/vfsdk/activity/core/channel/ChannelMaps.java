package com.vfinworks.vfsdk.activity.core.channel;

import android.content.Context;

import com.vfinworks.vfsdk.annotation.ChannelAnnotation;
import com.vfinworks.vfsdk.model.ChannelModel;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

/**
 * Created by tangyijian on 2016/10/27.
 */
public class ChannelMaps {
    private static ChannelMaps mChannelMaps;

    private Map<ChannelModel,BaseChannel> maps;

    public static ChannelMaps getInstance(){
        if(mChannelMaps==null){
            synchronized(ChannelMaps.class) {
                if (mChannelMaps == null) {
                    mChannelMaps = new ChannelMaps();
                }
            }
        }
        return mChannelMaps;
    }

    public void init(Context ctx){
        maps = new HashMap();
        scan(ctx, "com.vfinworks.vfsdk.activity.core.channel");
    }

    public void clear(){
        for(Map.Entry<ChannelModel,BaseChannel> entry : maps.entrySet()){
            entry.getValue().clear();
        }
    }

    public void clearChannel(){
        maps.clear();
    }

    public Map<ChannelModel,BaseChannel> scan(Context ctx, String entityPackage) {
        try {
            PathClassLoader classLoader = (PathClassLoader) Thread
                    .currentThread().getContextClassLoader();
            DexFile dex = new DexFile(ctx.getPackageResourcePath());
            Enumeration<String> entries = dex.entries();
            while (entries.hasMoreElements()) {
                String entryName = entries.nextElement();
                if (entryName.contains(entityPackage)) {
                    Class<?> entryClass = Class.forName(entryName, true,classLoader);//疑问：Class.forName(entryName);这种方式不知道为什么返回null
                    ChannelAnnotation annotation = entryClass.getAnnotation(ChannelAnnotation.class);
                    if (annotation != null) {
                        BaseChannel baseChannel = (BaseChannel) entryClass.newInstance();
                        ChannelModel channelModel=new ChannelModel(annotation.inst_code(),annotation.pay_mode());
                        maps.put(channelModel, baseChannel);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maps;
    }


    public BaseChannel getChannel(ChannelModel key){
        return maps.get(key);
    }

    synchronized public void addChannel(ChannelModel channelModel,BaseChannel baseChannel){
        maps.put(channelModel,baseChannel);
    }

}


