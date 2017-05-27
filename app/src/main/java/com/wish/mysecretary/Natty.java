package com.wish.mysecretary;

import android.util.Log;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Petingo on 2017/5/25.
 */

class Natty {
    static String getResult(String tmp, String matchingValue) {
        Date curDate = new Date(System.currentTimeMillis());
        String out = "";
        String testt = "";

        List<java.util.Date> dateList = new ArrayList<java.util.Date>();

        Parser parser = new Parser();
        List<DateGroup> groups = parser.parse(tmp);
        for (DateGroup group : groups) {
//            List<java.util.Date> dates = group.getDates();
//            int line = group.getLine();
//            int column = group.getPosition();
            matchingValue = group.getText();
//            String syntaxTree = group.getSyntaxTree().toStringTree();
//            Map parseMap = group.getParseLocations();
//            boolean isRecurreing = group.isRecurring();
//            java.util.Date recursUntil = group.getRecursUntil();

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
