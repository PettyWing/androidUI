package com.youer.ui.floatwindow;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
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
     * 悬浮View
     */
    private View floatView;
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
    private float downX;
    private float downY;
    private boolean showing;
    /**
     * 触摸点相对于屏幕左上角的坐标
     */
    private float rowX;
    private float rowY;
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
        floatView = new FloatView(context);
        floatView.setOnTouchListener(new ItemViewTouchListener());
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
        windowManager.updateViewLayout(floatView, layoutParams);
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
                windowManager.addView(floatView, layoutParams);
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
        windowManager.removeView(floatView);
        showing = false;
        if (viewStateListener != null) {
            viewStateListener.onDismiss();
        }
    }

    /**
     * 更新位置
     */
    public void updateLocation(float x, float y) {
        layoutParams.x = (int)x;
        layoutParams.y = (int)y;

        windowManager.updateViewLayout(floatView, layoutParams);
    }

    class FloatView extends FrameLayout {
        /**
         * 拖动最小偏移量
         */
        private static final int MINIMUM_OFFSET = 5;
        /**
         * 记录按下位置
         */
        int interceptX = 0;
        int interceptY = 0;

        public FloatView(Context context) {
            super(context);
            //这里由于一个ViewGroup不能add一个已经有Parent的contentView,所以需要先判断contentView是否有Parent
            //如果有则需要将contentView先移除
            if (contentView.getParent() != null && contentView.getParent() instanceof ViewGroup) {
                ((ViewGroup)contentView.getParent()).removeView(contentView);
            }

            addView(contentView);
        }

        /**
         * 解决点击与拖动冲突的关键代码
         *
         * @param ev
         * @return
         */
        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            //此回调如果返回true则表示拦截TouchEvent由自己处理，false表示不拦截TouchEvent分发出去由子view处理
            //解决方案：如果是拖动父View则返回true调用自己的onTouch改变位置，是点击则返回false去响应子view的点击事件
            boolean isIntercept = false;
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    interceptX = (int)ev.getX();
                    interceptY = (int)ev.getY();
                    downX = ev.getX();
                    downY = ev.getY();
                    isIntercept = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    //在一些dpi较高的设备上点击view很容易触发 ACTION_MOVE，所以此处做一个过滤
                    isIntercept = Math.abs(ev.getX() - interceptX) > MINIMUM_OFFSET && Math.abs(ev.getY() - interceptY)
                        > MINIMUM_OFFSET;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                default:
                    break;
            }
            return isIntercept;
        }
    }

    class ItemViewTouchListener implements OnTouchListener {

        public boolean onTouch(View v, MotionEvent event) {

            //获取触摸点相对于屏幕左上角的坐标
            rowX = event.getRawX();
            rowY = event.getRawY() - ScreenTool.getStatusBarHeight(context);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    actionDown(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    //拖动事件下一直计算坐标 然后更新悬浮窗位置
                    actionMove(event);
                    break;
                case MotionEvent.ACTION_UP:
                    actionUp(event);
                    break;
                case MotionEvent.ACTION_OUTSIDE:
                    actionOutSide(event);
                    break;
                default:
                    break;
            }
            return false;
        }

        /**
         * 手指按下事件
         *
         * @param event
         */
        private void actionDown(MotionEvent event) {
            if (viewStateListener != null) {
                viewStateListener.onActionDown(event);
            }
        }

        /**
         * 拖动事件
         *
         * @param event
         */
        private void actionMove(MotionEvent event) {
            float movedX = rowX - downX;
            float movedY = rowY - downY;
            //拖动事件下一直计算坐标 然后更新悬浮窗位置
            updateLocation((rowX - downX), (rowY - downY));
            if (viewStateListener != null) {
                viewStateListener.onActionMove(event, movedX, movedY);
            }
        }

        /**
         * 手指抬起事件
         *
         * @param event
         */
        private void actionUp(MotionEvent event) {
            if (viewStateListener != null) {
                viewStateListener.onActionUp(event);
            }
        }

        /**
         * 手指点击窗口外的事件
         *
         * @param event
         */
        private void actionOutSide(MotionEvent event) {
            if (viewStateListener != null) {
                viewStateListener.onActionOutSide(event);
            }
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