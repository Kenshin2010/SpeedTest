package com.manroid.speedtest.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manroid.speedtest.R;
import com.manroid.speedtest.wifianalyzer.Utility;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.manroid.speedtest.adapter.NetworkStatusAdapter.BSSID_TAB;
import static com.manroid.speedtest.adapter.NetworkStatusAdapter.CAPABILITIES_TAB;
import static com.manroid.speedtest.adapter.NetworkStatusAdapter.FREQUENCY_TAB;
import static com.manroid.speedtest.adapter.NetworkStatusAdapter.IS_CONNECTED;
import static com.manroid.speedtest.adapter.NetworkStatusAdapter.LEVEL_TAB;
import static com.manroid.speedtest.adapter.NetworkStatusAdapter.SSID_TAB;

public class NetworkStatusHolder extends RecyclerView.ViewHolder {

    public static final int LAYOUT = R.layout.network_status_listview;

    @BindView(R.id.ssid__bssid_textView) TextView ssidView;
    @BindView(R.id.capabilities_textView) TextView capabilitiesView;
    @BindView(R.id.frequency_textView)  TextView frequencyView;
    @BindView(R.id.strength_percent_textView)  TextView levelView;
    @BindView(R.id.channel_textView) TextView channelView;
    @BindView(R.id.strength_percent_progressbar_textView)   TextView strengthProgressBarView;
    @BindView(R.id.ns_wifi_strength_imageview)  ImageView imageView;
    @BindView(R.id.ns_quality_progressbar)  ProgressBar progressBar;
    
    public NetworkStatusHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void onBind(int pos, List<String[]> values) {
        setValuesToListView(pos, values);
    }

    @SuppressLint("StringFormatMatches")
    private void setValuesToListView(int position, List<String[]> values) {
        for (int i = 0; i < values.size(); i++) {
            String[] tab = values.get(position);

            ssidView.setText(String.format(
                    ssidView.getContext().getString(R.string.two_strings_textView), tab[SSID_TAB], tab[BSSID_TAB]
            ));
            channelView.setText(Integer.toString(
                    Utility.convertFrequencyToChannel(Integer.valueOf(tab[FREQUENCY_TAB]))
            ));
            levelView.setText(tab[LEVEL_TAB]);
            frequencyView.setText(tab[FREQUENCY_TAB]);
            capabilitiesView.setText(String.format(
                    capabilitiesView.getContext().getResources().getString(R.string.ns_capabilities_textView), Utility.getEncryptionFromCapabilities(tab[CAPABILITIES_TAB])
            ));

            int quality = Utility.convertRssiToQuality(Integer.valueOf(tab[LEVEL_TAB]));

            progressBar.setMax(100);
            progressBar.setProgress(quality);
            strengthProgressBarView.setText(String.format(
                    strengthProgressBarView.getContext().getResources().getString(R.string.percent_textView), quality
            ));

            if (tab[IS_CONNECTED].equals("1")) {
                ssidView.setTextColor(ssidView.getContext().getResources().getColor(R.color.dark_yellow));
            }

            if (Utility.convertQualityToStepsQuality(quality, 5) == 1) {
                imageView.setImageResource(R.mipmap.wireless_0);
            } else if (Utility.convertQualityToStepsQuality(quality, 5) == 2) {
                imageView.setImageResource(R.mipmap.wireless_1);
            } else if (Utility.convertQualityToStepsQuality(quality, 5) == 3) {
                imageView.setImageResource(R.mipmap.wireless_2);
            } else if (Utility.convertQualityToStepsQuality(quality, 5) == 4) {
                imageView.setImageResource(R.mipmap.wireless_3);
            } else if (Utility.convertQualityToStepsQuality(quality, 5) == 5) {
                imageView.setImageResource(R.mipmap.wireless_4);
            }
        }
    }
}