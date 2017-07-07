package com.rhyme.magicpic.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.rhyme.magicpic.entity.Main_Pic;
import com.rhyme.magicpic.util.BitmapUtil;
import com.rhyme.magicpic.util.ScreenUtil;

import java.util.List;

/**
 * Created by rhyme on 2017/7/6.
 */

public class MainAdapter extends BaseAdapter {
    private List<Main_Pic> main_pics;
    private Context context;
    public MainAdapter(Context context, List<Main_Pic> main_pics){
        this.main_pics=main_pics;
        this.context=context;
    }
    @Override
    public int getCount() {
        return main_pics.size();
    }

    @Override
    public Object getItem(int i) {
        return main_pics.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView=null;
        int density= ScreenUtil.Density(context);
        if (view==null){
            imageView=new ImageView(context);
            imageView.setLayoutParams(new AbsListView.LayoutParams(80*density,110*density));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }else {
            imageView= (ImageView) view;
        }
        imageView.setImageResource(0);
        Main_Pic main_pic=main_pics.get(i);
        if (main_pic.getPath()!=null){
            try{
                imageView.setImageBitmap(BitmapUtil.caculateBitmap(context,main_pic.getPath(),80,110));
            }catch (OutOfMemoryError e){
                e.printStackTrace();
            }
        }else {
            imageView.setImageResource(main_pic.getResource());
        }
        return imageView;
    }
}
