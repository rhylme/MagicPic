package com.rhyme.magicpic.util;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by rhyme on 2017/7/6.
 */

public class LruBitmapUtil {
    private static final String TAG="LruBitmapUtil";

    private LruCache<String,Bitmap> mMemoryCache;
    private int MAXMEMONRY=(int)(Runtime.getRuntime().maxMemory()/1024);
    public LruBitmapUtil(){
        if (mMemoryCache==null){
            mMemoryCache=new LruCache<String, Bitmap>(MAXMEMONRY/8){
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getRowBytes()*value.getHeight()/1024;
                }

                @Override
                protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
//                    super.entryRemoved(evicted, key, oldValue, newValue);
                    LogUtil.ShowLog(3, TAG,"hard cache if full,push to soft cache");
                }
            };
        }
    }
    public void clearCache(){
        if (mMemoryCache!=null){
            if (mMemoryCache.size()>0){
                LogUtil.ShowLog(0,TAG,"缓存大小:"+mMemoryCache.size());
                mMemoryCache.evictAll();
                LogUtil.ShowLog(0,TAG,"缓存大小:"+mMemoryCache.size());
            }
            mMemoryCache=null;
        }
    }
    public synchronized void addBitmapToMemoryCache(String key,Bitmap bitmap){
        if (mMemoryCache.get(key)==null){
            if (key!=null&&bitmap!=null){
                mMemoryCache.put(key,bitmap);
            }
        }else {
            LogUtil.ShowLog(4,TAG,"该程序已经退出");
        }
    }
    public synchronized Bitmap getBitmapToMemoryCache(String key){
        if (key!=null){
            Bitmap bp=mMemoryCache.get(key);
            return bp;
        }
        return null;
    }

    public synchronized void removeImageCache(String key){
        if (key!=null){
            if (mMemoryCache!=null){
                    Bitmap bp=mMemoryCache.remove(key);
                if (bp!=null){
                    bp.recycle();
                }
            }
        }
    }
}
