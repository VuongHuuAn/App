package android.tuananh.appbangiay.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.tuananh.appbangiay.R;
import android.tuananh.appbangiay.adapter.GiayNikeAdapter;
import android.tuananh.appbangiay.model.SanPhamMoi;
import android.tuananh.appbangiay.retrofit.ApiBanHang;
import android.tuananh.appbangiay.retrofit.RetrofitClient;
import android.tuananh.appbangiay.utils.Utils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    EditText editsearch;
    GiayNikeAdapter adapterNike;
    List<SanPhamMoi> sanPhamMoiList;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        ActionToolbar();
    }

    private void initView() {
        sanPhamMoiList = new ArrayList<>();
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

        editsearch = findViewById(R.id.edtsearch);
        toolbar = findViewById(R.id.toobar);
        recyclerView = findViewById(R.id.recycleview_search);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        editsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    sanPhamMoiList.clear();
                    adapterNike = new GiayNikeAdapter(getApplicationContext(), sanPhamMoiList);
                    recyclerView.setAdapter(adapterNike);
                } else {
                    getDataSearch(charSequence.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
    }

    private void getDataSearch(String s) {
        sanPhamMoiList.clear();
        compositeDisposable.add(apiBanHang.search(s)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if (sanPhamMoiModel.isSuccess()) {
                                sanPhamMoiList = sanPhamMoiModel.getResult();
                                adapterNike = new GiayNikeAdapter(getApplicationContext(), sanPhamMoiList);
                                recyclerView.setAdapter(adapterNike);
                            } else {
                                Toast.makeText(getApplicationContext(), sanPhamMoiModel.getMessage(), Toast.LENGTH_SHORT).show();
                                sanPhamMoiList.clear();
                                adapterNike.notifyDataSetChanged();
                            }
                        },
                        throwable -> Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }

    private void ActionToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}