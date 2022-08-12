package com.youer.androidui;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;

/**
 * Created by luxiao on 17/1/5.
 */

public class ScreenTools {

    /**
     * 适配单位，1ap =（屏幕宽度 / 375）
     **/
    private static final String SUFFIX_AP = "ap";

    /**
     * 同dp
     **/
    private static final String SUFFIX_NP = "np";

    private static final int WIDTH_REFER = 375;

    private static int WIDTH_SCREEN = -1;

    private static int HEIGHT_SCREEN = -1;

    private static float DENSITY = -1;

    public static int getPx(Context context, Object attrValue, int defaultValue) {
        if (attrValue == null) {
            return defaultValue;
        }

        String attrValueStr = String.valueOf(attrValue).toLowerCase();
        if (TextUtils.isEmpty(attrValueStr)) {

            return defaultValue;
        }

        try {
            int width = getScreenWidth(context);
            float density = getDensity(context);

            if (attrValueStr.contains(SUFFIX_NP)) {
                Float dp = Float.parseFloat(attrValueStr.replace(SUFFIX_NP, ""));
                return (int)(dp * density);
            } else if (attrValueStr.contains(SUFFIX_AP)) {
                Float pt = Float.parseFloat(attrValueStr.replace(SUFFIX_AP, ""));
                float scale = pt / WIDTH_REFER;
                return Math.round(width * scale);
            }

            float pt = Float.parseFloat(attrValueStr);
            float scale = pt / WIDTH_REFER;
            return Math.round(width * scale);
        } catch (NumberFormatException e) {

            return defaultValue;
        }

    }

    private static float getDensity(Context context) {
        if (DENSITY < 0) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            DENSITY = metrics.density;
        }

        return DENSITY;
    }

    /**
     * 有些情况获取宽和高是反的
     */
    public static int getScreenWidth(Context context) {
        if (WIDTH_SCREEN < 0) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            WIDTH_SCREEN = Math.min(metrics.widthPixels, metrics.heightPixels);
        }

        return WIDTH_SCREEN;
    }

    /**
     * 有些情况获取宽和高是反的
     */
    public static int getScreenHeight(Context context) {
        if (HEIGHT_SCREEN < 0) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            HEIGHT_SCREEN = Math.max(metrics.widthPixels, metrics.heightPixels);
        }

        return HEIGHT_SCREEN;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param context
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(pxValue / fontScale);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param context
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(spValue * fontScale);
    }

    /**
     * 在屏幕宽高发生变化时，重置所有跟屏幕相关的逻辑
     */
    public static void forceResetScreenSize() {
        WIDTH_SCREEN = -1;
        DENSITY = -1;
    }

}
