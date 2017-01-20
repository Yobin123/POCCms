package com.st.cms.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by CW on 2016/8/24.
 */
public class IntentGoTo {

    /**
     * 页面跳转
     * @param context
     * @param cls
     */
    public static void intentActivity(Context context, Class<?> cls){
        Intent intent = new Intent();
        intent.setClass(context,cls);
        context.startActivity(intent);
    }

    /**
     * 带一个值得页面跳转
     * @param context
     * @param cls
     * @param arg
     */
    public static void intentActivity(Context context, Class<?> cls, String arg){
        Intent intent = new Intent();
        intent.setClass(context,cls);
        Bundle bundle = new Bundle();
        bundle.putString("id",arg);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 带两个值的页面跳转
     * @param context
     * @param cls
     * @param arg
     * @param index
     */
    public static void intentActivity(Context context, Class<?> cls, String arg, int index){
        Intent intent = new Intent();
        intent.setClass(context,cls);
        Bundle bundle = new Bundle();
        bundle.putString("id",arg);
        bundle.putInt("index",index);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
