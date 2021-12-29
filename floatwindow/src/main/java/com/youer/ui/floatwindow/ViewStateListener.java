package com.youer.ui.floatwindow;

import android.view.MotionEvent;

/**
 * @author youer
 * @date 2021/12/29
 */
public interface ViewStateListener {
    void onShow();

    void onDismiss();

    void onActionDown(MotionEvent event);

    void onActionMove(MotionEvent event, float movedX, float movedY);

    void onActionUp(MotionEvent event);

    void onActionOutSide(MotionEvent event);
}
