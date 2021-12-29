package com.youer.ui.floatwindow.permission;

/**
 * @author youer
 * @date 2021/12/29
 */
public interface FloatPermissionListener {
    // 已获取
    void onAcquired();

    // 获取成功
    void onSuccess();

    // 获取失败
    void onFailed();
} 