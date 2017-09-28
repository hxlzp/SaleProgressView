package com.example.administrator.mysaleprogressview.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by hxl on 2017/9/28  at haichou.
 */

public class ScreenUtil {
    public static float dp2px(Context context ,int value){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        return value*density + 0.5f;
    }
    public static float px2dp(Context context ,int value){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        return value/density + 0.5f;
    }
}
