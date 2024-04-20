package android.tuananh.appbangiay.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.tuananh.appbangiay.R;
import android.tuananh.appbangiay.retrofit.ApiBanHang;
import android.tuananh.appbangiay.retrofit.RetrofitClient;
import android.tuananh.appbangiay.utils.Utils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ThongKeActivity extends AppCompatActivity {

    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Toolbar toolbar;
    private PieChart pieChart;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_ke);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        initView();
        actionToolbar();
        getDataChart();
        settingBarChart();
    }

    private void settingBarChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawValueAboveBar(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setAxisMinimum(1);
        xAxis.setAxisMaximum(12);
        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setAxisMinimum(0);
        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0);
    }

    private void getDataChart() {
        barChart.setVisibility(View.GONE);
        pieChart.setVisibility(View.VISIBLE);
        List<PieEntry> list = new ArrayList<>();
        compositeDisposable.add(apiBanHang.getThongKe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        thongKeModel -> {
                            if (thongKeModel.isSuccess()) {
                                thongKeModel.getResult().forEach(thongKe -> list.add(new PieEntry(thongKe.getTong(), thongKe.getTensp())));

                                PieDataSet pieDataSet = new PieDataSet(list, "Thống kê");
                                PieData pieData = new PieData();
                                pieData.addDataSet(pieDataSet);
                                pieData.setValueTextSize(12f);
                                pieData.setValueFormatter(new PercentFormatter(pieChart));
                                pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                                pieChart.setData(pieData);
                                pieChart.animateXY(2000, 2000);
                                pieChart.setUsePercentValues(true);
                                pieChart.getDescription().setEnabled(false);
                                pieChart.invalidate();
                            }
                        },
                        throwable -> Toast.makeText(getApplicationContext(), "Không kết nối server", Toast.LENGTH_LONG).show()
                ));
    }

    private void initView() {
        toolbar = findViewById(R.id.toobar);
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_thongke, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.tkThang) {
            getTkThang();
            return true;
        }
        if (id == R.id.tkHang) {
            getDataChart();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getTkThang() {
        barChart.setVisibility(View.VISIBLE);
        pieChart.setVisibility(View.GONE);
        compositeDisposable.add(apiBanHang.getThongKeThang()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        thongKeModel -> {
                            if (thongKeModel.isSuccess()) {
                                List<BarEntry> listData = new ArrayList<>();
                                thongKeModel.getResult().forEach(thongKe ->
                                        listData.add(new BarEntry(
                                                Integer.parseInt(thongKe.getThang()),
                                                Float.parseFloat(thongKe.getTongtienthang())
                                        )));
                                BarDataSet barDataSet = new BarDataSet(listData, "Thống kê");
                                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                                barDataSet.setValueTextSize(14f);
                                barDataSet.setValueTextColor(Color.RED);

                                BarData data = new BarData(barDataSet);
                                barChart.animateXY(2000, 2000);
                                barChart.setData(data);
                                barChart.invalidate();
                            }
                        },
                        throwable -> Toast.makeText(getApplicationContext(), "Không kết nối server", Toast.LENGTH_LONG).show()
                ));
    }

    private void actionToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}