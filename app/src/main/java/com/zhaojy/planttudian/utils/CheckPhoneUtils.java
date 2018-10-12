package com.zhaojy.planttudian.utils;

import android.content.Context;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 检查电话号码是否合法
 *
 * @author: zhaojy
 * @data:On 2018/9/28.
 */

public class CheckPhoneUtils {
    private final static String TIP_ONE = "手机号应为11位数";
    private final static String TIP_TWO = "请填入正确的手机号";

    public static boolean isPhone(Context context, String phone) {
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        if (phone.length() != 11) {
            Toast.makeText(context, TIP_ONE, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(phone);
            boolean isMatch = m.matches();

            if (!isMatch) {
                Toast.makeText(context, TIP_TWO, Toast.LENGTH_SHORT).show();
            }
            return isMatch;
        }
    }

}
