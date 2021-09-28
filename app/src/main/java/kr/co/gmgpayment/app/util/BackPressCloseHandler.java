package kr.co.gmgpayment.app.util;

import android.app.Activity;
import android.content.DialogInterface;

public class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
//    private Toast toast;
    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        activity.finish();
//        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
//            backKeyPressedTime = System.currentTimeMillis();
////            showGuide();
//            return;
//        }
//        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
//            activity.finish();
//            finishDialog();
//            toast.cancel();
//        }
    }

    public void showGuide() {
//        toast = Toast.makeText(activity, "'뒤로'버튼을 한번 더 누르시면", Toast.LENGTH_SHORT);
//        toast.show();
    }

    public void finishDialog(){
        android.app.AlertDialog.Builder gsDialog = new android.app.AlertDialog.Builder(activity);
        gsDialog.setTitle("알림");
        gsDialog.setMessage("앱을 종료하시겠습니까?");
        gsDialog.setCancelable(false);
        gsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
                return;
            }
        });
        gsDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        }).create().show();
    }
}
