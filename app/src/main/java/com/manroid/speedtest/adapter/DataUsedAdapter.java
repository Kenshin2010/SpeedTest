package com.manroid.speedtest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manroid.speedtest.model.DataUsed;

import java.util.ArrayList;
import java.util.List;

public class DataUsedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DataUsed> listData = new ArrayList<>();
    private Context context;

    public DataUsedAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<DataUsed> data) {
        listData.clear();
        listData.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(DataUsedHolder.LAYOUT, parent, false);
        return new DataUsedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DataUsedHolder dataUsedHolder = (DataUsedHolder) holder;
        dataUsedHolder.onBind(position, listData);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

}
