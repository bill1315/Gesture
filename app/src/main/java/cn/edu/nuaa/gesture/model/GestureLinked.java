package cn.edu.nuaa.gesture.model;

/**
 * Created by terry on 2017/3/19.
 */

public class GestureLinked {
    private int id;
    private String appName;// 程序名
    private String packageName; // 程序包名
    private String gestureName; // 手势名称
    private int type;//类型 1：安全  2应用程序

    public GestureLinked(){

    }

    public GestureLinked(int id, String appName, String packageName,String gestureName,int type ) {
        this.id = id;
        this.appName = appName;
        this.packageName = packageName;
        this.gestureName = gestureName;
        this.type = type;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getGestureName() {
        return gestureName;
    }

    public void setGestureName(String gestureName) {
        this.gestureName = gestureName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
