package cn.edu.nuaa.gesture.model;

import android.gesture.Gesture;

/**
 * Created by terry on 2017/1/24.
 */

public class NamedGesture {

    private String mName;
    private Gesture mGesture;

    public NamedGesture(){

    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Gesture getGesture() {
        return mGesture;
    }

    public void setGesture(Gesture gesture) {
        mGesture = gesture;
    }

}
