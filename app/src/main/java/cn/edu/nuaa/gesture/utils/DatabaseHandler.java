package cn.edu.nuaa.gesture.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nuaa.gesture.model.GestureLinked;

/**
 * Created by terry on 2017/3/19.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="Gesture";
    private static final String TABLE_NAME="GestureLink";
    private static final int VERSION=1;
    private static final String KEY_ID="id";
    private static final String KEY_APPNAME="appName";
    private static final String KEY_PACKAGENAME="packageName";
    private static final String KEY_GESTURENAME="gestureName";
    private static final String KEY_TYPE="type";

    //建表语句
    private static final String CREATE_TABLE=("create table " +TABLE_NAME+"(" +KEY_ID+
    " integer primary key autoincrement, "+KEY_APPNAME+ " VARCHAR not null,"+
    KEY_PACKAGENAME+ " VARCHAR not null,"+ KEY_GESTURENAME+ " VARCHAR not null,"+KEY_TYPE +" integer not null)");

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "  + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void addGestureLink(GestureLinked gestureLinked){
        SQLiteDatabase db=this.getWritableDatabase();

        //使用ContentValues添加数据
        ContentValues values=new ContentValues();
        values.put(KEY_APPNAME,  gestureLinked.getAppName());
        values.put(KEY_PACKAGENAME,gestureLinked.getPackageName());
        values.put(KEY_GESTURENAME,gestureLinked.getGestureName());
        values.put(KEY_TYPE,gestureLinked.getType());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }
    public GestureLinked getGestureLink(String name){
        SQLiteDatabase db=this.getWritableDatabase();

        //Cursor对象返回查询结果
        Cursor cursor=db.query(TABLE_NAME,new String[]{KEY_ID,KEY_APPNAME,KEY_PACKAGENAME,KEY_GESTURENAME,KEY_TYPE},
                KEY_GESTURENAME+"=?",new String[]{name},null,null,null,null);


        GestureLinked gestureLinked=null;
        //注意返回结果有可能为空
        if(cursor.moveToFirst()){
            gestureLinked=new GestureLinked(cursor.getInt(0),cursor.getString(1), cursor.getString(2),cursor.getString(3),cursor.getInt(4));
        }
        return gestureLinked;
    }
    public int getCounts(){
        String selectQuery="SELECT * FROM "+TABLE_NAME;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(selectQuery,null);
        cursor.close();
        return cursor.getCount();
    }

    //查找所有student
    public List<GestureLinked> getALL(){
        List<GestureLinked> gestureLinkedList=new ArrayList<GestureLinked>();

        String selectQuery="SELECT * FROM "+TABLE_NAME;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(selectQuery,null);
        if(cursor.moveToFirst()){
            do{
                GestureLinked gestureLinked=new GestureLinked();
                gestureLinked.setId(cursor.getInt(0));
                gestureLinked.setAppName(cursor.getString(1));
                gestureLinked.setPackageName(cursor.getString(2));
                gestureLinked.setGestureName(cursor.getString(3));
                gestureLinked.setType(cursor.getInt(4));
                gestureLinkedList.add(gestureLinked);
            }while(cursor.moveToNext());
        }
        return gestureLinkedList;
    }

    //更新student
    public int updateGestureLink(GestureLinked gestureLinked){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_APPNAME,gestureLinked.getAppName());
        values.put(KEY_PACKAGENAME,gestureLinked.getPackageName());
        values.put(KEY_GESTURENAME,gestureLinked.getGestureName());
        values.put(KEY_TYPE,gestureLinked.getType());

        return db.update(TABLE_NAME,values,KEY_ID+"=?",new String[]{String.valueOf(gestureLinked.getId())});
    }
    public void deleteGestureLink(GestureLinked gestureLinked){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_NAME,KEY_ID+"=?",new String[]{String.valueOf(gestureLinked.getId())});
        db.close();
    }

    public void closeDB() {
        SQLiteDatabase db=this.getWritableDatabase();
        db.close();
    }
}
