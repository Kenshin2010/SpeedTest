package com.manroid.speedtest.activity;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.manroid.speedtest.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    final int READ_PHONE_STATE_REQUEST = 11;
    final int ACCESS_LOCATION_REQUEST = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler();
    }

    public void test(View view) {
        startTestWifi();
    }

    private int mInterval = 5000;
    private Handler mHandler;

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTestWifi();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                checkWifi();
            } finally {
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    private void startTestWifi() {
        mStatusChecker.run();
    }

    private void stopTestWifi() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    private void checkWifi(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if(wifiInfo != null) {
                int level = wifiInfo.getRssi();
                if (level <= 0 && level >= -50) {
                    Toast.makeText(this, "Best signal: " + level , Toast.LENGTH_SHORT).show();
                } else if (level < -50 && level >= -70) {
                    Toast.makeText(this, "Good signal: " + level, Toast.LENGTH_SHORT).show();
                } else if (level < -70 && level >= -80) {
                    Toast.makeText(this, "Low signal: " + level, Toast.LENGTH_SHORT).show();
                } else if (level < -80 && level >= -100) {
                    Toast.makeText(this, "Very weak signa: " + level, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No signal", Toast.LENGTH_SHORT).show();
                }
            }
        }else {
            Toast.makeText(this, "No signal", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopTest(View view) {
        stopTestWifi();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void checkDataUsed(View view) {
        if (!hasPermissionToReadNetworkHistory()) {
            return;
        }
        if (!hasPermissionToReadPhoneStats()) {
            requestPhoneStateStats();
            return;
        }
        startActivity(new Intent(this, AppsUsedDataActivity.class));
//        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
//        StringBuilder sb = new StringBuilder();
//        sb.append("package:");
//        sb.append(getPackageName());
//        intent.setData(Uri.parse(sb.toString()));
//        startActivity(intent);
//        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void requestPermissions() {
        if (!hasPermissionToReadNetworkHistory()) {
            return;
        }
        if (!hasPermissionToReadPhoneStats()) {
            requestPhoneStateStats();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean hasPermissions() {
        return hasPermissionToReadNetworkHistory() && hasPermissionToReadPhoneStats();
    }

    private boolean hasPermissionToReadPhoneStats() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED) {
            return false;
        } else {
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean hasPermissionToReadNetworkHistory() {
        final AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        if (mode == AppOpsManager.MODE_ALLOWED) {
            return true;
        }
        appOps.startWatchingMode(AppOpsManager.OPSTR_GET_USAGE_STATS,
                getApplicationContext().getPackageName(),
                new AppOpsManager.OnOpChangedListener() {
                    @Override
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    public void onOpChanged(String op, String packageName) {
                        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                                android.os.Process.myUid(), getPackageName());
                        if (mode != AppOpsManager.MODE_ALLOWED) {
                            return;
                        }
                        appOps.stopWatchingMode(this);
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        if (getIntent().getExtras() != null) {
                            intent.putExtras(getIntent().getExtras());
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    }
                });
        requestReadNetworkHistoryAccess();
        return false;
    }

    private void requestReadNetworkHistoryAccess() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
    }

    private void requestPhoneStateStats() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_REQUEST);
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_PHONE_STATE_REQUEST
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startActivity(new Intent(this, AppsUsedDataActivity.class));
        }else if (requestCode == ACCESS_LOCATION_REQUEST
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startActivity(new Intent(this, WifiAnalyzerActivity.class));
        }
    }

    public void checkWifiAnalyzer(View view) {
        if (!hasPermissionToAccessLocation()) {
            requestAccessLocation();
            return;
        }
        startActivity(new Intent(this, WifiAnalyzerActivity.class));
    }

    private boolean hasPermissionToAccessLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            return false;
        } else {
            return true;
        }
    }

    private void requestAccessLocation() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST);
    }
}