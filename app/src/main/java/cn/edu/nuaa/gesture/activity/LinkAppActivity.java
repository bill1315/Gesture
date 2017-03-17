package cn.edu.nuaa.gesture.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.edu.nuaa.gesture.adapter.ShowAppListAdapter;
import cn.edu.nuaa.gesture.model.AppInfo;

import cn.edu.nuaa.gesture.R;
import cn.edu.nuaa.gesture.model.NamedGesture;

public class LinkAppActivity extends AppCompatActivity {

    /*
    * 应用程序集合
    */
    private ArrayList<AppInfo> appInfos;
    private ListView lv_app;
    /*
     * 管理应用程序包，并通过它获取程序信息
     */
    private PackageManager pm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_app);
        //Getting the Bundle object that pass from another activity
        Bundle bundle = getIntent().getExtras();
        NamedGesture namedGesture = (NamedGesture)bundle.getParcelable("selectedObject");
        Boolean mIsLink=bundle.getBoolean("isLink");
        //Toast.makeText(this,namedGesture.getName(),Toast.LENGTH_LONG).show();
        TextView mLinkShow= (TextView) findViewById(R.id.app_gesture_name);
        mLinkShow.setText(namedGesture.getName());
        Switch mLinkSwitch= (Switch) findViewById(R.id.link_switch);

        pm = getPackageManager();
        lv_app = (ListView) findViewById(R.id.app_list_view);
        if(mIsLink){
            mLinkSwitch.setChecked(true);
            lv_app.setEnabled(true);
            lv_app.getBackground().setAlpha(255);
        }else{
            mLinkSwitch.setChecked(false);
            lv_app.setEnabled(false);
            lv_app.getBackground().setAlpha(100);
        }
        mLinkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    lv_app.setEnabled(true);
                    lv_app.getBackground().setAlpha(255);
                } else {
                    lv_app.setEnabled(false);
                    lv_app.getBackground().setAlpha(100);
                }
            }
        });


        new Thread(runable).start();
    }



    private final Runnable runable = new Runnable() {

        public void run() {
            loadApplications();
            myHandler.obtainMessage().sendToTarget();
        }

    };

    private Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            lv_app.setAdapter(new ShowAppListAdapter(LinkAppActivity.this, appInfos, pm));

        }

    };

    /**
     * 加载应用列表
     */
    private void loadApplications() {
        PackageManager manager = this.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> apps = manager.queryIntentActivities(
                mainIntent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));
        if (apps != null) {
            final int count = apps.size();
            if (appInfos == null) {
                appInfos = new ArrayList<AppInfo>(count);
            }
            appInfos.clear();
            for (int i = 0; i < count; i++) {
                AppInfo application = new AppInfo();
                ResolveInfo info = apps.get(i);
                application.title = info.loadLabel(manager);
                application.setActivity(new ComponentName(
                        info.activityInfo.applicationInfo.packageName,
                        info.activityInfo.name), Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                application.icon = info.activityInfo.loadIcon(manager);
                application.packageName = info.activityInfo.applicationInfo.packageName;
                appInfos.add(application);
            }
        }
    }

}
