package com.dingmouren.wallpager.model.dao;

import io.realm.RealmObject;

/**
 * Created by dingmouren on 2017/5/16.
 * 频道类：新作，精选，热门，建筑，饮食，自然，物品，人物，科技，星空，作者
 */

public class Channel extends RealmObject {
    private String name;
    private boolean isFixed;
    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFixed() {
        return isFixed;
    }

    public void setFixed(boolean fixed) {
        isFixed = fixed;
    }
}