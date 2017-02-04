package cn.edu.nuaa.gesture.activity;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureStore;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.edu.nuaa.gesture.R;
import cn.edu.nuaa.gesture.model.NamedGesture;
import cn.edu.nuaa.gesture.utils.Global;

/**
 * Created by terry on 2017/1/23.
 */
public class GestureListActivity extends ListActivity{
    private static final int STATUS_SUCCESS = 0;
    private static final int STATUS_CANCELLED = 1;
    private static final int STATUS_NO_STORAGE = 2;
    private static final int STATUS_NOT_LOADED = 3;
    private static GestureStore sGestureStore;
    private static GestureLibrary sStore;
    private static final int DIALOG_RENAME_GESTURE = 1;
    private static final int REQUEST_NEW_GESTURE=1;
    private static final int MENU_ID_RENAME = 1;
    private static final int MENU_ID_REMOVE = 2;
    private GesturesAdapter mGesturesAdapter;
    private GesturesLoadTask mTask;
    private TextView mEmpty;
    private EditText mInput;
    private NamedGesture mCurrentRenameGesture;
    private static final String GESTURES_INFO_ID = "gestures.info_id";
    private Dialog mRenameDialog;


    private final Comparator<NamedGesture> mSorter = new Comparator<NamedGesture>() {
        public int compare(NamedGesture object1, NamedGesture object2) {
            return object1.getName().compareTo(object2.getName());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesture_list);
        //listView加载
        mGesturesAdapter=new GesturesAdapter(this);
        setListAdapter(mGesturesAdapter);
        if(null==sStore){
            sStore= GestureLibraries.fromFile(Global.mStoreFile);
        }
        mEmpty=(TextView)findViewById(R.id.loadlist_show);//listView加载文字描述
        //手势加载
        loadGestures();
        //注册长按点击事件
        registerForContextMenu(getListView());
        //到新增页
        Button mToAddPageButton=(Button)findViewById(R.id.gesture_button_toaddpage);
        mToAddPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GestureListActivity.this,GestureAddActivity.class);
                startActivityForResult(intent,REQUEST_NEW_GESTURE);
            }
        });
        //重新加载
        Button mReloadButton=(Button)findViewById(R.id.gesture_button_reload);
        mReloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadGestures();
            }
        });
        //到识别页
        Button mPerformButton=(Button)findViewById(R.id.gesture_button_perform);
        mPerformButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GestureListActivity.this,GestureRecognizeActivity.class);
                startActivity(intent);
            }
        });
        //菜单弹出框
        //showDialog();
    }

    static GestureLibrary getStore() {
        return sStore;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=mTask&&mTask.getStatus()!=GesturesLoadTask.Status.FINISHED){
            mTask.cancel(true);
            mTask=null;
        }
        cleanupRenameDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case REQUEST_NEW_GESTURE:
                    loadGestures();
                    break;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(null!=mCurrentRenameGesture){
            outState.putLong(GESTURES_INFO_ID,mCurrentRenameGesture.getGesture().getID());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        Long id=state.getLong(GESTURES_INFO_ID,-1);
        if(id!=-1){
            final Set<String> entries=sStore.getGestureEntries();
            out:for (String name : entries) {
                for (Gesture gesture : sStore.getGestures(name)) {
                    if (gesture.getID() == id) {
                        mCurrentRenameGesture = new NamedGesture();
                        mCurrentRenameGesture.setName(name);
                        mCurrentRenameGesture.setGesture(gesture);
                        break out;
                    }
                }
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info =(AdapterView.AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle(((TextView) info.targetView).getText());
        //菜单列表
        menu.add(0, MENU_ID_RENAME, 0, R.string.gesture_rename);
        menu.add(0, MENU_ID_REMOVE, 0, R.string.gesture_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo menuInfo=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        final NamedGesture namedGesture=(NamedGesture)menuInfo.targetView.getTag();
        switch (item.getItemId()) {
            case MENU_ID_RENAME:
                renameGesture(namedGesture);
                return true;
            case MENU_ID_REMOVE:
                deleteGesture(namedGesture);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_RENAME_GESTURE) {
            return createRenameDialog();
        }
        return super.onCreateDialog(id);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        if (id == DIALOG_RENAME_GESTURE) {
            mInput.setText(mCurrentRenameGesture.getName());
        }
    }

    private Dialog createRenameDialog() {
        final View layout = View.inflate(this, R.layout.dialog_rename, null);
        mInput = (EditText) layout.findViewById(R.id.gesture_name);
        ((TextView) layout.findViewById(R.id.gesture_label)).setText(R.string.gesture_rename_label);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(0);
        builder.setTitle(getString(R.string.gesture_rename_title));
        builder.setCancelable(true);
        builder.setOnCancelListener(new Dialog.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                cleanupRenameDialog();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel_action), new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cleanupRenameDialog();
                    }
                }
        );
        builder.setPositiveButton(getString(R.string.rename_action),
                new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        changeGestureName();
                    }
                }
        );
        builder.setView(layout);
        return builder.create();
    }

    /**public void showDialog() {
        DialogFragment newFragment = RenameDialog.newInstance(getString(R.string.gesture_rename_title));
        newFragment.show(getFragmentManager(), "dialog");
    }**/

    public void doPositiveClick() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Positive click!");
    }

    public void doNegativeClick() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Negative click!");
    }

    private void changeGestureName() {
        final String name = mInput.getText().toString();
        if (!TextUtils.isEmpty(name)) {
            final NamedGesture renameGesture = mCurrentRenameGesture;
            final GesturesAdapter adapter = mGesturesAdapter;
            final int count = adapter.getCount();

            // Simple linear search, there should not be enough items to warrant
            // a more sophisticated search
            for (int i = 0; i < count; i++) {
                final NamedGesture gesture = adapter.getItem(i);
                if (gesture.getGesture().getID() == renameGesture.getGesture().getID()) {
                    sStore.removeGesture(gesture.getName(), gesture.getGesture());
                    gesture.setName(mInput.getText().toString());
                    sStore.addGesture(gesture.getName(), gesture.getGesture());
                    break;
                }
            }

            adapter.notifyDataSetChanged();
        }
        mCurrentRenameGesture = null;
    }

    /**
     * 重命名手势
     * @param gesture
     */
    private void renameGesture(NamedGesture gesture) {
        mCurrentRenameGesture = gesture;
        showDialog(DIALOG_RENAME_GESTURE);
    }

    /**
     * 加载手势
     */
    private void loadGestures(){
        if(null!=mTask&&mTask.getStatus()!=GesturesLoadTask.Status.FINISHED){
            mTask.cancel(true);
        }
        //mTask= (GesturesLoadTask)new GesturesLoadTask().execute();
        mTask=new GesturesLoadTask();
        mTask.execute();
    }
    private void checkForEmpty() {
        if (mGesturesAdapter.getCount() == 0) {
            mEmpty.setText(R.string.gestures_empty);
        }else{
            mEmpty.setText("");
        }
    }

    private  void cleanupRenameDialog(){
        if(null!=mRenameDialog){
            mRenameDialog.dismiss();
            mRenameDialog=null;
        }
        mCurrentRenameGesture=null;
    }

    /**
     * 删除手势
     * @param gesture
     */
    private void deleteGesture(NamedGesture gesture){
        sStore.removeGesture(gesture.getName(),gesture.getGesture());
        sStore.save();
        final GesturesAdapter adapter=mGesturesAdapter;
        adapter.setNotifyOnChange(false);
        adapter.remove(gesture);
        adapter.sort(mSorter);
        checkForEmpty();
        adapter.notifyDataSetChanged();
        Toast.makeText(this,R.string.gesture_delete_success,Toast.LENGTH_LONG).show();
    }


    private class GesturesLoadTask extends AsyncTask<Void,NamedGesture,Integer>{
        private int mThumbnailSize;
        private int mThumbnailInset;
        private int mPathColor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            final Resources resources=getResources();
            mPathColor= resources.getColor(R.color.gesture_color);
            mThumbnailSize=(int)resources.getDimension(R.dimen.gesture_thumbnail_size);
            mThumbnailInset=(int)resources.getDimension(R.dimen.gesture_thumbnail_inset);
            findViewById(R.id.gesture_button_toaddpage).setEnabled(false);
            findViewById(R.id.gesture_button_reload).setEnabled(false);
            mGesturesAdapter.setNotifyOnChange(false);//降低ui的处理量，刷新ui可以更快速
            mGesturesAdapter.clear();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            if (isCancelled()) return STATUS_CANCELLED;
            if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {//MEDIA_MOUNTED:存储媒体已经挂载，并且挂载点可读/写
                return STATUS_NO_STORAGE;
            }

            final GestureLibrary store = sStore;

            if (store.load()) {
                for (String name : store.getGestureEntries()) {
                    if (isCancelled()) break;

                    for (Gesture gesture : store.getGestures(name)) {
                        final Bitmap bitmap = gesture.toBitmap(mThumbnailSize, mThumbnailSize, mThumbnailInset, mPathColor);
                        final NamedGesture namedGesture = new NamedGesture();
                        namedGesture.setGesture(gesture);
                        namedGesture.setName(name);

                        mGesturesAdapter.addBitmap(namedGesture.getGesture().getID(), bitmap);
                        publishProgress(namedGesture);
                    }
                }

                return STATUS_SUCCESS;
            }

            return STATUS_NOT_LOADED;
        }

        @Override
        protected void onProgressUpdate(NamedGesture... values) {
            super.onProgressUpdate(values);
            final GesturesAdapter adapter=mGesturesAdapter;
            adapter.setNotifyOnChange(false);
            for(NamedGesture namedGesture:values){
                adapter.add(namedGesture);
            }
            adapter.sort(mSorter);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == STATUS_NO_STORAGE) {
                getListView().setVisibility(View.GONE);
                mEmpty.setVisibility(View.VISIBLE);
                mEmpty.setText(getString(R.string.gestures_error_loading, Global.mStoreFile.getAbsolutePath()));
            } else {
                findViewById(R.id.gesture_button_toaddpage).setEnabled(true);
                findViewById(R.id.gesture_button_reload).setEnabled(true);
                checkForEmpty();
            }
        }
    }

    private class GesturesAdapter extends ArrayAdapter<NamedGesture>{
        private final LayoutInflater mLayoutInflater;
        private final Map<Long,Drawable> mThumbnails= Collections.synchronizedMap(new HashMap<Long,Drawable>());
        public GesturesAdapter(Context context){
            super(context,0);
            mLayoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        void addBitmap(Long id,Bitmap bitmap){
            mThumbnails.put(id,new BitmapDrawable(bitmap));
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //return super.getView(position, convertView, parent);
            if(null==convertView){
                convertView=mLayoutInflater.inflate(R.layout.gesture_item,parent,false);
            }
            NamedGesture namedGesture=getItem(position);
            TextView textView=(TextView)convertView;
            textView.setTag(namedGesture);
            textView.setText(namedGesture.getName());
            textView.setCompoundDrawablesWithIntrinsicBounds(mThumbnails.get(namedGesture.getGesture().getID()),null,null,null);
            return convertView;
        }
    }
}
