package cn.edu.nuaa.gesture.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.edu.nuaa.gesture.R;
import cn.edu.nuaa.gesture.model.AppInfo;

/**
 * Created by terry on 2017/3/14.
 */

public class ShowAppListAdapter extends BaseAdapter {
    private ArrayList<AppInfo> appList;
    private LayoutInflater inflater;

    public ShowAppListAdapter(Context context, ArrayList<AppInfo> appList,
                              PackageManager pm) {
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


    public View getView(int position, View convertView, ViewGroup parent) {
        final AppInfo info = appList.get(position);
        ViewHolder holder = null;
        if(null == convertView){
            convertView = inflater.inflate(R.layout.app_list_item, null);
            holder = new ViewHolder();
            holder.lv_image = (ImageView) convertView.findViewById(R.id.app_icon);
            holder.lv_name = (TextView) convertView.findViewById(R.id.lv_item_appname);
            holder.lv_packname = (TextView) convertView.findViewById(R.id.lv_item_packageame);
            holder.lv_appchecked = (CheckBox) convertView.findViewById(R.id.lv_app_checked);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.lv_image.setImageDrawable(info.icon);
        final CharSequence name = info.title;
        final CharSequence packName = info.packageName;
        holder.lv_name.setText(name);
        holder.lv_packname.setText(packName);

        holder.lv_appchecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    System.out.print("success");
                }else{
                    System.out.print("canel");
                }
            }
        });
        return convertView;
    }
    private final static  class ViewHolder{
        ImageView lv_image;
        TextView lv_name;
        TextView lv_packname;
        CheckBox lv_appchecked;
    }
}
