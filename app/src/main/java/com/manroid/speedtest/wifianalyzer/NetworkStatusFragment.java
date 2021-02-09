package com.manroid.speedtest.wifianalyzer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.manroid.speedtest.R;
import com.manroid.speedtest.adapter.NetworkStatusAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.manroid.speedtest.adapter.NetworkStatusAdapter.BSSID_TAB;
import static com.manroid.speedtest.adapter.NetworkStatusAdapter.CAPABILITIES_TAB;
import static com.manroid.speedtest.adapter.NetworkStatusAdapter.FREQUENCY_TAB;
import static com.manroid.speedtest.adapter.NetworkStatusAdapter.IS_CONNECTED;
import static com.manroid.speedtest.adapter.NetworkStatusAdapter.LEVEL_TAB;
import static com.manroid.speedtest.adapter.NetworkStatusAdapter.SSID_TAB;

public class NetworkStatusFragment extends Fragment {
    @SuppressWarnings("unused")
    public static final String LOG_TAG = NetworkStatusFragment.class.getSimpleName();

    private WifiScanReceiver mWifiReceiver;
    private WifiManager mWifiManager;

    private int mRefreshRateInSec = 2;
    private int mRrefreshRate = 1000 * mRefreshRateInSec;
    private Timer timer;
    private NetworkStatusAdapter adapter;
    private RecyclerView rvNetworkStatus;
    private TextView intervalView;
    private TextView wirelessNetworksView;
    private TextView connectedInfoView;
    private TextView ipView;
    private TextView speedView;
    private ImageView refreshRateButton;
    private ImageView refreshButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mWifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiReceiver = new WifiScanReceiver();
        super.onCreate(savedInstanceState);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findId(view);
        Utility.enableWifi(mWifiManager);
        initView();
        mWifiManager.startScan();
    }

    private void findId(View view) {
        rvNetworkStatus = view.findViewById(R.id.rv_network_status);
        intervalView = view.findViewById(R.id.ns_interval_textview);
        wirelessNetworksView = view.findViewById(R.id.ns_number_of_available_network_textView);
        connectedInfoView = view.findViewById(R.id.ns_connected_textview);
        ipView = view.findViewById(R.id.ns_ip_textView);
        speedView = view.findViewById(R.id.ns_speed_textView);
        refreshRateButton = view.findViewById(R.id.scanning_time_button);
        refreshButton = view.findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(v -> updateView());
        refreshRateButton.setOnClickListener(v -> createRefreshIntervalDialog());
    }

    private void initView() {
        adapter = new NetworkStatusAdapter(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvNetworkStatus.setLayoutManager(linearLayoutManager);
        rvNetworkStatus.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvNetworkStatus.getContext(),
                linearLayoutManager.getOrientation());
        rvNetworkStatus.addItemDecoration(dividerItemDecoration);
        refreshRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRefreshIntervalDialog();
            }
        });
    }

    @Override
    public void onPause() {
        setParametersReceiverBefore();
        super.onPause();
    }

    @Override
    public void onResume() {
        setParametersReceiverAfter();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.network_status_tab, container, false);
        return rootView;
    }

    //unregister receiver & close timer
    private void setParametersReceiverBefore() {
        getActivity().unregisterReceiver(mWifiReceiver);

        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    //register receiver & schedule timer
    private void setParametersReceiverAfter() {
        getActivity().registerReceiver(mWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        //schedule timer
        if (mRrefreshRate > 0) {
            timer = new Timer();
            TimerTask updateTask = new TimerTask() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateView();
                        }
                    });
                }
            };
            timer.scheduleAtFixedRate(updateTask, 0, mRrefreshRate);
        }
    }

    //update View - Networks & Bar
    private void updateView() {
        Utility.enableWifi(mWifiManager);
        mWifiManager.startScan();

        List<ScanResult> wifiScanList = mWifiManager.getScanResults();
        Log.d("debug data wifi", "update: " + new Gson().toJson(wifiScanList));
        if (wifiScanList == null) {
            return;
        }

        updateNetworkStatus(wifiScanList);
        updateInfoBar(wifiScanList.size());
    }

    //TODO create progress dialog during update
    //update network status for each signal
    private void updateNetworkStatus(List<ScanResult> wifiScanList) {
        List<String[]> list = new ArrayList<>();

        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();

        String bssid = wifiInfo.getBSSID();
        if (bssid == null)
            bssid = "";

        for (int i = 0; i < wifiScanList.size(); i++) {
            String[] wifiDetails = new String[NetworkStatusAdapter.SIZE_TAB];

            wifiDetails[BSSID_TAB] = wifiScanList.get(i).BSSID;
            wifiDetails[SSID_TAB] = wifiScanList.get(i).SSID;
            wifiDetails[CAPABILITIES_TAB] = wifiScanList.get(i).capabilities;
            wifiDetails[FREQUENCY_TAB] = String.valueOf(wifiScanList.get(i).frequency);
            wifiDetails[LEVEL_TAB] = String.valueOf(wifiScanList.get(i).level);

            if (bssid.equals(wifiScanList.get(i).BSSID)) {
                wifiDetails[IS_CONNECTED] = "1";
            } else {
                wifiDetails[IS_CONNECTED] = "0";
            }

            list.add(wifiDetails);
        }

        adapter.setData(list);
    }

    //update view in simple info bar
    @SuppressLint("StringFormatMatches") @SuppressWarnings("deprecation")
    private void updateInfoBar(int size) {
        Resources resources = getResources();
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();

        intervalView.setText(String.format(resources.getString(R.string.ns_interval), mRefreshRateInSec));
        wirelessNetworksView.setText(String.format(resources.getString(R.string.ns_number_of_available_network), size));

        if (wifiInfo.getBSSID() == null)
            return;

        connectedInfoView.setText(String.format(resources.getString(R.string.connected_bar), wifiInfo.getSSID()));
        ipView.setText(String.format(resources.getString(R.string.ns_ip), Formatter.formatIpAddress(wifiInfo.getIpAddress())));
        speedView.setText(String.format(resources.getString(R.string.ns_speed), wifiInfo.getLinkSpeed()));
    }

    //create dialog to choice refresh interval
    private void createRefreshIntervalDialog() {
        final String[] stringsRefreshRateValues = getResources().getStringArray(R.array.pref_refresh_rate_values);
        final String[] stringsRefreshRateOptions = getResources().getStringArray(R.array.pref_refresh_rate_options);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.ns_title_dialog_refresh_rate);

        alertDialog.setSingleChoiceItems(stringsRefreshRateOptions, 1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mRefreshRateInSec = Integer.parseInt(stringsRefreshRateValues[which]);
                        mRrefreshRate = 1000 * mRefreshRateInSec;

                        NetworkStatusFragment.this.setParametersReceiverBefore();
                        NetworkStatusFragment.this.setParametersReceiverAfter();
                        NetworkStatusFragment.this.updateView();

                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    //update if AS FAST AS POSSIBLE
    private class WifiScanReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            if (mRrefreshRate == 0) {
                updateView();
            }
        }
    }
}
