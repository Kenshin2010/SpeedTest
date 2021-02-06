package com.manroid.speedtest.model;

import android.annotation.SuppressLint;
import android.content.Context;

import com.manroid.speedtest.data.NetworkTrafficCollector;
import com.manroid.speedtest.data.ValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@SuppressLint("NewApi")
public class NetworkTrafficDate {
    private String month;
    private long mobile;
    private long wifi;
//    private long tethering;

    private Context context;
    private NetworkTrafficCollector ntc;
    private List<DataUsed> data = new ArrayList<>();
    public static final int A_WEEK = 7;
    public static final int A_MONTH = 30;
    private long startTime;
    private long endTime;
    private long totalDataWeek;

    public NetworkTrafficDate(Context context, long startTime, long endTime, int monthOffset) {
        this.context = context;
        this.startTime = startTime;
        this.endTime = endTime;

        ntc = new NetworkTrafficCollector(context);
//        month = setMonth(monthOffset);

        //mobile usage data
        mobile = ntc.getMobileTrafficData(startTime, endTime);

        //wifi  usage data
        wifi = ntc.getWiFiTrafficData(startTime, endTime);

        //tethering usage data
//        tethering = ntc.getTetheringTrafficData(startTime, endTime);
    }

    public NetworkTrafficDate(Context context) {
        ntc = new NetworkTrafficCollector(context);
        this.context = context;
    }

    private String setMonth(int monthOffset) {
        DateFormat df = new SimpleDateFormat("MMM");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 0 - monthOffset);
        return df.format(cal.getTime());
    }

    public String getMonth() {
        return this.month;
    }

    public long getMobileTraffic() {
        return this.mobile;
    }

    public long getWiFiTraffic() {
        return this.wifi;
    }

//    public long getTetheringTraffic(){
//        return this.tethering;
//    }

    public long getTotalTraffic() {
        return this.mobile + this.wifi/*+this.tethering*/;
    }

    public List<DataUsed> getNetWorkToDay() {
        data.clear();
        DataUsed dataUsed = new DataUsed();
        dataUsed.setDataMobile(ValueFormatter.getFileSize(getMobileTraffic()));
        dataUsed.setDataWifi(ValueFormatter.getFileSize(getWiFiTraffic()));
        dataUsed.setDate("To day");
        dataUsed.setTotal(ValueFormatter.getFileSize(getTotalTraffic()));
        data.add(dataUsed);
        return data;
    }

    public List<DataUsed> getNetWorkByDay(int day) {
        data.clear();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
        for (int i = 0; i < day; i++) {
            DataUsed dataUsed = new DataUsed();
            Calendar startCalendar = Calendar.getInstance();
            Calendar endCalendar = Calendar.getInstance();
            startCalendar.set(Calendar.HOUR_OF_DAY, 0);
            startCalendar.set(Calendar.MINUTE, 0);
            startCalendar.set(Calendar.SECOND, 0);
            endCalendar.set(Calendar.HOUR_OF_DAY, 24);
            endCalendar.set(Calendar.MINUTE, 0);
            endCalendar.set(Calendar.SECOND, 0);
            startCalendar.add(Calendar.DAY_OF_YEAR, -i);
            endCalendar.add(Calendar.DAY_OF_YEAR, -i);

            Date newDate = startCalendar.getTime();
            String date = dateFormat.format(newDate);
            if (i == 0) {
                dataUsed.setDate("To day");
            } else {
                dataUsed.setDate(date);
            }
            long dataMobile = ntc.getMobileTrafficData(startCalendar.getTimeInMillis(), endCalendar.getTimeInMillis());
            long dataWifi = ntc.getWiFiTrafficData(startCalendar.getTimeInMillis(), endCalendar.getTimeInMillis());
            long total = dataMobile + dataWifi;
            totalDataWeek += total;
            dataUsed.setDataMobile(ValueFormatter.getFileSize(dataMobile));
            dataUsed.setDataWifi(ValueFormatter.getFileSize(dataWifi));
            dataUsed.setTotal(ValueFormatter.getFileSize(total));
            data.add(dataUsed);
        }
        return data;
    }

    public long getTotalDataWeek() {
        return totalDataWeek;
    }
}
