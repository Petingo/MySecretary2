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

//            List<java.util.Date> dates = group.getDates();
//            int line = group.getLine();
//            int column = group.getPosition();

//            String syntaxTree = group.getSyntaxTree().toStringTree();
//            Map parseMap = group.getParseLocations();
//            boolean isRecurreing = group.isRecurring();
//            java.util.Date recursUntil = group.getRecursUntil();


class Natty {
    private String resource;
    private String output;
    private String matchingValue;

    Natty(String resource) {
        this.resource = resource;
        getResult();
    }

    String getOutput() {
        return this.output;
    }

    String getMatchingValue() {
        return this.matchingValue;
    }

    private void getResult() {
        String tmp = this.resource;
        String out = "";

        Parser parser = new Parser();
        List<DateGroup> groups = parser.parse(tmp);

        DateGroup group = groups.get(0);

        //for (DateGroup group : groups) {
        //TODO more than one result
        this.matchingValue = group.getText();
                        /* if any Dates are present in current group then add them to dateList */
        if (group.getDates() != null) {
            out += group.getDates().toString();
            Log.e("out", out);
            out = out.replace("[", "");
            out = out.replace("]", "");
            out = out.replace(":", " ");

            String[] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

            for (int i = 0; i < 12; i++) {
                out = out.replace(month[i], String.valueOf(i));
            }
        }

        //}
        this.output = out;
    }
}
