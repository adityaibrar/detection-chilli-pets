package com.example.chilipestdetection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.chilipestdetection.R;
import com.example.chilipestdetection.adapters.HamaAdapter;
import com.example.chilipestdetection.contracts.DrawerContract;
import com.example.chilipestdetection.contracts.HamaView;
import com.example.chilipestdetection.models.HamaRequest;
import com.example.chilipestdetection.models.HamaResponse;
import com.example.chilipestdetection.presenters.DrawerPresenter;
import com.example.chilipestdetection.presenters.HamaPresenter;
import com.example.chilipestdetection.utils.SharedPreferencesManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class SecondaryActivity extends DrawerActivity implements HamaView {

    private RecyclerView recyclerView;
    private HamaAdapter adapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fabAdd;

    private HamaPresenter presenter;
    private List<HamaRequest> hamaList = new ArrayList<>();

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_secondary;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        View contentView = getLayoutInflater().inflate(R.layout.activity_secondary, findViewById(R.id.container), false);

        initViews();
        initPresenter();
        setupRecyclerView();
        setupListeners();
        setUpToolbar();

        presenter.loadHamaData();
    }

    private  void setUpToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu); // Pastikan Anda memiliki icon menu
            getSupportActionBar().setTitle("Data Hama");
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        fabAdd = findViewById(R.id.fabAdd);
    }

    private void initPresenter() {
        presenter = new HamaPresenter((HamaView) this);
        DrawerPresenter drawerPresenter = new DrawerPresenter(this, this);

        drawerPresenter.checkUserType();
    }

    private void setupRecyclerView() {
        adapter = new HamaAdapter(this, hamaList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnHamaActionListener(new HamaAdapter.OnHamaActionListener() {
            @Override
            public void onEditHama(String kodeHama, String namaHama, String type) {
                presenter.updateHama(kodeHama, namaHama, type);
            }

            @Override
            public void onDeleteHama(String kodeHama) {
                presenter.deleteHama(kodeHama);
            }
        });
    }

    private void setupListeners() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            presenter.loadHamaData();
        });

        fabAdd.setOnClickListener(v -> showAddDialog());
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_hama, null);

        EditText etNamaHama = dialogView.findViewById(R.id.etNamaHama);
        EditText etType = dialogView.findViewById(R.id.etType);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String namaHama = etNamaHama.getText().toString().trim();
            String type = etType.getText().toString().trim();

            if (!namaHama.isEmpty() && !type.isEmpty()) {
                presenter.createHama(namaHama, type);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Mohon isi semua field", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle toolbar home button click to open drawer
        if (item.getItemId() == android.R.id.home) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showHamaList(List<HamaRequest> hamaItems) {
        List<HamaRequest> requests = new ArrayList<>();
        for (HamaRequest item : hamaItems) {
            HamaRequest request = new HamaRequest();
            request.setKodeHama(item.getKodeHama());
            request.setNamaHama(item.getNamaHama());
            request.setType(item.getType());
            requests.add(request);
        }

        adapter.updateData(requests);
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }
}