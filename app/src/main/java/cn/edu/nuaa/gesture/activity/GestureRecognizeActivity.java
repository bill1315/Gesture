package cn.edu.nuaa.gesture.activity;

import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

import cn.edu.nuaa.gesture.R;
import cn.edu.nuaa.gesture.utils.Global;

public class GestureRecognizeActivity extends Activity {

    GestureLibrary mGestureLibrary;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesture_recognize);

        GestureOverlayView gestureOverlayView=(GestureOverlayView)findViewById(R.id.gestures_overlay);
        gestureOverlayView.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);
        gestureOverlayView.setFadeOffset(2000);
        gestureOverlayView.setGestureColor(Color.BLACK);
        gestureOverlayView.setGestureStrokeWidth(8);
        gestureOverlayView.addOnGesturePerformedListener(new GestureOverlayView.OnGesturePerformedListener() {
            @Override
            public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
                //判断手势库是否加载
                /**if (mGestureLibrary.load()) {
                    Toast.makeText(GestureRecognizeActivity.this, R.string.load_gesture_success, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(GestureRecognizeActivity.this, R.string.load_gesture_failed, Toast.LENGTH_SHORT).show();
                }**/
                // 从手势库中查询匹配的内容，匹配的结果可能包括多个相似的结果，匹配度高的结果放在最前面
                ArrayList<Prediction> predictions=mGestureLibrary.recognize(gesture);
                if(null!=predictions&&predictions.size()>0){
                    Prediction prediction=predictions.get(0);
                    // 匹配的手势
                    if(prediction.score>5.0){// 越匹配score的值越大，最大为10
                        Toast.makeText(GestureRecognizeActivity.this,"手势名称："+prediction.name +" 相似度：" + prediction.score,Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(GestureRecognizeActivity.this,R.string.no_match_gesture,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        if(null==mGestureLibrary){
            mGestureLibrary= GestureLibraries.fromFile(Global.mStoreFile);
            mGestureLibrary.load();
        }
    }
}
