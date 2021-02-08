package com.manroid.speedtest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NetworkStatusAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    public final static short SIZE_TAB = 6;
    public final static short BSSID_TAB = 0;
    public final static short SSID_TAB = 1;
    public final static short CAPABILITIES_TAB = 2;
    public final static short FREQUENCY_TAB = 3;
    public final static short LEVEL_TAB = 4;
    public final static short IS_CONNECTED = 5;
    private final List<String[]> listData  = new ArrayList<>();;

    public NetworkStatusAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<String[]> data) {
        listData.clear();
        listData.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(NetworkStatusHolder.LAYOUT, parent, false);
        return new NetworkStatusHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NetworkStatusHolder statusHolder = (NetworkStatusHolder) holder;
        statusHolder.onBind(position, listData);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}