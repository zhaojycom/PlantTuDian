package com.zhaojy.planttudian.utils;

import android.os.Build;

import java.lang.reflect.Method;

/**
 * 监测手机系统是否为魅族系统
 *
 * @author: zhaojy
 * @data:On 2018/9/16.
 */

public final class FlymeUtils {

    public static boolean isFlyme() {
        try {
            // Invoke Build.hasSmartBar()
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (final Exception e) {
            return false;
        }
    }

}
