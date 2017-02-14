package cn.edu.nuaa.gesture.activity;

import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import cn.edu.nuaa.gesture.R;
import cn.edu.nuaa.gesture.utils.Global;
/**
 * Created by terry on 2017/1/23.
 */
public class GestureAddActivity extends Activity {
    private static final float LENGTH_THRESHOLD = 120.0f;
    private static final String GESTURE = "gesture";

    private EditText mEditName;
    private Button mAddButton;
    private Button mCanelButton;
    private Gesture mGesture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesture_add);
        GestureOverlayView mOverlay=(GestureOverlayView)findViewById(R.id.gesture_overlay_set);
        mOverlay.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE); //设置手势可多笔画绘制，默认情况为单笔画绘制
        mOverlay.setFadeOffset(2000);//多笔画两次的间隔时间,默认值为420毫秒
        mOverlay.setGestureColor(Color.BLACK);// 设置手势绘制颜色
        mOverlay.setUncertainGestureColor(Color.GREEN);//设置还未形成的手势颜色
        mOverlay.setGestureStrokeWidth(8);//画笔粗细值
        mOverlay.addOnGestureListener(new GestureProcessor());

        mAddButton = (Button)findViewById(R.id.gesture_button_add);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mGesture){
                    mEditName=(EditText)findViewById(R.id.gesture_name);
                    CharSequence editText=mEditName.getText();
                    if(editText.length()==0){
                        mEditName.setError(getString(R.string.error_missing_name));
                        return;
                    }
                    /**
                     * 获取手势库
                     *   private final File mStoreFile = new File(Environment.getExternalStorageDirectory(), "gestures");
                     *   GestureLibrary sStore = GestureLibraries.fromFile(mStoreFile);
                     */
                    final GestureLibrary store = GestureListActivity.getStore();
                    store.addGesture(editText.toString(), mGesture);
                    store.save();

                    setResult(RESULT_OK);
                    Toast.makeText(GestureAddActivity.this,getString(R.string.save_success,Global.mStoreFile.getAbsolutePath()),Toast.LENGTH_SHORT).show();
                }else{
                    setResult(RESULT_CANCELED);
                }
                finish();
            }
        });
        mCanelButton=(Button)findViewById(R.id.gesture_button_canel);
        mCanelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(null!=mGesture){
            outState.putParcelable(GESTURE,mGesture);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mGesture=savedInstanceState.getParcelable(GESTURE);
        if(null!=mGesture){
            final GestureOverlayView mGestureOverlayView=(GestureOverlayView)findViewById(R.id.gesture_overlay_set);
            mGestureOverlayView.post(new Runnable() {
                @Override
                public void run() {
                    mGestureOverlayView.setGesture(mGesture);
                }
            });
            mAddButton.setEnabled(true);
        }
    }

    private class GestureProcessor implements GestureOverlayView.OnGestureListener{

        public void onGestureStarted(GestureOverlayView overlay,MotionEvent event){
            mAddButton.setEnabled(false);
            mGesture=null;
        }

        public void onGesture(GestureOverlayView overlay, MotionEvent event) {
        }

        public void onGestureEnded(GestureOverlayView overlay,MotionEvent event){
            mGesture=overlay.getGesture();
            if(mGesture.getLength()<LENGTH_THRESHOLD){
                overlay.clear(false);
            }
            mAddButton.setEnabled(true);
        }

        public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
        }
    }
}
