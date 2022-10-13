package com.youer.androidui.coordinate;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

/**
 * @author youer
 * @date 2022/10/12
 */
public class MyBottomSheetBehavior extends BottomSheetBehavior {
    // true表明由FastBottomBehavior去处理滑动逻辑，false表明由子view来处理逻辑
    private static final String TAG = "MyBottomSheetBehavior";
    private boolean intercept = true;
    private float downY;

    public MyBottomSheetBehavior() {
    }

    public MyBottomSheetBehavior(@NonNull Context context,
        @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull View child,
        @NonNull MotionEvent event) {
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - downY;
                //防止是按下也判断
                if (Math.abs(dy) > 5) {
                    // 下拉并且图片为最大的时候，由FastBottomBehavior去处理逻辑，支持下拉关闭页面
                    if (dy > 0 && intercept) {
                        Log.d(TAG, "onInterceptTouchEvent: true");
                        return true;
                    } else {
                        Log.d(TAG, "onInterceptTouchEvent: false");
                        return false;
                    }
                }
                break;
        }
        return super.onInterceptTouchEvent(parent, child, event);
    }

    public MyBottomSheetBehavior setIntercept(boolean intercept) {
        this.intercept = intercept;
        return this;
    }

}