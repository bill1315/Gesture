package cn.edu.nuaa.gesture.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by terry on 2017/1/23.
 */

public class Global {
    public final static String GESTURES = "gestureLib";
    public final static  File mStoreFile = new File(Environment.getExternalStorageDirectory(), GESTURES);
}
