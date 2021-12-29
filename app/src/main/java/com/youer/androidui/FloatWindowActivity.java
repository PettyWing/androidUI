package com.youer.androidui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import com.youer.ui.floatwindow.FloatWindow;
import com.youer.ui.floatwindow.FloatWindow.Builder;
import com.youer.ui.floatwindow.Screen;
import com.youer.ui.floatwindow.ViewStateListener;
import com.youer.ui.floatwindow.permission.FloatPermissionListener;

/**
 * @author youer
 * @date 2021/12/29
 */
public class FloatWindowActivity extends Activity {
    private static final String TAG = "FloatWindowActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float_window);
    }

    public void openFloatWindow(View view) {
        LinearLayout floatView = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.float_view, null);
        FloatWindow floatWindow = new Builder(this, floatView)
            .setWidth(Screen.WIDTH, 0.5f)
            .setHeight(Screen.HEIGHT, 0.3f)
            .setStartX(Screen.WIDTH, 0.5f)
            .setViewStateListener(new ViewStateListener() {
                @Override
                public void onShow() {
                    Log.d(TAG, "onShow() called");
                }

                @Override
                public void onDismiss() {
                    Log.d(TAG, "onDismiss() called");
                }

                @Override
                public void onActionDown(MotionEvent event) {
                    Log.d(TAG, "onActionDown() called with: event = [" + event + "]");
                }

                @Override
                public void onActionMove(MotionEvent event, float movedX, float movedY) {
                    Log.d(TAG,
                        "onActionMove() called with: event = [" + event + "], movedX = [" + movedX + "], movedY = ["
                            + movedY + "]");
                }

                @Override
                public void onActionUp(MotionEvent event) {
                    Log.d(TAG, "onActionUp() called with: event = [" + event + "]");
                }

                @Override
                public void onActionOutSide(MotionEvent event) {
                    Log.d(TAG, "onActionOutSide() called with: event = [" + event + "]");
                }
            })
            .setPermissionListener(new FloatPermissionListener() {
                @Override
                public void onAcquired() {
                    Log.d(TAG, "onAcquired() called");
                }

                @Override
                public void onSuccess() {
                    Log.d(TAG, "onSuccess() called");
                }

                @Override
                public void onFailed() {
                    Log.d(TAG, "onFailed() called");
                }
            })
            .build();
        floatWindow.show();
    }

}