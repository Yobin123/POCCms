package com.st.cms.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by jt on 2016/8/31.
 */
public class TestView extends EditText{
    public TestView(Context context) {
        this(context, null);
    }

    public TestView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
