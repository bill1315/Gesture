package cn.edu.nuaa.gesture.model;

import android.gesture.Gesture;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by terry on 2017/1/24.
 */

public  class NamedGesture implements Parcelable {

    private String mName;
    private Gesture mGesture;

    public NamedGesture(){

    }

    protected NamedGesture(Parcel in) {
        mName = in.readString();
        mGesture = in.readParcelable(Gesture.class.getClassLoader());
    }

    public static final Creator<NamedGesture> CREATOR = new Creator<NamedGesture>() {
        @Override
        public NamedGesture createFromParcel(Parcel in) {
            return new NamedGesture(in);
        }

        @Override
        public NamedGesture[] newArray(int size) {
            return new NamedGesture[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeParcelable(mGesture, flags);
    }
}
