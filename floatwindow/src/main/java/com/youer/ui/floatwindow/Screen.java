package com.youer.ui.floatwindow;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/**
 * @author youer
 * @date 2021/12/29
 */
public class Screen {

    public static final int WIDTH = 0;//宽
    public static final int HEIGHT = 1;//高

    @IntDef({WIDTH, HEIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScreenType {

    }

} 