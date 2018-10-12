package com.zhaojy.planttudian.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * @author: zhaojy
 * @data:On 2018/5/17.
 */

public class PermissionUtils {

    /**
     * 检查所申请的权限是否已经获得
     *
     * @param permissions 申请的权限集
     * @param context
     * @return
     */
    public static boolean checkPermissionAllGranted(String[] permissions, Context context) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}
