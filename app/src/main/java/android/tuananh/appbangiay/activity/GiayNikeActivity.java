package android.tuananh.appbangiay.activity;

import android.os.Bundle;
import android.os.Handler;
import android.tuananh.appbangiay.R;
import android.tuananh.appbangiay.adapter.GiayNikeAdapter;
import android.tuananh.appbangiay.model.SanPhamMoi;
import android.tuananh.appbangiay.retrofit.ApiBanHang;
import android.tuananh.appbangiay.retrofit.RetrofitClient;
import android.tuananh.appbangiay.utils.Utils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GiayNikeActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    int page = 1;
    int loai;
    GiayNikeAdapter adapterNike;
    List<SanPhamMoi> sanPhamMoiList;
    LinearLayoutManager linearLayoutManager;
    Handler handler = new Handler();
    boolean isLoading = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giay_nike);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        loai = getIntent().getIntExtra("loai", 1);

        Anhxa();
        ActionToolbar();
        getData(page);
        addEventLoad();
    }

    private void addEventLoad() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLoading) {
                    if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == sanPhamMoiList.size() - 1) {
                        isLoading = true;
                        loadMore();
                    }
                }

            }
        });

    }

    private void loadMore() {
        handler.post(() -> {
            //add null
            sanPhamMoiList.add(null);
            adapterNike.notifyItemInserted(sanPhamMoiList.size() - 1);
        });
        handler.postDelayed(() -> {
            //remover null
            sanPhamMoiList.remove(sanPhamMoiList.size() - 1);
            adapterNike.notifyItemRemoved(sanPhamMoiList.size());
            page = page + 1;
            getData(page);
            adapterNike.notifyDataSetChanged();
            isLoading = false;

        }, 2000);
    }

    private void getData(int page) {
        compositeDisposable.add(apiBanHang.getSanPham(page, loai)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if (sanPhamMoiModel.isSuccess()) {
                                if (adapterNike == null) {
                                    sanPhamMoiList = sanPhamMoiModel.getResult();
                                    adapterNike = new GiayNikeAdapter(getApplicationContext(), sanPhamMoiList);
                                    recyclerView.setAdapter(adapterNike);
                                } else {
                                    int vitri = sanPhamMoiList.size() - 1;
                                    int soluongadd = sanPhamMoiModel.getResult().size();
                                    for (int i = 0; i < soluongadd; i++) {
                                        sanPhamMoiList.add(sanPhamMoiModel.getResult().get(i));
                                    }
                                    adapterNike.notifyItemRangeInserted(vitri, soluongadd);
                                }


                            } else {
                                Toast.makeText(getApplicationContext(), "Hết dữ liệu rồi!", Toast.LENGTH_LONG).show();
                                isLoading = true;
                            }
                        },
                        throwable -> Toast.makeText(getApplicationContext(), "Không kết nối server", Toast.LENGTH_LONG).show()
                ));
    }

    private void ActionToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    private void Anhxa() {
        toolbar = findViewById(R.id.toobar);
        recyclerView = findViewById(R.id.recycleview_nike);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        sanPhamMoiList = new ArrayList<>();

    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}