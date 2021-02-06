package com.manroid.speedtest.data;

import android.icu.util.Calendar;
import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.N)
public class DurationManager {

    public static long getFirstMomentThisMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, 0, 0, 0);
        return cal.getTimeInMillis();
    }

    public static long getToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0, 0, 0);
        return cal.getTimeInMillis();
    }

    public static long getFirstMomentPreviousMonth(int monthOffset) {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, 0, 0, 0);
        cal.add(Calendar.MONTH, 0-monthOffset);
//        Log.i("cal first", cal.getTime().toString());
        return cal.getTimeInMillis();
    }

    public static long getLastMomentPreviousMonth(int monthOffset) {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, 0, 0, 0);
        cal.add(Calendar.MONTH, 1-monthOffset);
        cal.add(Calendar.SECOND, -1);
//        Log.i("cal last", cal.getTime().toString());
        return cal.getTimeInMillis();
    }


    public static final long getCurrentTime(){
        return System.currentTimeMillis();
    }
}
