package com.rhyme.magicpic;

import android.app.Application;

import com.rhyme.magicpic.util.LruBitmapUtil;

/**
 * Created by rhyme on 2017/7/6.
 */

public class BaseApplication extends Application {
    public static LruBitmapUtil lruBitmapUtil;
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化缓存
        lruBitmapUtil=new LruBitmapUtil();
    }


}
