package com.rhyme.magicpic.entity;

import android.graphics.Bitmap;

/**
 * Created by rhyme on 2017/7/6.
 */

public class BitmapBean {
    private int firstId;
    private int id;
    private Bitmap bitmap;

    public BitmapBean() {
    }
    public BitmapBean(int id, Bitmap bitmap) {
        this.id = id;
        this.bitmap = bitmap;
    }

    public int getFirstId() {
        return firstId;
    }

    public void setFirstId(int firstId) {
        this.firstId = firstId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
