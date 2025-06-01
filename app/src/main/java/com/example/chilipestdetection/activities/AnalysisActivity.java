package com.example.chilipestdetection.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chilipestdetection.R;
import com.example.chilipestdetection.adapters.HistoryAdapter;
import com.example.chilipestdetection.contracts.AnalysisContract;
import com.example.chilipestdetection.models.AnalysisModel;
import com.example.chilipestdetection.models.DetectionHistory;
import com.example.chilipestdetection.presenters.AnalysisPresenter;
import java.io.File;
import java.util.List;

public class AnalysisActivity extends AppCompatActivity implements AnalysisContract.View, HistoryAdapter.OnHistoryItemClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private HistoryAdapter adapter;
    private AnalysisContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        initViews();
        setupToolbar();
        setupRecyclerView();
        initPresenter();
        loadData();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
//        tvEmptyState = findViewById(R.id.tvEmptyState);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Analisis Riwayat Deteksi");
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(this, null);
        adapter.setOnHistoryItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void initPresenter() {
        AnalysisModel model = new AnalysisModel(this);
        presenter = new AnalysisPresenter(this, model);
    }

    private void loadData() {
        presenter.loadHistoryData();
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
//        tvEmptyState.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showHistoryData(List<DetectionHistory> historyList) {
        recyclerView.setVisibility(View.VISIBLE);
//        tvEmptyState.setVisibility(View.GONE);
        adapter.updateData(historyList);
    }

    @Override
    public void showEmptyState() {
        recyclerView.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showDeleteSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDeleteError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void confirmDelete(DetectionHistory history) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Hapus");
        builder.setMessage("Apakah Anda yakin ingin menghapus riwayat deteksi ini?");
        builder.setPositiveButton("Hapus", (dialog, which) -> {
            presenter.confirmDeleteHistory(history);
        });
        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    @Override
    public void updateHistoryList(List<DetectionHistory> historyList) {
        adapter.updateData(historyList);
    }

    @Override
    public void onItemClick(DetectionHistory history) {
        showDetailDialog(history);
    }

    @Override
    public void onDeleteClick(DetectionHistory history) {
        presenter.deleteHistory(history);
    }

    private void showDetailDialog(DetectionHistory history) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_history_detail, null);

        ImageView ivImage = dialogView.findViewById(R.id.ivDetailImage);
        TextView tvPestType = dialogView.findViewById(R.id.tvDetailPestType);
        TextView tvDate = dialogView.findViewById(R.id.tvDetailDate);
        TextView tvSeverity = dialogView.findViewById(R.id.tvDetailSeverity);
        TextView tvAccuracy = dialogView.findViewById(R.id.tvDetailAccuracy);
        TextView tvDescription = dialogView.findViewById(R.id.tvDetailDescription);

        // Load image
        if (history.getImagePath() != null && !history.getImagePath().isEmpty()) {
            File imageFile = new File(history.getImagePath());
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(history.getImagePath());
                if (bitmap != null) {
                    ivImage.setImageBitmap(bitmap);
                }
            }
        }

        tvPestType.setText(history.getPestType());
        tvDate.setText(history.getDetectionDate());
        tvSeverity.setText(history.getSeverity());
        tvAccuracy.setText(String.format("%.1f%%", history.getAccuracy()));
        tvDescription.setText(getPestDescription(history.getPestType()));

        builder.setView(dialogView);
        builder.setTitle("Detail Riwayat Deteksi");
        builder.setPositiveButton("Tutup", null);
        builder.show();
    }

    private String getPestDescription(String pestType) {
        switch (pestType.toLowerCase()) {
            case "aphids":
            case "kutu daun":
                return "Kutu daun adalah hama kecil yang menghisap cairan tanaman. Pengendalian dapat dilakukan dengan insektisida sistemik atau predator alami.";
            case "thrips":
                return "Thrips menyebabkan kerusakan dengan menghisap cairan sel tanaman. Gunakan perangkap biru atau insektisida kontak.";
            case "whitefly":
            case "kutu kebul":
                return "Kutu kebul dapat menyebarkan virus pada tanaman cabai. Gunakan perangkap kuning atau insektisida sistemik.";
            case "caterpillar":
            case "ulat":
                return "Ulat pemakan daun dapat dikendalikan dengan Bt (Bacillus thuringiensis) atau insektisida kontak.";
            case "healthy":
            case "sehat":
                return "Daun cabai dalam kondisi sehat. Lanjutkan perawatan rutin untuk menjaga kesehatan tanaman.";
            default:
                return "Konsultasikan dengan ahli pertanian untuk penanganan yang tepat.";
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }
}