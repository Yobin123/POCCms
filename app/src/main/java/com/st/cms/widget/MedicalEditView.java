package com.st.cms.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.st.cms.utils.DensityUtil;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/8/31.
 */
public class MedicalEditView extends MedicalSelectView{
    private static TextChangedActionListener actionListener;
    public MedicalEditView(Context context) {
        this(context, null);
    }

    public MedicalEditView(Context context, AttributeSet attrs) {
        //这里构造方法也很重要，不加这个很多属性不能再XML里面定义
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public MedicalEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    @Override
    public void initData() {
        mtv_textColor = getResources().getColor(R.color.lime);
        mtv_text = "";
        mtv_textSize = DensityUtil.dip2px(getContext(), 16);
        mtv_backgroundColor = getResources().getColor(R.color.switch_thumb_normal_material_light);
        mtv_src = R.mipmap.dropdown_ic_arrow_normal_holo_dark;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if(actionListener != null)
        {
            actionListener.textChangedAction(this);
        }
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }
    public static void setTextChangedActionListener(TextChangedActionListener textChangedActionListener)
    {
        actionListener = textChangedActionListener;
    }
    public interface TextChangedActionListener
    {
        public void textChangedAction(MedicalEditView medicalEditView);
    }
}
