package com.wish.mysecretary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by user on 2015/3/31.
 */
public class IncomingSms extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Get","successfully");

        Bundle bundle = intent.getExtras();
        Object[] myObj = (Object[]) bundle.get("pdus");
        SmsMessage smsMsg[] = new SmsMessage[myObj.length];
        System.out.println(myObj.length);
        String msg = "";

        for (int i = 0; i < myObj.length; i++) {
                    smsMsg[i] = SmsMessage.createFromPdu((byte[]) myObj[i]);
                    msg += smsMsg[i].getDisplayMessageBody() + smsMsg[i].getOriginatingAddress();
        }
        MainActivity.nomatterwhat=false;
        MainActivity.analaysis(msg);
        Log.e("MSG",msg);

    }

}