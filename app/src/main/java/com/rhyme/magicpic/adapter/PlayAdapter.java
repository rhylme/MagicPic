package com.rhyme.magicpic.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.rhyme.magicpic.entity.BitmapBean;
import com.rhyme.magicpic.util.ScreenUtil;

import java.util.List;

/**
 * Created by rhyme on 2017/7/6.
 */

public class PlayAdapter extends BaseAdapter {
    private List<BitmapBean> bitmapBeanList;
    private Context context;

    public PlayAdapter(Context context, List<BitmapBean> bitmapBeanList) {
        this.bitmapBeanList = bitmapBeanList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return bitmapBeanList.size();
    }

    @Override
    public Object getItem(int i) {
        return bitmapBeanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView = null;
        int w = 0;
        int h = 0;
        if (view == null) {
            imageView = new ImageView(context);
            int density = ScreenUtil.Density(context);
            if (getCount() == 16) {
                w = 80 * density;
                h = 110 * density;
            } else if (getCount() == 9) {
                w = 107 * density;
                h = 146 * density;
            } else if (getCount() == 4) {
                w = 165 * density;
                h = 220 * density;
            }
            imageView.setLayoutParams(new AbsListView.LayoutParams(w, h));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            imageView = (ImageView) view;
        }
        BitmapBean bitmapBean = bitmapBeanList.get(i);
        imageView.setImageBitmap(bitmapBean.getBitmap());
        return imageView;
    }
}
