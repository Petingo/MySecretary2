package com.wish.mysecretary;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.AvoidXfermode;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends ActionBarActivity {
    private static Context c;
    static public String matchingValue;
    public static boolean nomatterwhat = true;
    static private KeywordDBhelper wdbhelper = null;
    static private SQLiteDatabase wdb;
    static private UserKeywordDBhelper uwdbhelper = null;
    static private SQLiteDatabase uwdb;
    public static SharedPreferences App;
    public static SharedPreferences runningOrNot;
    /*Announce of ClipBoard*/
    ClipboardManager myClipBoard;
    static boolean bHasClipChangedListener = false;

    ClipboardManager.OnPrimaryClipChangedListener mPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {
            ClipData clipData = myClipBoard.getPrimaryClip();
            String clipDatatmp = clipData.getItemAt(0).toString().replace("ClipData.Item { T:", "");
            clipDatatmp = clipDatatmp.replace(" }", "");
            Log.d("***** clip changed,", "clipData:" + clipData.getItemAt(0));
            nomatterwhat = false;
            if (!clipDatatmp.equals(App.getString("clipContent", null))) {
                MainActivity.analaysis(clipDatatmp);
                App.edit().putString("clipContent", clipDatatmp).apply();
            }
        }
    };

    private void RegPrimaryClipChanged() {
        if (!bHasClipChangedListener) {
            myClipBoard.addPrimaryClipChangedListener(mPrimaryClipChangedListener);
            bHasClipChangedListener = true;
        }
    }

    private void UnRegPrimaryClipChanged() {
        if (bHasClipChangedListener) {
            myClipBoard.removePrimaryClipChangedListener(mPrimaryClipChangedListener);
            bHasClipChangedListener = false;
        }
    }
    /*End of Announce of ClipBoard*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);

        final SharedPreferences pref;
        pref = getSharedPreferences("com.wish.mysecretary", MODE_PRIVATE);
        App = getSharedPreferences("com.wish.mysecretary", MODE_PRIVATE);
        runningOrNot = getSharedPreferences("com.wish.mysecretary", MODE_PRIVATE);
        if (pref.getBoolean("firstrun", true)) {
            copyKeyworddb();
            showintro();
            pref.edit().putBoolean("firstrun", false).apply();
            pref.edit().putInt("version", 3).apply();
        }
        Log.e("version", String.valueOf(pref.getInt("version", 0)));
        if (pref.getInt("version", 0) < 3) {
            copyKeyworddb();
            pref.edit().putBoolean("updatefirstrun", false).apply();
            pref.edit().putInt("version", 3).apply();
        }

        wdbhelper = new KeywordDBhelper(this);
        wdb = wdbhelper.getWritableDatabase();

        uwdbhelper = new UserKeywordDBhelper(this);
        uwdb = uwdbhelper.getWritableDatabase();
        c = this;

        Button btn1 = (Button) findViewById(R.id.button1);
        btn1.setOnClickListener(new View.OnClickListener() {
            final Cursor wc = wdb.rawQuery("Select * from keyword order by weight DESC", null);

            @Override
            public void onClick(View arg4) {
                TextView editkeyin = (TextView) findViewById(R.id.editText4);
                String tmp;
                tmp = editkeyin.getText().toString();
                if (tmp.isEmpty()) {
                    Toast.makeText(MainActivity.this, "請在上方輸入！", Toast.LENGTH_SHORT).show();
                } else {
                    editkeyin.setText("");
                    nomatterwhat = true;
                    analaysis(tmp);
                }

            }

        });
        Button addNoti = (Button) findViewById(R.id.addNoti);
        addNoti.setOnClickListener(new View.OnClickListener() {
            final Cursor wc = wdb.rawQuery("Select * from keyword order by weight DESC", null);

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View arg4) {
                TextView editkeyin = (TextView) findViewById(R.id.editText4);
                String tmp;
                tmp = editkeyin.getText().toString();
                if (tmp.isEmpty()) {
                    Toast.makeText(MainActivity.this, "請在上方輸入！", Toast.LENGTH_SHORT).show();
                } else {
                    editkeyin.setText("");
                    pref.edit().putInt("noti", pref.getInt("noti", 0) + 1).apply();

                    final int notifyID = 5;
                    final int priority = Notification.PRIORITY_MAX;
                    //final Intent intent = new Intent(getApplicationContext(), null);
                    final int flags = PendingIntent.FLAG_ONE_SHOT;
                    final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), notifyID, new Intent(), flags);
                    final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
                    final Notification notification = new Notification.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.ic_stat_smallicon_01)
                            .setContentTitle(tmp)
                            .setContentText("點擊以消除")
                            .setPriority(priority)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setOngoing(true)
                            .build();
                            /*.setOngoing(true)*/
                    notificationManager.notify(tmp, notifyID, notification);
                }

            }

        });
        final TextView DialogV = (TextView) findViewById(R.id.dialogView);
        final ImageButton SmileButton = (ImageButton) findViewById(R.id.smileButton);
        if (runningOrNot.getBoolean("chk", true)) {
            DialogV.setText("點我的臉開始背景服務...");
            SmileButton.setImageResource(R.drawable.icon_off);
        } else {
            DialogV.setText("我的專屬助理正在為您服務...");
            SmileButton.setImageResource(R.drawable.icon_on1);
        }
        SmileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (runningOrNot.getBoolean("chk", true)) {
                    Toast.makeText(MainActivity.this, "start", Toast.LENGTH_SHORT).show();
                    DialogV.setText("我的專屬助理正在為您服務...");
                    runningOrNot.edit().putBoolean("chk", false).apply();

                    myClipBoard = (ClipboardManager) MainActivity.this.getSystemService(android.content.Context.CLIPBOARD_SERVICE);
                    RegPrimaryClipChanged();

                    SmileButton.setImageResource(R.drawable.icon_on2);
                    SmileButton.setImageResource(R.drawable.icon_on1);

                    Intent intent = new Intent(MainActivity.this, MyService.class);
                    startService(intent);
                } else {
                    Toast.makeText(MainActivity.this, "end", Toast.LENGTH_SHORT).show();
                    DialogV.setText("點我的臉開始背景服務...");
                    runningOrNot.edit().putBoolean("chk", true).apply();

                    UnRegPrimaryClipChanged();

                    SmileButton.setImageResource(R.drawable.icon_on2);
                    SmileButton.setImageResource(R.drawable.icon_off);

                    Intent intent = new Intent(MainActivity.this, MyService.class);
                    stopService(intent);

                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.cancel(1);
                }
            }
        });
    }


    private void addKW(String tmp1, String tmp2, String tmp3) {
        SQLiteDatabase uwdb = uwdbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        int temp = Integer.valueOf(tmp3);
        values.put("word", tmp1);
        values.put("content", tmp2);
        values.put("type", temp);
        uwdb.insert("keyword", null, values);
    }

    public void showintro() {
        AlertDialog.Builder MyAlertDialog = new AlertDialog.Builder(this);
        MyAlertDialog.setTitle("使用說明：");
        MyAlertDialog.setMessage(
                "輸入「明天下午三點要開會」\n" +
                        "或是後天、下禮拜三、5月5號…\n" +
                        "然後按下OK鍵\n" +
                        "試試看吧\n\n" +
                        "更詳細的說明請至Play Store查看\n" +
                        "謝謝您的支持\n\n" +
                        "*當程式不斷閃退，請點擊右上角的三個小白點重置聊天記錄檔\n");
        DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        };
        MyAlertDialog.setPositiveButton("OK", OkClick);
        MyAlertDialog.show();
    }

    public void copyKeyworddb() {
        String DATABASE_PATH = "/data/data/com.wish.mysecretary/databases/";
        String DATABASE_FILENAME = "keyword.db";
        String databaseFilename = DATABASE_PATH + DATABASE_FILENAME;
        File dir = new File(DATABASE_PATH);

        if (!dir.exists())
            dir.mkdir();

        FileOutputStream os = null;
        try {

            os = new FileOutputStream(databaseFilename);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        InputStream is = getResources().openRawResource(R.raw.keyword);
        byte[] buffer = new byte[8192];
        int count;
        // 开始复制db文件
        try {
            while ((count = is.read(buffer)) > 0) {
                os.write(buffer, 0, count);
                os.flush();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            is.close();
            os.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View addKWlayout = inflater.inflate(R.layout.manyedittext, null);
        final EditText word = (EditText) addKWlayout.findViewById(R.id.input1);
        final EditText content = (EditText) addKWlayout.findViewById(R.id.input2);

        final Spinner spin = (Spinner) addKWlayout.findViewById(R.id.type_spinner);
        final ArrayAdapter<String> arrayAdapter;
        final String[] list = {"地點", "排除"};
        arrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, list);
        spin.setAdapter(arrayAdapter);
        switch (id) {
            case (R.id.opt1):
                dialog
                        .setTitle("Add Keyword")
                        .setView(addKWlayout)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.e("onClick", "successful");
                                if (list[spin.getSelectedItemPosition()].equals("地點"))
                                    addKW(word.getText().toString(), content.getText().toString(), "3");
                                else
                                    addKW(word.getText().toString(), content.getText().toString(), "4");
                                Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                break;
            case (R.id.opt2):
                copyKeyworddb();
                Toast.makeText(MainActivity.this, "Succeed", Toast.LENGTH_SHORT).show();
                break;
            case (R.id.Intro):
                showintro();
                break;
            case (R.id.Report):
                Uri uri = Uri.parse("http://goo.gl/forms/fBl7L1XW8l");
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
        }
        return true;

    }

    public static boolean analaysis(String tmp) {
        Cursor wc = wdb.rawQuery("Select * from keyword order by weight DESC", null);
        Cursor uwc = uwdb.rawQuery("Select * from keyword order by weight DESC", null);

        String kwd;
        String place;
        String content;
        String replace;

        int type;
        long beginTimetmp1;
        boolean add = false;
        boolean drop = true;
        boolean GoNatty = false;
        boolean Eng = false;

        if (tmp.getBytes().length == tmp.length()) {
            Eng = true;
        }
        Calendar beginTime = Calendar.getInstance();
        int wcCount = wc.getCount();
        int uwcCount = uwc.getCount();

        beginTimetmp1 = 0;
        place = "";

        if (!Eng) {
            wc.moveToFirst();
            for (int i = 0; i < wcCount; i++) {
                kwd = wc.getString(1);
                type = wc.getInt(5);
                if (kwd.isEmpty()) {
                    continue;
                }
                if (tmp != null && tmp.contains(kwd)) {
                    content = wc.getString(2);
                    replace = wc.getString(4);
                    if (content != null && type != 3) {
                        beginTimetmp1 += Long.valueOf(content);
                    }
                    if (replace == null) {
                        tmp = tmp.replace(kwd, "");
                    } else {
                        tmp = tmp.replace(kwd, replace);
                    }
                    Log.e("kwd", kwd);
                    Log.e("type", String.valueOf(type));
                    switch (type) {
                        case 1:
                            GoNatty = true;
                            break;
                        case 3:
                            place = content;
                            break;
                        case 4:
                            drop = false;
                            i = wcCount;
                            break;
                    }
                    if (type != 2) {
                        add = true;
                    }
                    Log.e("BT", String.valueOf(beginTimetmp1));
                    Log.e("tmp", tmp);
                }
                wc.moveToNext();
            }
            uwc.moveToFirst();
            for (int i = 0; i < uwcCount; i++) {
                kwd = uwc.getString(1);
                type = uwc.getInt(5);
                Log.e("userKWD", kwd);
                if (kwd.isEmpty()) {
                    continue;
                }
                if (tmp != null && tmp.contains(kwd)) {
                    content = uwc.getString(2);
                    replace = uwc.getString(4);
                    if (content != null && type != 3) {
                        beginTimetmp1 += Long.valueOf(content);
                    }
                    if (replace == null) {
                        tmp = tmp.replace(kwd, "");
                    } else {
                        tmp = tmp.replace(kwd, replace);
                    }
                    Log.e("kwd", kwd);
                    Log.e("type", String.valueOf(type));
                    switch (type) {
                        case 1:
                            GoNatty = true;
                            break;
                        case 3:
                            place = content;
                            break;
                        case 4:
                            drop = false;
                            i = uwcCount;
                            break;
                    }
                    if (type != 2) {
                        add = true;
                    }
                    Log.e("BT", String.valueOf(beginTimetmp1));
                    Log.e("tmp", tmp);
                }
                uwc.moveToNext();
            }
        }
        if (GoNatty) {
            String out;
            out = Natty.getResult(tmp);
            if (!matchingValue.isEmpty()) {
                String[] cutOut = out.split(" ");

                beginTime.set(Integer.parseInt(cutOut[7]), Integer.parseInt(cutOut[1]), Integer.parseInt(cutOut[2]) + 1, 0, 0);
                Log.e("Natty", beginTime.toString());
                Log.e("matchingValue", matchingValue);
                tmp = tmp.replace(matchingValue, "");
            }
        }

        if (Eng) {
            String out;
            out = Natty.getResult(tmp);
            if (!matchingValue.isEmpty()) {
                String[] cutOut = out.split(" ");
                Calendar Timetemp = Calendar.getInstance();
                Timetemp.set(Integer.parseInt(cutOut[7]), Integer.parseInt(cutOut[1]), Integer.parseInt(cutOut[2]), Integer.parseInt(cutOut[3]), Integer.parseInt(cutOut[4]));
                beginTimetmp1 = Timetemp.getTimeInMillis();
                beginTime.setTimeInMillis(0);
                Log.e("ENGbeginTime", String.valueOf(beginTimetmp1));
                Log.e("matchingValue", matchingValue);
                tmp = tmp.replace(matchingValue, "");
            }
        }


        Long datetmp = beginTime.getTimeInMillis();
        Log.e("beginTime", String.valueOf(beginTime));
        datetmp -= datetmp % 86400000;
        //////////
        Log.e("Btt1", String.valueOf(beginTimetmp1));
        if (tmp.contains("早上") && beginTimetmp1 % 86400000 != 0)
            tmp = tmp.replace("早上", "");
        if (tmp.contains("中午")) {
            if (beginTimetmp1 % 86400000 == 0)
                beginTimetmp1 += 39600000;
            else
                tmp = tmp.replace("中午", "");
        }
        if (tmp.contains("下午")) {
            if (beginTimetmp1 % 86400000 == 0)
                beginTimetmp1 += 50400000;
            else {
                beginTimetmp1 += 43200000;
                tmp = tmp.replace("下午", "");
            }
        }
        if (tmp.contains("傍晚")) {
            if (beginTimetmp1 % 86400000 == 0)
                beginTimetmp1 += 61200000;
            else {
                beginTimetmp1 += 43200000;
                tmp = tmp.replace("傍晚", "");
            }
        }
        if (tmp.contains("晚上")) {
            if (beginTimetmp1 % 86400000 == 0)
                beginTimetmp1 += 72000000;
            else {
                beginTimetmp1 += 43200000;
                tmp = tmp.replace("晚上", "");
            }
        }
        if (beginTimetmp1 % 86400000 == 0)
            beginTimetmp1 += 28800000;

        //////////
        beginTime.setTimeInMillis(datetmp + beginTimetmp1);
        wc.moveToLast();
        String desc;
        desc = tmp.replace(" ", "");

        Log.e("Timezone", String.valueOf(beginTime.getTimeZone().getRawOffset()));
        Long Timezone = (long) beginTime.getTimeZone().getRawOffset();

        if (Eng) {
            Timezone = 0L;
        }

        if (beginTime.getTimeInMillis() < System.currentTimeMillis() + Timezone) {
            Long bTtmp = beginTime.getTimeInMillis();
            beginTime.setTimeInMillis(bTtmp + 43200000);
        }

        if (nomatterwhat || (add && drop)) {
            Log.e("建立事項", String.valueOf(beginTime.getTimeInMillis()));

            Intent intent = new Intent(c, MainActivity.class);
            Intent intent_cal = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    //.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, allday)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis() - Timezone)
                    .putExtra(CalendarContract.Events.TITLE, desc)
                    .putExtra(CalendarContract.Events.DESCRIPTION, "")
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, place)
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
            intent_cal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(intent_cal);
            intent_cal.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Log.e("calendar", "successful");
        }
        return add && drop;
    }
}