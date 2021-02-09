package com.manroid.speedtest.activity;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manroid.speedtest.R;
import com.manroid.speedtest.adapter.DataUsedAdapter;
import com.manroid.speedtest.data.DurationManager;
import com.manroid.speedtest.data.ValueFormatter;
import com.manroid.speedtest.model.NetworkTrafficDate;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@SuppressLint("NewApi")
public class AppsUsedDataActivity extends AppCompatActivity {

    @BindView(R.id.rv_data_used) RecyclerView rvDataUsed;
    @BindView(R.id.tv_total_data) TextView tvTotalData;
    private DataUsedAdapter adapter;
    private NetworkTrafficDate trafficDate;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_used_data);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initData() {
        selectTodayAppUsedData();
    }

    private void initView() {
        adapter = new DataUsedAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvDataUsed.setLayoutManager(linearLayoutManager);
        rvDataUsed.setAdapter(adapter);
    }


    @SuppressLint("NewApi")
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!hasPermissionToReadNetworkHistory()) {
            return false;
        }
        int id = item.getItemId();
        switch (id) {
            case R.id.select_month:
                selectMonthAppUsedData();
                break;
            case R.id.select_week:
                selectWeekAppUsedData();
                break;
            case R.id.select_today:
                selectTodayAppUsedData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectMonthAppUsedData(){
        Toast.makeText(this, "Loading ....", Toast.LENGTH_SHORT).show();
        Disposable disposable = Observable.fromCallable(() -> {
            try {
                return trafficDate = new NetworkTrafficDate(this);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe((result) -> {
            if (result != null){
                adapter.setData(trafficDate.getNetWorkByDay(NetworkTrafficDate.A_MONTH));
                tvTotalData.setText(ValueFormatter.getFileSize(trafficDate.getTotalDataWeek()));
                Toast.makeText(this, "Loaded", Toast.LENGTH_SHORT).show();
            }
        });
        compositeDisposable.add(disposable);
    }

    private void selectWeekAppUsedData(){
        Toast.makeText(this, "Loading ....", Toast.LENGTH_SHORT).show();
        Disposable disposable = Observable.fromCallable(() -> {
            try {
                return trafficDate = new NetworkTrafficDate(this);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe((result) -> {
            if (result != null){
                adapter.setData(trafficDate.getNetWorkByDay(NetworkTrafficDate.A_WEEK));
                tvTotalData.setText(ValueFormatter.getFileSize(trafficDate.getTotalDataWeek()));
                Toast.makeText(this, "Loaded", Toast.LENGTH_SHORT).show();
            }
        });
        compositeDisposable.add(disposable);
    }

    private void selectTodayAppUsedData(){
        Toast.makeText(this, "Loading ....", Toast.LENGTH_SHORT).show();
        Disposable disposable = Observable.fromCallable(() -> {
            try {
                return trafficDate = new NetworkTrafficDate(this, DurationManager.getToday(), DurationManager.getCurrentTime(), 0);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe((result) -> {
            if (result != null){
                adapter.setData(trafficDate.getNetWorkToDay());
                tvTotalData.setText(ValueFormatter.getFileSize(trafficDate.getTotalTraffic()));
                Toast.makeText(this, "Loaded", Toast.LENGTH_SHORT).show();
            }
        });
        compositeDisposable.add(disposable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_apps_data_used, menu);
        // return true so that the menu pop up is opened
        return true;
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
                        Intent intent = new Intent(AppsUsedDataActivity.this, AppsUsedDataActivity.class);
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
}