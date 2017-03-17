package cn.edu.nuaa.gesture.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cn.edu.nuaa.gesture.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button mAppSet=(Button)findViewById(R.id.appSet);
        Button mSafeSet=(Button)findViewById(R.id.safeSet);
        mAppSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,GestureListActivity.class);
                startActivity(intent);
            }
        });
    }
}
