package com.st.cms.entity;

/**
 * Created by lining on 2016/7/14.
 */
public class PointSimple {

    // 标记点相对于横向的宽度的比例
    public double width_scale;
    // 标记点相对于横向的高度的比例
    public double height_scale;
    private Winjury winjury;

    public void setWinjury(Winjury winjury) {
        this.winjury = winjury;
    }

    public Winjury getWinjury() {
        return winjury;
    }
}
