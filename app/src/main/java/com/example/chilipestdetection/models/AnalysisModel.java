package com.example.chilipestdetection.models;

import android.content.Context;
import com.example.chilipestdetection.contracts.AnalysisContract;
import com.example.chilipestdetection.helpers.DatabaseHelper;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;

public class AnalysisModel implements AnalysisContract.Model {

    private DatabaseHelper databaseHelper;
    private ExecutorService executor;
    private Handler mainHandler;

    public AnalysisModel(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void loadAllHistory(OnHistoryLoadedListener listener) {
        executor.execute(() -> {
            try {
                List<DetectionHistory> historyList = databaseHelper.getAllDetectionHistory();
                mainHandler.post(() -> {
                    if (historyList != null && !historyList.isEmpty()) {
                        listener.onSuccess(historyList);
                    } else {
//                        listener.onSuccess(historyList); // Empty list will be handled by presenter
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> listener.onError("Gagal memuat riwayat: " + e.getMessage()));
            }
        });
    }

    @Override
    public void deleteHistory(int id, OnHistoryDeletedListener listener) {
        executor.execute(() -> {
            try {
                boolean isDeleted = databaseHelper.deleteDetectionHistory(id);
                mainHandler.post(() -> {
                    if (isDeleted) {
                        listener.onSuccess("Riwayat berhasil dihapus");
                    } else {
                        listener.onError("Gagal menghapus riwayat");
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> listener.onError("Error: " + e.getMessage()));
            }
        });
    }

    public void onDestroy() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}