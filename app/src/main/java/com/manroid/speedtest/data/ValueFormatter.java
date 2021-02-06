package com.manroid.speedtest.data;

import com.manroid.speedtest.model.FileSize;

import java.text.DecimalFormat;

public class ValueFormatter {

    public static String getRoundedValueForView(long value){
        String text;
        DecimalFormat df = new DecimalFormat("###.00");
        if(value >= FileSize.sizeOfGB){
            text = df.format(value/ FileSize.sizeOfGB)+" GB";
        } else if (value >= FileSize.sizeOfMB){
            text = df.format(value/FileSize.sizeOfMB)+" MB";
        } else if (value >= FileSize.sizeOfKB){
            text = df.format(value/FileSize.sizeOfKB)+" KB";
        } else {
            text = value+" bytes";
        }
        return text;
    }

    public static String getFileSize(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static String getRoundedValueForLineChart(float value){
        DecimalFormat df = new DecimalFormat("###");
        return df.format(value/FileSize.sizeOfGB)+" GB";
    }

}
