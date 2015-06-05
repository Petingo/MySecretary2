package com.wish.mysecretary;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.provider.CalendarContract;
import android.util.Log;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class MyService extends IntentService {

    static public boolean b=true;
    public String matchingValue;
    public MyService() {
        super("MyService");
    }
    public MyService(String name) {
        super(name);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.e("C","onCreate");
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onHandleIntent(Intent intent) {
        //Random ran = new Random(System.currentTimeMillis());
        final int notifyID = 1; // 通知的識別號碼
        //notID.edit().putInt("ID",notifyID).commit();
        final int requestCode = notifyID; // PendingIntent的Request Code
        final Intent openintent = new Intent(getApplicationContext(), MainActivity.class); // 開啟另一個Activity的Intent
        final int flags = PendingIntent.FLAG_UPDATE_CURRENT; // ONE_SHOT：PendingIntent只使用一次；CANCEL_CURRENT：PendingIntent執行前會先結束掉之前的；NO_CREATE：沿用先前的PendingIntent，不建立新的PendingIntent；UPDATE_CURRENT：更新先前PendingIntent所帶的額外資料，並繼續沿用
        final TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext()); // 建立TaskStackBuilder
        stackBuilder.addParentStack(MainActivity.class); // 加入目前要啟動的Activity，這個方法會將這個Activity的所有上層的Activity(Parents)都加到堆疊中
        stackBuilder.addNextIntent(openintent); // 加入啟動Activity的Intent

        final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestCode, openintent, flags);
        final int priority = Notification.PRIORITY_MAX;
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final Notification notification = new Notification.Builder(getApplicationContext()).setSmallIcon(R.drawable.ic_launcher).setContentTitle("我的專屬助理正在背景執行...").setContentText("Running in background").setContentIntent(pendingIntent).build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(notifyID, notification);
        if(MainActivity.App.getBoolean("FB",true)||MainActivity.App.getBoolean("Line",true)) {
            while (b) {

                LineDBhelper dbhelper = null;
                SQLiteDatabase db;

                FBDBhelper fdbhelper = null;
                SQLiteDatabase fdb;

                KeywordDBhelper wdbhelper = null;
                SQLiteDatabase wdb;

                dbhelper = new LineDBhelper(this);
                db = dbhelper.getReadableDatabase();

                fdbhelper = new FBDBhelper(this);
                fdb = fdbhelper.getReadableDatabase();

                wdbhelper = new KeywordDBhelper(this);
                wdb = wdbhelper.getWritableDatabase();

                String tmp = "";
                String kwd = "";
                String place = "";
                String content = "";
                String replace = "";
                String ori = "";
                String from = "";
                String Modify = "";

                int type;
                long count;
                long beginTimetmp1;
                boolean add;
                boolean drop = true;
                boolean GoNatty = false;
                boolean Eng = false;

                ContentValues values = new ContentValues();

                if (MainActivity.App.getBoolean("Line", true)) {
                    Log.e("Line", "Start");
                    GetLineDB.copy();

                    Cursor c = db.rawQuery("Select * from chat_history", null);
                    Cursor cm = db.rawQuery("Select * from contacts", null);
                    Cursor wc = wdb.rawQuery("Select * from keyword order by weight DESC", null);

                    c.moveToLast();
                    int cCount = c.getCount();
                    int wcCount = wc.getCount();

                    for (int j = 0; j < cCount; j++) {
                        Calendar beginTime = Calendar.getInstance();
                        count = MainActivity.App.getLong("LineCount", 0);
                        String from_mid = c.getString(4);
                        if (from_mid == null) { //僅別人
                            c.moveToPrevious();
                            continue;
                        }
                        if (c.getInt(0) <= count) {
                            j = cCount;
                            continue;
                        }

                        MainActivity.App.edit().putLong("LineCount", Long.valueOf(c.getString(0))).apply();

                        tmp = c.getString(5);
                        ori = tmp;
                        if (tmp.getBytes().length == tmp.length()) {
                            Eng = true;
                            Log.e("All Eng", "true");
                        }
                        beginTimetmp1 = 0;
                        place = "";
                        add = false;
                        drop = true;
                        wc.moveToFirst();
                        for (int i = 0; i < wcCount && drop; i++) {
                            kwd = wc.getString(1);
                            if (kwd.isEmpty()) {
                                continue;
                            }
                            if (tmp != null && tmp.contains(kwd)) {
                                content = wc.getString(2);
                                replace = wc.getString(4);
                                if (content != null) {
                                    beginTimetmp1 += Long.valueOf(content);
                                }
                                if (replace == null) {
                                    tmp = tmp.replace(kwd, "");
                                } else {
                                    tmp = tmp.replace(kwd, replace);
                                }
                                Log.e("kwd", kwd);
                                type = wc.getInt(5);
                                switch (type) {
                                    case 1:
                                        GoNatty = true;
                                        break;
                                    case 3:
                                        place = content;
                                        break;
                                    case 4:
                                        drop = true;
                                        i = wcCount;
                                        break;
                                }
                                if (type != 2) {
                                    add = true;
                                }
                            }
                            wc.moveToNext();
                        }
                        int cmCount = cm.getCount();
                        cm.moveToFirst();
                        for (int i = 0; i < cmCount; i++) {
                            String cmname = cm.getString(3);
                            String cmserver_name = cm.getString(5);
                            if (tmp.contains(cmname) && !Modify.contains(cmname)) {
                                Modify += cmname;
                                Log.e("name", "get");
                            } else if (tmp.contains(cmserver_name) && !cmname.equals(cmserver_name) && !Modify.contains(cmname)) {
                                Modify += cmserver_name;
                                Log.e("servername", "get");
                            }
                            cm.moveToNext();
                        }

                        if (GoNatty || Eng) {
                            String out = "";
                            String testt = "";
                            out = Natty(tmp);
                            String[] cutOut = out.split(" ");
                            for (int i = 0; i < 8; i++) {
                                testt += cutOut[i] + '\n';
                            }

                            beginTime.set(Integer.parseInt(cutOut[7]), Integer.parseInt(cutOut[1]), Integer.parseInt(cutOut[2]) + 1, 0, 0);
                            Log.e("Natty", beginTime.toString());
                            Log.e("matchingValue", matchingValue);
                            tmp = tmp.replace(matchingValue, "");
                        }
                        if ((add && drop) || !(matchingValue == null || matchingValue.length() == 0)) {
                            Long datetmp = beginTime.getTimeInMillis() - beginTime.getTimeInMillis() % 86400000;
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
                            //cltmp %= 86400000;
                            //cltmp += beginTimetmp1;

                            wc.moveToLast();
                            String desc = tmp;
                            for (int i = 0; i < wcCount; i++) {
                                kwd = wc.getString(2);
                                type = wc.getInt(4);
                                if (kwd != null && desc.contains(kwd) && (type == 1 || type == 3)) {
                                    desc = desc.replace(kwd, "");
                                }
                                wc.moveToPrevious();
                            }
                            desc = desc.replace("  ", " ");
                            Log.e("建立事項", "");
                            Cursor cc = db.rawQuery("Select * from contacts", null);
                            Integer ccCount = cc.getCount();
                            cc.moveToFirst();
                            for (int icc = 0; icc < ccCount; icc++) {
                                if (cc.getString(0).equals(from_mid)) {
                                    from = cc.getString(3);
                                }
                                cc.moveToNext();
                            }

                            Long Timezone = Long.valueOf(28800000);
                            if (Eng)
                                Timezone = Long.valueOf(0);
                            Log.e(String.valueOf(beginTime.getTimeInMillis()), String.valueOf(System.currentTimeMillis()));
                            if (beginTime.getTimeInMillis() < System.currentTimeMillis() + Timezone) {
                                Log.e("set", "OK");
                                beginTime.setTimeInMillis(System.currentTimeMillis() + Timezone);
                            }

                            String Modify_toString = "";
                            if (!Modify.isEmpty()) {
                                Modify_toString += Modify + "將出席";
                            }
                            Intent intent_cal = new Intent(Intent.ACTION_INSERT)
                                    .setData(CalendarContract.Events.CONTENT_URI)
                                            //.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, allday)
                                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis() - 28800000)
                                    .putExtra(CalendarContract.Events.TITLE, desc)
                                    .putExtra(CalendarContract.Events.DESCRIPTION, "「" + ori + "」" + "－" + "由 " + from + " 發送\n" + Modify_toString)
                                    .putExtra(CalendarContract.Events.EVENT_LOCATION, place)
                                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
                            intent_cal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent_cal);
                            intent_cal.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            Log.e("calendar", "successful");
                        /*try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/

                        }
                        c.moveToPrevious();
                        kwd = null;
                        add = false;
                        drop = true;
                        GoNatty = false;
                        Eng = false;
                    }
                    Log.e("Line", "end");
                    db.close();
                }
//////////////////////FaceBook////////////////////
                if (MainActivity.App.getBoolean("FB", true)) {
                    Log.e("FB", "Start");
                    GetFBDB.copy();
                    Cursor fc = fdb.rawQuery("Select * from messages", null);
                    Cursor wc = wdb.rawQuery("Select * from keyword order by weight DESC", null);

                    String myname = MainActivity.App.getString("FBname", null);
                    Log.e("name", myname);

                    fc.moveToLast();
                    int fcCount = fc.getCount();
                    for (int j = 0; j < fcCount; j++) {
                        Calendar beginTime = Calendar.getInstance();
                        count = MainActivity.App.getLong("FBcount", 0);
                        String forth = fc.getString(4);
                        if (myname.isEmpty()) {
                            j = fcCount;
                            continue;
                        }
                        if (forth == null) {
                            fc.moveToPrevious();
                            continue;
                        }
                        if (forth.contains(myname)) {
                            fc.moveToPrevious();
                            continue;
                        }
                        Long ktmp = Long.valueOf(fc.getString(5));
                        if (ktmp <= count) {
                            fc.moveToPrevious();
                            continue;
                        }

                        MainActivity.App.edit().putLong("FBcount", Long.valueOf(fc.getString(5))).apply();

                        tmp = fc.getString(3);
                        ori = tmp;

                        if (tmp.getBytes().length == tmp.length()) {
                            Eng = true;
                            Log.e("All Eng", "true");
                        }
                        beginTimetmp1 = 0;
                        place = "";
                        add = false;
                        drop = true;
                        wc.moveToFirst();
                        int wcCount = wc.getCount();
                        for (int i = 0; i < wcCount && drop; i++) {
                            kwd = wc.getString(1);
                            if (kwd.isEmpty()) {
                                continue;
                            }
                            if (tmp != null && tmp.contains(kwd)) {
                                content = wc.getString(2);
                                replace = wc.getString(4);
                                if (content != null) {
                                    beginTimetmp1 += Long.valueOf(content);
                                }
                                if (replace == null) {
                                    tmp = tmp.replace(kwd, "");
                                } else {
                                    tmp = tmp.replace(kwd, replace);
                                }
                                Log.e("kwd", kwd);
                                type = wc.getInt(5);
                                switch (type) {
                                    case 1:
                                        GoNatty = true;
                                        break;
                                    case 3:
                                        place = content;
                                        break;
                                    case 4:
                                        drop = true;
                                        i = wcCount;
                                        break;
                                }
                                if (type != 2) {
                                    add = true;
                                }
                            }
                            wc.moveToNext();
                        }
                        if (GoNatty || Eng) {
                            String out = "";
                            String testt = "";
                            out = Natty(tmp);
                            String[] cutOut = out.split(" ");
                            for (int i = 0; i < 8; i++) {
                                testt += cutOut[i] + '\n';
                            }

                            beginTime.set(Integer.parseInt(cutOut[7]), Integer.parseInt(cutOut[1]), Integer.parseInt(cutOut[2]) + 1, 0, 0);
                            Log.e("Natty", beginTime.toString());
                            Log.e("matchingValue", matchingValue);
                            tmp = tmp.replace(matchingValue, "");
                        }
                        if ((add || !matchingValue.isEmpty()) && drop) {
                            Long datetmp = beginTime.getTimeInMillis() - beginTime.getTimeInMillis() % 86400000;
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

                            //cltmp %= 86400000;
                            //cltmp += beginTimetmp1;

                            wc.moveToLast();
                            String desc = tmp;
                            for (int i = 0; i < wcCount; i++) {
                                kwd = wc.getString(2);
                                type = wc.getInt(4);
                                if (kwd != null && desc.contains(kwd) && (type == 1 || type == 3)) {
                                    desc = desc.replace(kwd, "");
                                }
                                wc.moveToPrevious();
                            }
                            desc = desc.replace("  ", " ");
                            Log.e("建立事項", "");
                            from = fc.getString(4);
                            String[] sendercutOut = from.split("\"");
                            Log.e("senderout", sendercutOut[11]);
                            from = sendercutOut[11];

                            Long Timezone = Long.valueOf(28800000);
                            if (Eng)
                                Timezone = Long.valueOf(0);
                            Log.e(String.valueOf(beginTime.getTimeInMillis()), String.valueOf(System.currentTimeMillis()));
                            if (beginTime.getTimeInMillis() < System.currentTimeMillis() + Timezone) {
                                Log.e("set", "OK");
                                beginTime.setTimeInMillis(System.currentTimeMillis() + Timezone);
                            }

                            Intent intent_cal = new Intent(Intent.ACTION_INSERT)
                                    .setData(CalendarContract.Events.CONTENT_URI)
                                            //.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, allday)
                                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis() - Timezone)
                                    .putExtra(CalendarContract.Events.TITLE, desc)
                                    .putExtra(CalendarContract.Events.DESCRIPTION, ori + "－" + from)
                                    .putExtra(CalendarContract.Events.EVENT_LOCATION, place)
                                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
                            intent_cal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent_cal);
                            intent_cal.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            Log.e("calendar", "successful");
                        /*try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/
                        }

                        fc.moveToPrevious();
                        kwd = null;
                        add = false;
                        drop = true;
                        GoNatty = false;
                        Eng = false;
                    }
                    Log.e("FB", "end");
                    fdb.close();
                    //////////////////////FaceBook//////////////////////
                    wdb.close();
                }
                Log.e("Service", "end");
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void onDestroy(){
        super.onDestroy();
        if(MainActivity.runningOrNot.getBoolean("chk", false)){
            Intent intent = new Intent(MyService.this, MyService.class);
            startService(intent);
        }

        else {
            b = false;
        }
        Log.e("D","onDestroy");
    }
    public String Natty(String tmp) {
        Date curDate = new Date(System.currentTimeMillis());
        String out = "";
        String testt = "";

        List<java.util.Date> dateList = new ArrayList<java.util.Date>();

        Parser parser = new Parser();
        List<DateGroup> groups = parser.parse(tmp);
        for (DateGroup group : groups) {
            List<java.util.Date> dates = group.getDates();
            int line = group.getLine();
            int column = group.getPosition();
            matchingValue = group.getText();
            String syntaxTree = group.getSyntaxTree().toStringTree();
            Map parseMap = group.getParseLocations();
            boolean isRecurreing = group.isRecurring();
            java.util.Date recursUntil = group.getRecursUntil();

                        /* if any Dates are present in current group then add them to dateList */
            if (group.getDates() != null) {
                out += group.getDates().toString();
                Log.e("out", out);
                out = out.replace("[", "");
                out = out.replace("]", "");
                out = out.replace(":", " ");

                out = out.replace("Jan", "0");
                out = out.replace("Feb", "1");
                out = out.replace("Mar", "2");
                out = out.replace("Apr", "3");
                out = out.replace("May", "4");
                out = out.replace("Jun", "5");
                out = out.replace("Jul", "6");
                out = out.replace("Aug", "7");
                out = out.replace("Sep", "8");
                out = out.replace("Oct", "9");
                out = out.replace("Nov", "10");
                out = out.replace("Dec", "11");
            }
        }
        return out;
    }
}

