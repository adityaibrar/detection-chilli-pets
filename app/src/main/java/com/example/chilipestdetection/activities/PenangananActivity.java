package com.example.chilipestdetection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chilipestdetection.R;
import com.example.chilipestdetection.adapters.PenangananAdapter;
import com.example.chilipestdetection.contracts.PenangananContract;
import com.example.chilipestdetection.models.PenangananData;
import com.example.chilipestdetection.presenters.PenangananPresenter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PenangananActivity extends AppCompatActivity implements PenangananContract.View {

    private RecyclerView recyclerView;
    private PenangananAdapter adapter;
    private FloatingActionButton fabAdd;
    private ProgressBar progressBar;
    private PenangananPresenter presenter;
    private List<PenangananData> penangananList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penanganan);

        initViews();
        setupRecyclerView();
        setupPresenter();
        setupListeners();

        presenter.loadPenangananList();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupRecyclerView() {
        penangananList = new ArrayList<>();
        adapter = new PenangananAdapter(this, penangananList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupPresenter() {
        presenter = new PenangananPresenter(this);
    }

    private void setupListeners() {
        fabAdd.setOnClickListener(v -> presenter.addPenanganan());

        adapter.setOnItemClickListener(new PenangananAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PenangananData penanganan) {
                // Handle item click if needed
                Toast.makeText(PenangananActivity.this, "Item clicked: " + penanganan.getKode_p_hama(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEditClick(PenangananData penanganan) {
                presenter.editPenanganan(penanganan);
            }

            @Override
            public void onDeleteClick(PenangananData penanganan) {
                presenter.deletePenanganan(penanganan);
            }
        });
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showPenangananList(List<PenangananData> penangananList) {
        this.penangananList.clear();
        this.penangananList.addAll(penangananList);
        adapter.updateData(this.penangananList);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToAddPenanganan() {
        Intent intent = new Intent(this, AddEditPenangananActivity.class);
        startActivity(intent);
    }

    @Override
    public void navigateToEditPenanganan(PenangananData penanganan) {
        Intent intent = new Intent(this, AddEditPenangananActivity.class);
        intent.putExtra("penanganan_data", penanganan);
        intent.putExtra("is_edit", true);
        startActivity(intent);
    }

    @Override
    public void showDeleteConfirmation(PenangananData penanganan) {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Hapus")
                .setMessage("Apakah Anda yakin ingin menghapus data penanganan " + penanganan.getKode_p_hama() + "?")
                .setPositiveButton("Ya", (dialog, which) -> presenter.confirmDelete(penanganan))
                .setNegativeButton("Tidak", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.loadPenangananList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }
}