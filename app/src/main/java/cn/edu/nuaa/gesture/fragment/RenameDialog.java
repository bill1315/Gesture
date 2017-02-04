package cn.edu.nuaa.gesture.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import cn.edu.nuaa.gesture.R;
import cn.edu.nuaa.gesture.activity.GestureListActivity;

/**
 * Created by terry on 2017/1/24.
 */

public class RenameDialog extends DialogFragment {
    public static RenameDialog newInstance(String title) {
        RenameDialog fragment = new RenameDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
       /** String title = getArguments().getString("title");
        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_launcher)
                .setTitle(title)
                .setPositiveButton(getString(R.string.rename_action),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((GestureListActivity)getActivity()).doPositiveClick();
                            }
                        })
                .setNegativeButton(getString(R.string.cancel_action),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((GestureListActivity)getActivity()).doNegativeClick();
                            }
                        })
                .create();
        **/
        return null;
    }
}
