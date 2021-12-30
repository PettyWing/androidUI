package com.youer.ui.floatwindow.permission;

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
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * @author youer
 * @date 2021/11/11
 */
public class FloatPermissionUtil {

    private static final String TAG = "FloatPermissionManager";
    public static final int REQ_FLOAT_PERMISSION = 1;

    /**
     * 是否有悬浮窗权限
     */
    public static boolean checkPermission(Context context) {
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

    public static void jumpToSetting(AppCompatActivity activity) {
        try {
            if (VERSION.SDK_INT >= 23) {
                toSettingFloatPermission(activity);
            } else if (TextUtils.equals("Meizu", Build.MANUFACTURER)) {
                Log.e(TAG, "jumpToSetting: " + "魅族手机");
                toMeiZuSetting(activity);
            } else if ("Oppo".equalsIgnoreCase(Build.MANUFACTURER)) {
                Log.e(TAG, "jumpToSetting: " + "Oppo手机");
                toOppoSetting(activity);
            } else if ("Xiaomi".equals(Build.MANUFACTURER)) {
                Log.e(TAG, "jumpToSetting: " + "小米手机");
                toXiaoMiSetting(activity);
            }
        } catch (Exception unused) {
            Log.d(TAG, "jumpToSetting: " + unused.getMessage());
            Toast.makeText(activity, "开启悬浮播放功能失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 跳转到系统设置界面
     *
     * @param activity
     */
    @TargetApi(23)
    private static void toSettingFloatPermission(AppCompatActivity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        String sb = "package:" + activity.getPackageName();
        intent.setData(Uri.parse(sb));
        activity.startActivityForResult(intent, REQ_FLOAT_PERMISSION);
    }

    /**
     * 魅族设置界面
     */
    private static void toMeiZuSetting(AppCompatActivity activity) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.setClassName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity");
        intent.putExtra("packageName", activity.getPackageName());
        activity.startActivityForResult(intent, REQ_FLOAT_PERMISSION);
    }

    private static void toXiaoMiSetting(AppCompatActivity activity) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        if ("V5".equals(getXiaoMiVersion())) {
            PackageInfo packageInfo = null;
            try {
                packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
                intent.setClassName("com.miui.securitycenter",
                    "com.miui.securitycenter.permission.AppPermissionsEditor");
                intent.putExtra("extra_package_uid", packageInfo.applicationInfo.uid);
                activity.startActivityForResult(intent, REQ_FLOAT_PERMISSION);
            } catch (NameNotFoundException unused) {
                unused.printStackTrace();
            }
        } else if ("V6".equals(getXiaoMiVersion())) {
            intent.setClassName("com.miui.securitycenter",
                "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            intent.putExtra("extra_pkgname", activity.getPackageName());
            activity.startActivityForResult(intent, REQ_FLOAT_PERMISSION);
        } else {
            Intent intent2 = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent2.setPackage("com.miui.securitycenter");
            intent2.putExtra("extra_pkgname", activity.getPackageName());
            activity.startActivity(intent2);
        }
    }

    /**
     * 跳转到oppo手机设置界面
     */
    private static void toOppoSetting(AppCompatActivity activity) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setComponent(new ComponentName("com.coloros.safecenter",
            "com.coloros.safecenter.permission.floatwindow.FloatWindowListActivity"));
        activity.startActivityForResult(intent, REQ_FLOAT_PERMISSION);
    }

    /**
     * 跳转到iqoo
     */
    private static void toIqoo(AppCompatActivity activity) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setComponent(
            new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.FloatWindowManager"));
        activity.startActivityForResult(intent, REQ_FLOAT_PERMISSION);
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