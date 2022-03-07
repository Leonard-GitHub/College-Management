package com.example.collegemanagement;

import android.app.Application;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

    }
    public static final String formatTimeStamp(long timestamp){
        Calendar cal=Calendar.getInstance(Locale.ENGLISH);
        String date= DateFormat.format("dd/MM/yyyy", cal).toString();
        return date;
    }
}
