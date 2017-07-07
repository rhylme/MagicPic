package com.rhyme.magicpic.util;

import android.util.Log;

/**
 * Created by rhyme on 2017/7/6.
 */

public class LogUtil {
    private static final boolean DEBUG=true;


    public static void ShowLog(int i,String TAG,String content){
        switch (i){
            case 0:
                Log.d(TAG,content);
                break;
            case 1:
                Log.i(TAG,content);
                break;
            case 2:
                Log.e(TAG,content);
                break;
            case 3:
                Log.v(TAG,content);
                break;
            case 4:
                Log.w(TAG,content);
                break;
            case 5:
                Log.wtf(TAG,content);
                break;
        }
    }
}
