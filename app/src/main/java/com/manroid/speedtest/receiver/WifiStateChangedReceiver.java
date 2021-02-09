package com.manroid.speedtest.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

public class WifiStateChangedReceiver extends BroadcastReceiver {

    private WifiListener listener;

    public void setListener(WifiListener listener) {
        this.listener = listener;
    }

    @Override public void onReceive(Context context, Intent intent) {
        int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
        switch (extraWifiState) {
            case WifiManager.WIFI_STATE_DISABLED:
                listener.actionWifi(false);
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                listener.actionWifi(true);
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                break;
        }
    }

    public interface WifiListener {
        public void actionWifi(boolean isEnable);
    }
}
