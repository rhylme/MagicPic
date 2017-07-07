package com.rhyme.magicpic.entity;

import java.io.Serializable;

/**
 * Created by rhyme on 2017/7/6.
 */

public class Main_Pic implements Serializable{
    private int id;
    private String path;
    private int resource;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public int getResource() {
        return resource;
    }
    public void setResource(int resource) {
        this.resource = resource;
    }
}
