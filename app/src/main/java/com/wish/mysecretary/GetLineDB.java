package com.wish.mysecretary;

import android.util.Log;

import java.io.OutputStreamWriter;

public class GetLineDB {
    public static void copy(){
        Runtime ex= Runtime.getRuntime();
        String su="su";
        String line="/data/data/jp.naver.line.android/databases/naver_line";
        String ATC="/data/data/com.wish.mysecretary/databases/";
        try{
            Process rumsum=ex.exec(su);
            int exitval=0;
            final OutputStreamWriter out=new OutputStreamWriter(rumsum.getOutputStream());

            out.write("busybox chmod o+rw "+line+"\n");
            out.flush();

            //out.write("busybox mkdir "+ATC+"\n");
            //out.flush();

            out.write("busybox cp "+line+" "+ATC+"line\n");
            out.flush();

            out.write("busybox chmod 777 "+ATC+"line\n");
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
