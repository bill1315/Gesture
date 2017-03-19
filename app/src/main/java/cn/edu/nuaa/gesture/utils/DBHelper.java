package cn.edu.nuaa.gesture.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by terry on 2017/3/19.
 */

public class DBHelper  extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Gesture";
    private static final String TABLE_NAME="GestureLink";
    private static final int VERSION=1;
    private static final String KEY_ID="id";
    private static final String KEY_APPNAME="appName";
    private static final String KEY_PACKAGENAME="packageName";
    private static final String KEY_GESTURENAME="gestureName";
    private static final String KEY_TYPE="type";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        //CursorFactory设置为null,使用默认值
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //数据库第一次被创建时onCreate会被调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " +TABLE_NAME+"(" +KEY_ID+
                " integer primary key autoincrement, "+KEY_APPNAME+ " VARCHAR not null,"+
                KEY_PACKAGENAME+ " VARCHAR not null,"+ KEY_GESTURENAME+ " VARCHAR not null,"+KEY_TYPE +" integer not null)");
    }

    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE "+TABLE_NAME+" ADD COLUMN other STRING");
    }
}
