package com.st.cms.utils;

/**
 * Created by jt on 2016/9/20.
 */
public class Constants {
    //    public static String DOMAIN = "http://10.41.87.42:8080/";
    public static String IP = "180.101.8.180";
    public static int PORT = 61613;
    //域名
    public static String DOMAIN = "http://180.101.8.180:61613/";
//    public static String DOMAIN = "http://10.41.87.81:8080/";
//    public static String DOMAIN = "http://10.41.87.19:8080/";

    //前缀路径
    public static String PRE_SITE = "site/ws/cms/";
    //获取所有tag
    public static String GET_ALL_TAG = "getallperson";
    //比对脸部
    public static String FACE = "face";
    //保存tag
    public static String SAVE_TAG = "savetag";
    //标记伤处
    public static String MARK_INJURY = "markinjury";
    //获取伤处
    public static String GET_INJURY = "injury";
    //获取医疗资源信息
    public static String GET_MEDICAL = "medical";
    //保存医疗资源使用信息
    public static String TREAT = "treate";
    //设置医院
    public static final String SET_HOSPITAL = "hospital";

    public static final String TREAT2 = "treate2";
    public static String HELLO = "hello";
    //保存gcs数据
    public static String GCS = "gcs";
//    设置相应个人信息
    public static final String SET_BINDPRESON ="bindperson";

    public static String getFaceUrl(String tagId){
        return DOMAIN + PRE_SITE + tagId;
    }
    public static String getUrl(String suffix) {

        return DOMAIN + PRE_SITE + suffix;
    }
    public static String getIdentifyUrl(String nric,String no){

        return  DOMAIN + PRE_SITE+"getperson/"+ nric+"/"+no;
    }
    public static String getIdentifyUrl(){

        return  DOMAIN + PRE_SITE+"getperson/";
    }
    public static String getPersonInformation(){
        return DOMAIN +PRE_SITE+SET_BINDPRESON;
    }

}


