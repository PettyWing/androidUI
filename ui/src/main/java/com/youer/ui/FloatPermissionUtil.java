package com.youer.ui;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * @author youer
 * @date 2021/11/11
 */
public class FloatPermissionUtil {

    private static final String TAG = "FloatPermissionManager";

    /**
     * 申请悬浮窗权限
     *
     * @param Context context
     */
    public static boolean requestFloatPermission(Context context) {
        if (!isRequestFloatPermission(context)) {
            jumpToSetting(context);
            return false;
        }
        return true;
    }

    /**
     * 是否有悬浮窗权限
     */
    public static boolean isRequestFloatPermission(Context context) {
        // 如果大于23则直接通过系统判断
        if (VERSION.SDK_INT >= 23) {
            return Settings.canDrawOverlays(context);
        }
        if (VERSION.SDK_INT >= 19) {
            return checkLowVersion(context, 24);
        }
        return true;
    }

    @TargetApi(19)
    private static boolean checkLowVersion(Context context, int i) {
        boolean z = false;
        if (VERSION.SDK_INT >= 19) {
            AppOpsManager appOpsManager = (AppOpsManager)context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class.forName(appOpsManager.getClass().getName());
                if ((Integer)appOpsManager.getClass()
                    .getDeclaredMethod("checkOp", new Class[] {Integer.TYPE, Integer.TYPE, String.class})
                    .invoke(appOpsManager, new Object[] {Integer.valueOf(i), Integer.valueOf(Binder.getCallingUid()),
                        context.getPackageName()})
                    == 0) {
                    z = true;
                }
                return z;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return false;
        }

        return false;
    }

    public static void jumpToSetting(Context context) {
        try {
            if (VERSION.SDK_INT >= 23) {
                toSettingFloatPermission(context);
            } else if (TextUtils.equals("Meizu", Build.MANUFACTURER)) {
                Log.e(TAG, "jumpToSetting: " + "魅族手机");
                toMeiZuSetting(context);
            } else if ("Oppo".equalsIgnoreCase(Build.MANUFACTURER)) {
                Log.e(TAG, "jumpToSetting: " + "Oppo手机");
                toOppoSetting(context);
            } else if ("Xiaomi".equals(Build.MANUFACTURER)) {
                Log.e(TAG, "jumpToSetting: " + "小米手机");
                toXiaoMiSetting(context);
            }
        } catch (Exception unused) {
            Log.d(TAG, "jumpToSetting: " + unused.getMessage());
            Toast.makeText(context, "开启悬浮播放功能失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 跳转到系统设置界面
     *
     * @param Context context
     */
    @TargetApi(23)
    private static void toSettingFloatPermission(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.settings.action.MANAGE_OVERLAY_PERMISSION");
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        String sb = "package:" + context.getPackageName();
        intent.setData(Uri.parse(sb));
        context.startActivity(intent);
    }

    /**
     * 魅族设置界面
     */
    private static void toMeiZuSetting(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.setClassName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity");
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", context.getPackageName());
        context.startActivity(intent);
    }

    private static void toXiaoMiSetting(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        if ("V5".equals(getXiaoMiVersion())) {
            PackageInfo packageInfo = null;
            try {
                packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                intent.setClassName("com.miui.securitycenter",
                    "com.miui.securitycenter.permission.AppPermissionsEditor");
                intent.putExtra("extra_package_uid", packageInfo.applicationInfo.uid);
                context.startActivity(intent);
            } catch (NameNotFoundException unused) {
                unused.printStackTrace();
            }
        } else if ("V6".equals(getXiaoMiVersion())) {
            intent.setClassName("com.miui.securitycenter",
                "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            intent.putExtra("extra_pkgname", context.getPackageName());
            context.startActivity(intent);
        } else {
            Intent intent2 = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent2.setPackage("com.miui.securitycenter");
            intent2.putExtra("extra_pkgname", context.getPackageName());
            context.startActivity(intent2);
        }
    }

    /**
     * 跳转到oppo手机设置界面
     */
    private static void toOppoSetting(Context context) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(new ComponentName("com.coloros.safecenter",
            "com.coloros.safecenter.permission.floatwindow.FloatWindowListActivity"));
        context.startActivity(intent);
    }

    /**
     * 跳转到iqoo
     */
    private static void toIqoo(Context context) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(
            new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.FloatWindowManager"));
        context.startActivity(intent);
    }

    public static String getXiaoMiVersion() {
        String str = "null";
        if (!"Xiaomi".equals(Build.MANUFACTURER)) {
            return str;
        }
        try {
            Class cls = Class.forName("android.os.SystemProperties");
            str = (String)cls.getDeclaredMethod("get", new Class[] {String.class, String.class}).invoke(cls,
                new Object[] {"ro.miui.ui.version.name", null});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
}