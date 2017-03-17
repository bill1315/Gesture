package cn.edu.nuaa.gesture.model;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * Created by terry on 2017/3/14.
 */

public class AppInfo {
    public CharSequence title;// 程序名
    public CharSequence packageName; // 程序包名
    Intent intent;// 启动Intent
    public Drawable icon;// 程序图标

    /*
     * 设置启动该程序的Intent
     */
    public final void setActivity(ComponentName className, int launchFlags) {
        intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(className);
        intent.setFlags(launchFlags);
    }
}
