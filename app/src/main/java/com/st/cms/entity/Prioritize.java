package com.st.cms.entity;

/**
 * Created by jt on 2016/8/18.
 */
public class Prioritize {
    private int id;
    //tag手环id
    private String tagId;
    //地区
    private String zone;
    //地点
    private String area;
    //时间戳
    private long timestamp;
    //伤患id
    private int victimId;
    //定义受伤级别
    private int p_lv;
    private int hospitalId;
    private int ambulanceId;
    //0代表未到达，1代表已到达
    private int isArrived;
    //以lat，lon格式传入
    private String coordinate;
    //说明，暂定为病史
    private String profile;
    //以下为预留字段
    private int allowanceInt;
    private String allowanceString;
    //以下是对应的人脸，指纹，以及视屏，在手机上使用acache类进行缓存，所以这些字段是用key值表示的。
    private String allowanceObj1;
    private String allowanceObj2;
    private String allowanceObj3;

}
