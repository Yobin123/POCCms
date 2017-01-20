package com.st.cms.utils;

import android.content.Context;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/10/9.
 */
public class PropertiesUtils {
    public static Properties loadConfig(Context context, String file) {
        Properties properties = new Properties();
        try {
            FileInputStream s = new FileInputStream(file);
            properties.load(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }
    public static Properties loadConfig(Context context)
    {
        Properties properties = new Properties();
        try {
            properties.load(context.getResources().openRawResource(R.raw.version));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
