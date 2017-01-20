package com.st.cms.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jt on 2016/9/28.
 * 受伤部位区域定义
 */
public class SitePoint {
    private static List<SitePoint> front_sitePoints = null;
    private static List<SitePoint> back_sitePoints = null;
    static
    {
        front_sitePoints = new ArrayList<>();
        back_sitePoints = new ArrayList<>();
    }
    private String name;
    private float leftScale;
    private float topScale;
    private float rightScale;
    private float bottomScale;
    private float x_scale;
    private float y_scale;
    //0正面，1反面
    private int surface;
    private String profile;

    public SitePoint() {
    }

    public SitePoint(String name, float leftScale, float topScale, float rightScale, float bottomScale, int surface) {
        this.name = name;
        this.leftScale = leftScale;
        this.topScale = topScale;
        this.rightScale = rightScale;
        this.bottomScale = bottomScale;
        this.surface = surface;
        if(surface == 0)
        {
            front_sitePoints.add(this);
        }
        else
        {
            back_sitePoints.add(this);
        }
    }

    public static List<SitePoint> getFront_sitePoints() {
        return front_sitePoints;
    }

    public static List<SitePoint> getBack_sitePoints() {
        return back_sitePoints;
    }

    public static SitePoint checkSite(float x_scale, float y_scale, int surface)
    {
        SitePoint sitePoint = null;
        List<SitePoint> sitePoints = surface == 0 ? front_sitePoints : back_sitePoints;
        for(int i = 0;i < sitePoints.size();i++)
        {
            sitePoint = sitePoints.get(i);
            if(!(x_scale >= sitePoint.leftScale && x_scale <= sitePoint.rightScale))
            {
                continue;
            }
            if(!(y_scale >= sitePoint.topScale && y_scale <= sitePoint.bottomScale))
            {
                continue;
            }
            sitePoint.x_scale = (sitePoint.leftScale + sitePoint.rightScale) / 2;
            sitePoint.y_scale = (sitePoint.topScale + sitePoint.bottomScale) / 2;
            return sitePoint;
        }
        return null;
    }

    public float getX_scale() {
        return x_scale;
    }


    public float getY_scale() {
        return y_scale;
    }

    public String getName() {
        return name;
    }

    public int getSurface() {
        return surface;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
