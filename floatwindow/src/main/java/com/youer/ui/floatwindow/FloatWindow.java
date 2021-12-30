package com.youer.ui.floatwindow;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.youer.ui.floatwindow.permission.FloatPermissionActivity;
import com.youer.ui.floatwindow.permission.FloatPermissionListener;

/**
 * 悬浮窗
 *
 * @author youer
 * @date 2021/11/30
 */
public class FloatWindow {

    /**
     * 手指移动位置
     */
    private static final String TAG = "FloatWindow";
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private Context context;
    /**
     * 内容View
     */
    private View contentView;
    /**
     * View的宽
     */
    private int width = FrameLayout.LayoutParams.WRAP_CONTENT;
    /**
     * View的高
     */
    private int height = FrameLayout.LayoutParams.WRAP_CONTENT;
    /**
     * 悬浮窗起始位置
     */
    private int startX, startY;

    /**
     * 触摸点相对于view左上角的坐标
     */
    private float downX, downY, upX, upY;
    private int mx;
    private int my;
    private boolean showing;
    private FloatPermissionListener permissionListener;
    private ViewStateListener viewStateListener;
    // 是否可编辑
    private boolean editable;

    public FloatWindow(Builder builder) {
        build(builder);
        initWindowManager();
        initLayoutParams();
        initFloatView();
        initFloatView();
    }

    private void build(Builder builder) {
        this.context = builder.context;
        this.contentView = builder.view;
        this.width = builder.width;
        this.height = builder.height;
        this.startX = builder.startX;
        this.startY = builder.startY;
        this.permissionListener = builder.permissionListener;
        this.viewStateListener = builder.viewStateListener;
        this.editable = builder.editable;
    }

    private void initWindowManager() {
        windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
    }

    private void initLayoutParams() {
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        // 设置之后window永远不会获取焦点,所以用户不能给此window发送点击事件,焦点会传递给在其下面的可获取焦点的window
        // windowManger.LayoutParams flag含义 https://www.jianshu.com/p/b2580adcfcd2
        if (editable) {
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        } else {
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        //悬浮窗起始位置
        layoutParams.x = startX;
        layoutParams.y = startY;
    }

    private void initFloatView() {
        contentView.setOnTouchListener(new ItemViewTouchListener());
    }

    /**
     * 设置悬浮窗可以获取焦点，用于弹出输入框
     *
     * @param editable
     */
    public void setEditable(boolean editable) {
        if (editable) {
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        } else {
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }
        windowManager.updateViewLayout(contentView, layoutParams);
    }

    public void show() {
        FloatPermissionActivity.requestPermission(context, new FloatPermissionListener() {
            @Override
            public void onAcquired() {
                show();
                if (permissionListener != null) {
                    permissionListener.onAcquired();
                }
            }

            @Override
            public void onSuccess() {
                show();
                if (permissionListener != null) {
                    permissionListener.onSuccess();
                }
            }

            @Override
            public void onFailed() {
                if (permissionListener != null) {
                    permissionListener.onFailed();
                }
            }

            private void show() {
                if (isShowing()) {
                    return;
                }
                windowManager.addView(contentView, layoutParams);
                showing = true;
                if (viewStateListener != null) {
                    viewStateListener.onShow();
                }
            }
        });

    }

    public boolean isShowing() {
        return showing;
    }

    public void dismiss() {
        if (!showing) {
            return;
        }
        windowManager.removeView(contentView);
        showing = false;
        if (viewStateListener != null) {
            viewStateListener.onDismiss();
        }
    }

    /**
     * 更新位置
     */
    public void updateLocation(int x, int y) {
        layoutParams.x = mx = x;
        layoutParams.y = my = y;

        windowManager.updateViewLayout(contentView, layoutParams);
    }

    class ItemViewTouchListener implements OnTouchListener {
        float lastX, lastY, changeX, changeY;
        boolean click = false;

        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getRawX();
                    downY = event.getRawY();
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    if (viewStateListener != null) {
                        viewStateListener.onActionDown(event);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    //拖动事件下一直计算坐标 然后更新悬浮窗位置
                    changeX = event.getRawX() - lastX;
                    changeY = event.getRawY() - lastY;
                    int movedX = (int)(mx + changeX);
                    int movedY = (int)(my + changeY);
                    //拖动事件下一直计算坐标 然后更新悬浮窗位置
                    updateLocation(movedX, movedY);
                    if (viewStateListener != null) {
                        viewStateListener.onActionMove(event, movedX, movedY);
                    }
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    upX = event.getRawX();
                    upY = event.getRawY();
                    //click = (Math.abs(upX - downX) > 5) || (Math.abs(upY - downY) > 5);
                    if (viewStateListener != null) {
                        viewStateListener.onActionUp(event);
                    }
                    break;
                case MotionEvent.ACTION_OUTSIDE:
                    if (viewStateListener != null) {
                        viewStateListener.onActionOutSide(event);
                    }
                    break;
                default:
                    break;
            }
            return click;
        }
    }

    public static class Builder {
        private Context context;
        private View view;
        private int width = FrameLayout.LayoutParams.WRAP_CONTENT;
        private int height = FrameLayout.LayoutParams.WRAP_CONTENT;
        private int startX;
        private int startY;
        private FloatPermissionListener permissionListener;
        private ViewStateListener viewStateListener;
        private boolean editable;

        public Builder(Context context, View view) {
            this.context = context;
            this.view = view;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setWidth(@Screen.ScreenType int screen, float radio) {
            this.width = ScreenTool.getSize(context, screen, radio);
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setHeight(@Screen.ScreenType int screen, float radio) {
            this.height = ScreenTool.getSize(context, screen, radio);
            return this;
        }

        public Builder setStartX(int startX) {
            this.startX = startX;
            return this;
        }

        public Builder setStartX(@Screen.ScreenType int screen, float radio) {
            this.startX = ScreenTool.getSize(context, screen, radio);
            return this;
        }

        public Builder setStartY(int startY) {
            this.startY = startY;
            return this;
        }

        public Builder setStartY(@Screen.ScreenType int screen, float radio) {
            this.startY = ScreenTool.getSize(context, screen, radio);
            return this;
        }

        public Builder setPermissionListener(FloatPermissionListener permissionListener) {
            this.permissionListener = permissionListener;
            return this;
        }

        public Builder setViewStateListener(ViewStateListener viewStateListener) {
            this.viewStateListener = viewStateListener;
            return this;
        }

        public Builder setEditable(boolean editable) {
            this.editable = editable;
            return this;
        }

        public FloatWindow build() {
            return new FloatWindow(this);
        }
    }
} 