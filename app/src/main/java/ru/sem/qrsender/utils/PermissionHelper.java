package ru.sem.qrsender.utils;


import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.sem.qrsender.ui.MainActivity;

public class PermissionHelper {

    private static final String TAG = "PermissionHelper";
    private String snackMessage;
    private boolean shouldShowRationale;
    private View view;
    private Activity activity;
    private OnPermissionResultImpl onPermissionResult;

    public <T extends View> PermissionHelper(MainActivity mainActivity, T viewById) {
    }

    public interface OnPermissionResultImpl{
        void onPermissionsResult(int requestCode, boolean isGranted);
    }


    public PermissionHelper(Activity activity, View view, OnPermissionResultImpl onPermissionResult) {
        this.activity = activity;
        this.view = view;
        this.onPermissionResult = onPermissionResult;
    }

    private String[] getNeededPermissions(String[] permissions){
        List<String> permissionsNeeded = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            final String perm = permissions[i];
            if (ContextCompat.checkSelfPermission(activity, perm) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, perm)) {
                    this.shouldShowRationale=true;
                    permissionsNeeded.add(perm);
                }else permissionsNeeded.add(perm);
            }
        }
        Log.d(TAG, "needed="+permissionsNeeded.toString());
        return permissionsNeeded.toArray(new String[permissionsNeeded.size()]);
    }

    public void requestPermissions(@NonNull String[] permissions, final int requestCode,
                                   String snackMessage){
        shouldShowRationale=false;
        this.snackMessage = snackMessage;
        final String[] permissionsNeeded = getNeededPermissions(permissions);
        if(permissionsNeeded.length>0) {
            if (shouldShowRationale) {
                Snackbar snack = Snackbar.make(view, snackMessage, Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.YELLOW)
                        .setAction("OK", view1 -> ActivityCompat.requestPermissions(activity,
                                permissionsNeeded, requestCode));
                View view = snack.getView();
                TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.WHITE);
                snack.show();
            } else {
                ActivityCompat.requestPermissions(activity,
                        permissionsNeeded, requestCode);
            }
        }else{//все права есть
            onPermissionResult.onPermissionsResult(requestCode, true);
        }
    }

    public void onPermissionsResult(int requestCode, @NonNull String[] permissions,
                                      @NonNull int[] grantResults){
        boolean isGranted=true;
        for(int grantR: grantResults){
            if(grantR!=PackageManager.PERMISSION_GRANTED){
                isGranted=false;
                break;
            }
        }
        onPermissionResult.onPermissionsResult(requestCode, isGranted);
    }

}
