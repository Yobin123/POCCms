package com.st.cms.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

import com.st.cms.fragment.ConveyDetailFragment;
import com.st.cms.utils.DensityUtil;

/**
 * Created by jt on 2016/9/20.
 */
public class ScrollBottomScrollView extends ScrollView {

    private ScrollBottomListener scrollBottomListener;
    private UnScrollBottomListener unScrollBottomListener;

    public ScrollBottomScrollView(Context context) {
        super(context);
    }

    public ScrollBottomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollBottomScrollView(Context context, AttributeSet attrs,int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt){
//        Log.e("TEST", (t + getHeight() + DensityUtil.dip2px(getContext(), 72)) + "----" + computeVerticalScrollRange());
        if(t + getHeight() + DensityUtil.dip2px(getContext(), 72) >=  computeVerticalScrollRange()){
            //ScrollView滑动到底部了
            scrollBottomListener.scrollBottom();
        }
        else
        {
            unScrollBottomListener.unScrollBottom();
        }
    }

    public void setScrollBottomListener(ScrollBottomListener scrollBottomListener){
        this.scrollBottomListener = scrollBottomListener;
    }

    public void setUnScrollBottomListener(UnScrollBottomListener unScrollBottomListener) {
        this.unScrollBottomListener = unScrollBottomListener;
    }

    public interface ScrollBottomListener{
        public void scrollBottom();
    }
    public interface UnScrollBottomListener{
        public void unScrollBottom();
    }
}