package com.rhyme.magicpic.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import com.rhyme.magicpic.BaseApplication;
import com.rhyme.magicpic.entity.BitmapBean;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rhyme on 2017/7/6.
 */

public class BitmapUtil {
     private static Map<String,SoftReference<Bitmap>> imageMap=
             new HashMap<>();

    public static BitmapBean blank;
    public static Bitmap lastBitmap;
    /**
     * 获取当前模式
     * @param type
     */
    public static int Difficute(String type){
        switch (type){
            case "2X2":
                return 4;
            case "3X3":
               return 9;
            case "4X4":
              return 16;
        }
        return 0;
    }
    /**
     * 对原图片进行裁剪切成对应难度的数量块
     * @param type 难度
     * @param bitmap 当前图片
     * @return
     */
    public static List<BitmapBean> createList(String type, Bitmap bitmap){
        int w=bitmap.getWidth();
        int h=bitmap.getHeight();
        List<BitmapBean> bitmapBeans=new ArrayList<>();
        int count=Difficute(type);

        int cw= (int) (w/Math.sqrt(count));
        int ch= (int) (h/Math.sqrt(count));
        try{
            for (int i=0;i<Math.sqrt(count);i++){
                for (int j=0;j<Math.sqrt(count);j++){
                    BitmapBean bean=new BitmapBean((int) (i*Math.sqrt(count)+j+1),Bitmap.createBitmap(bitmap,j*cw,i*ch,cw,ch));
                    bean.setFirstId(bean.getId());
                    bitmapBeans.add(bean);
                }
            }
        }catch (OutOfMemoryError e){
            return null;
        }
        lastBitmap=bitmapBeans.remove(count-1).getBitmap();
        BitmapBean lastbean=new BitmapBean(count,Bitmap.createBitmap(cw,ch, Bitmap.Config.ARGB_4444));
        lastbean.setFirstId(count);
        bitmapBeans.add(lastbean);
        blank=lastbean;//设置为白色块
        return bitmapBeans;
    }

    /**
     * 获取压缩比例
     */
    public static int caculateSampleSize(BitmapFactory.Options options,int reqw,int reqh){
        int w=options.outWidth;
        int h=options.outHeight;
        int SampleSize=1;
            if (w>reqw||h>reqh){
                int wr=Math.round(w*1.0f/reqw);
                int hr=Math.round(h*1.0f/reqh);
                SampleSize=Math.max(wr,hr);
            }
            return SampleSize;
    }

    /**
     * 压缩图片
     */
    public static Bitmap caculateBitmap(Context context,String path, int width, int height){
        int density=ScreenUtil.Density(context);
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        Bitmap bitmap=null;
        bitmap=BitmapFactory.decodeFile(path,options);
        options.inSampleSize= BitmapUtil.caculateSampleSize(options,width*density,height*density);
        options.inPreferredConfig= Bitmap.Config.ARGB_4444;
        /*下面两个字段需要组合使用*/
        options.inPurgeable=true;
        options.inInputShareable=true;
        options.inJustDecodeBounds=false;
        bitmap=BitmapFactory.decodeFile(path,options);
        return bitmap;
    }
    /**
     * 压缩图片
     */
    public static Bitmap caculateBitmap(String path){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        Bitmap bitmap=null;
        bitmap=BitmapFactory.decodeFile(path,options);
        options.inPreferredConfig= Bitmap.Config.ARGB_4444;
        /*下面两个字段需要组合使用*/
        options.inPurgeable=true;
        options.inInputShareable=true;
        options.inJustDecodeBounds=false;
        bitmap=BitmapFactory.decodeFile(path,options);
        return bitmap;
    }
    public static Bitmap loadBitmap(final String path,final ImageCallBack callBack){
        SoftReference<Bitmap> reference=imageMap.get(path);
        if (reference!=null){
            if (reference.get()!=null){
                return reference.get();
            }
        }
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Bitmap bitmap= (Bitmap) msg.obj;
                imageMap.put(path,new SoftReference<Bitmap>(bitmap));
                if (callBack!=null){
                    callBack.getBitmap(bitmap);
                }
            }
        };
        new Thread(){
            @Override
            public void run() {
                Message message=handler.obtainMessage();
                message.obj=BitmapFactory.decodeFile(path);
                handler.sendMessage(message);
            }
        }.start();
        return null;
    }

    /**
     * 文件加载的形式加载图片,有缓存
     * @param path
     * @return
     */
    public static Bitmap loadBitmap(String path){
       Bitmap bitmap= BaseApplication.lruBitmapUtil.getBitmapToMemoryCache(path);
        if (bitmap==null){
            bitmap=BitmapFactory.decodeFile(path);
            BaseApplication.lruBitmapUtil.addBitmapToMemoryCache(path,bitmap);
        }
        return bitmap;
    }


    interface  ImageCallBack{
        void getBitmap(Bitmap bitmap);
    }
}
