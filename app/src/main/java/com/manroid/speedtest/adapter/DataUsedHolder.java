package com.manroid.speedtest.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manroid.speedtest.model.DataUsed;
import com.manroid.speedtest.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DataUsedHolder extends RecyclerView.ViewHolder {

    public static final int LAYOUT = R.layout.item_data_used;

    @BindView(R.id.tv_mobile) TextView tvMobile;
    @BindView(R.id.tv_wifi) TextView tvWifi;
    @BindView(R.id.tv_total) TextView tvTotal;
    @BindView(R.id.tv_day) TextView tvDay;

    public DataUsedHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void onBind(int pos, List<DataUsed> data) {
        DataUsed dataUsed = data.get(pos);
        tvDay.setText(dataUsed.getDate());
        tvMobile.setText(dataUsed.getDataMobile());
        tvWifi.setText(dataUsed.getDataWifi());
        tvTotal.setText(dataUsed.getTotal());
    }
}
