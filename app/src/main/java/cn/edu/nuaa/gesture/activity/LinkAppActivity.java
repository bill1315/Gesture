package cn.edu.nuaa.gesture.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.nuaa.gesture.model.AppInfo;

import cn.edu.nuaa.gesture.R;
import cn.edu.nuaa.gesture.model.GestureLinked;
import cn.edu.nuaa.gesture.model.NamedGesture;
import cn.edu.nuaa.gesture.utils.DatabaseHandler;

public class LinkAppActivity extends AppCompatActivity {

    /*
    * 应用程序集合
    */
    private ArrayList<AppInfo> appInfos;
    private ListView lv_app;
    private SQLiteDatabase db;
    private DatabaseHandler dbHandler;
    /*
     * 管理应用程序包，并通过它获取程序信息
     */
    private PackageManager pm;
    private Map<Integer, Boolean> isSelected;
    private List beSelectedData = new ArrayList();
    private String gestureName;
    private String appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_app);
        dbHandler=new DatabaseHandler(this);
        //Getting the Bundle object that pass from another activity
        Bundle bundle = getIntent().getExtras();
        NamedGesture namedGesture = (NamedGesture)bundle.getParcelable("selectedObject");
        Boolean mIsLink=bundle.getBoolean("isLink");
        //Toast.makeText(this,namedGesture.getName(),Toast.LENGTH_LONG).show();
        gestureName=namedGesture.getName();
        TextView mLinkShow= (TextView) findViewById(R.id.app_gesture_name);
        mLinkShow.setText(gestureName);
        //查询表信息
        GestureLinked gestureLink=dbHandler.getGestureLink(gestureName);
        appName=gestureLink.getAppName();
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
        initList();
        new Thread(runable).start();
    }

    void initList(){
        if (isSelected != null) {
            isSelected = null;
        }
        isSelected = new HashMap<Integer, Boolean>();
        // 清除已经选择的项
        if (beSelectedData.size() > 0) {
            beSelectedData.clear();
        }
        lv_app.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv_app.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("map", appInfos.get(position).toString());
            }
        });
        /*lv_app.setOnLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
                AppInfo appInfo = appInfos.get(pos);
                startApp(appInfo);
                return true;
            }
        });*/
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

    private void startApp(AppInfo appInfo) throws Exception {
        final String packName = appInfo.packageName.toString();
        final String activityName;
        activityName = getActivityName(packName);
        if (null == activityName) {
            Toast.makeText(LinkAppActivity.this, "程序无法启动", Toast.LENGTH_SHORT);
            return;
        }

        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packName, activityName));
        startActivity(intent);
    }

    /**
     * 获取启动相关程序的Activity
     * @param packName
     * @return
     * @throws Exception
     */
    private String getActivityName(String packName) throws Exception {
        final PackageInfo packInfo = pm.getPackageInfo(packName, PackageManager.GET_ACTIVITIES);
        final ActivityInfo[] activitys = packInfo.activities;
        if (null == activitys || activitys.length <= 0) {
            return null;
        }
        return activitys[0].name;
    }

    /**
     * 加载应用列表
     */
    private void loadApplications() {
        PackageManager manager = this.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
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
            for (int i = 0; i < appInfos.size(); i++) {
                if(appName.equals(appInfos.get(i).title)) {
                    isSelected.put(i, true);
                }else{
                    isSelected.put(i,false);
                }
            }
        }
    }

    class ShowAppListAdapter extends BaseAdapter {
        private ArrayList<AppInfo> appList;
        private LayoutInflater inflater;

        public ShowAppListAdapter(Context context, ArrayList<AppInfo> appList,PackageManager pm) {
            this.appList = appList;
            inflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return appList.size();
        }


        public Object getItem(int position) {
            return appList.get(position);
        }


        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final AppInfo info = appList.get(position);
            ViewHolder holder = null;
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.app_list_item, null);
                holder = new ViewHolder();
                holder.lv_image = (ImageView) convertView.findViewById(R.id.app_icon);
                holder.lv_name = (TextView) convertView.findViewById(R.id.lv_item_appname);
                holder.lv_packname = (TextView) convertView.findViewById(R.id.lv_item_packageame);
                holder.lv_appchecked = (CheckBox) convertView.findViewById(R.id.lv_app_checked);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.lv_image.setImageDrawable(info.icon);
            final CharSequence name = info.title;
            final CharSequence packName = info.packageName;
            holder.lv_name.setText(name);
            holder.lv_packname.setText(packName);

            holder.lv_appchecked.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // 当前点击的CB
                    boolean cu = !isSelected.get(position);
                    // 先将所有的置为FALSE
                    for(Integer p : isSelected.keySet()) {
                        isSelected.put(p, false);
                    }
                    // 再将当前选择CB的实际状态
                    isSelected.put(position, cu);
                    ShowAppListAdapter.this.notifyDataSetChanged();
                    beSelectedData.clear();
                    if(cu) beSelectedData.add(appList.get(position));
                    GestureLinked gestureLink=dbHandler.getGestureLink(gestureName);
                    if(null!=gestureLink){
                        gestureLink.setGestureName(gestureName);
                        gestureLink.setAppName((String) name);
                        gestureLink.setPackageName((String) packName);
                        gestureLink.setType(2);
                        dbHandler.updateGestureLink(gestureLink);
                    }else{
                        gestureLink=new GestureLinked();
                        gestureLink.setGestureName(gestureName);
                        gestureLink.setAppName((String) name);
                        gestureLink.setPackageName((String) packName);
                        gestureLink.setType(2);
                        dbHandler.addGestureLink(gestureLink);
                    }
                }
            });
            holder.lv_appchecked.setChecked(isSelected.get(position));
            return convertView;
        }

        private class ViewHolder {
            ImageView lv_image;
            TextView lv_name;
            TextView lv_packname;
            CheckBox lv_appchecked;
        }
    }

}
