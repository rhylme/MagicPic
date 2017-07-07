package com.rhyme.magicpic.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by rhyme on 2017/7/6.
 * 获取屏幕尺寸
 */

public class ScreenUtil {

    public static int Density(Context context){
        DisplayMetrics metrics=new DisplayMetrics();
        WindowManager manager= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metrics);
        return (int) metrics.density;
    }
    public static DisplayMetrics Metrics(Context context){
        DisplayMetrics metrics=new DisplayMetrics();
        WindowManager manager= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }
}
