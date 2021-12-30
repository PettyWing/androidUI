package com.youer.ui.floatwindow.permission;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * 用于权限验证的activity
 *
 * @author youer
 * @date 2021/12/29
 */
public class FloatPermissionActivity extends AppCompatActivity {

    private static FloatPermissionListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FloatPermissionUtil.jumpToSetting(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FloatPermissionUtil.REQ_FLOAT_PERMISSION) {
            if (FloatPermissionUtil.checkPermission(this)) {
                mListener.onSuccess();
            } else {
                mListener.onFailed();
            }
        }
        finish();
    }

    public static void requestPermission(Context context, FloatPermissionListener listener) {
        if (FloatPermissionUtil.checkPermission(context)) {
            listener.onAcquired();
            return;
        }
        mListener = listener;
        Intent intent = new Intent(context, FloatPermissionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}