package com.wish.mysecretary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

/**
 * Created by petingo on 2017/5/4.
 */

public class myDialog {
    public AlertDialog.Builder checkRootDialog(Context context){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog
                .setTitle("需要root權限")
                .setMessage("開啟此項功能需要root權限，請確認手機已root且安裝root管理軟體如SuperSU，否則軟體將無法執行閃退。\n\n若發生不正常閃退請至 設定／應用程式／選取本應用程式／清除資料 再開啟程式重新設定")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e("onClick","successful");
                    }
                });
        return dialog;
    }
}
