package com.wish.mysecretary;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;


import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class MyService extends Service {
    ClipboardManager.OnPrimaryClipChangedListener mPClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
        @Override
        public void onPrimaryClipChanged() {
            ClipboardManager mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData mClipData = mClipboardManager.getPrimaryClip();
            Log.e("ClipData",String.valueOf(mClipData.getItemAt(0)));
        }
    };

    @Override
    public void onCreate(){
        super.onCreate();
        Log.e("C","onCreate");
        ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).addPrimaryClipChangedListener(mPClipChangedListener);
    }

//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    @Override
//    protected void onHandleIntent(Intent intent) {
//        if(MainActivity.App.getBoolean("showNoti",true)){
//            final int notifyID = 1; // 通知的識別號碼
//            //notID.edit().putInt("ID",notifyID).commit();
//            final int requestCode = notifyID; // PendingIntent的Request Code
//            final Intent openintent = new Intent(getApplicationContext(), MainActivity.class);
//            // 開啟另一個Activity的Intent
//            final int flags = PendingIntent.FLAG_UPDATE_CURRENT;
//            // ONE_SHOT：PendingIntent只使用一次；CANCEL_CURRENT：PendingIntent執行前會先結束掉之前的；NO_CREATE：沿用先前的PendingIntent，不建立新的PendingIntent；
//            // UPDATE_CURRENT：更新先前PendingIntent所帶的額外資料，並繼續沿用
//            final TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
//            // 建立TaskStackBuilder
//            stackBuilder.addParentStack(MainActivity.class);
//            // 加入目前要啟動的Activity，這個方法會將這個Activity的所有上層的Activity(Parents)都加到堆疊中
//            stackBuilder.addNextIntent(openintent);
//            // 加入啟動Activity的Intent
//
//            final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestCode, openintent, flags);
//            final int priority = Notification.PRIORITY_MAX;
//            final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            final Notification notification = new Notification.Builder(getApplicationContext())
//                    .setContentTitle("我的專屬助理正在背景執行...")
//                    .setContentText("Running in background")
//                    .setContentIntent(pendingIntent)
//                    .build();
//            notification.flags |= Notification.FLAG_ONGOING_EVENT;
//            notificationManager.notify(notifyID, notification);
//        }
//   }
    public void onDestroy() {
        super.onDestroy();
        Log.e("D", "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

