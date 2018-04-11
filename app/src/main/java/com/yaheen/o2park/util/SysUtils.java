package com.yaheen.o2park.util;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;

/**
 * Created by linjingsheng on 17/5/4.
 */

public class SysUtils {

    private static DisplayMetrics dm;

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getsWindowWidth(Context context) {
        if (dm == null) {
            dm = context.getResources().getDisplayMetrics();
        }
        return dm.widthPixels;
    }

    /***
     * 获取屏幕高度
     * @param context
     * @return
     */
    public static int getsWindowHeight(Context context) {
        if (dm == null) {
            dm = context.getResources().getDisplayMetrics();
        }
        return dm.heightPixels;
    }

    /***
     * 获取机器码
     * @return
     */
    public static String android_id(Context context) {
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (TextUtils.isEmpty(android_id)) { //若获取不到则生成一个机器码
            android_id = UUIDUtils.getUuid();
        }
        return android_id;
    }
}
