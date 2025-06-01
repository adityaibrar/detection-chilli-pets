package com.example.chilipestdetection.contracts;

import com.example.chilipestdetection.models.DetectionHistory;
import java.util.List;

public interface AnalysisContract {

    interface View {
        void showLoading();
        void hideLoading();
        void showHistoryData(List<DetectionHistory> historyList);
        void showEmptyState();
        void showError(String message);
        void showDeleteSuccess(String message);
        void showDeleteError(String message);
        void confirmDelete(DetectionHistory history);
        void updateHistoryList(List<DetectionHistory> historyList);
    }

    interface Presenter {
        void loadHistoryData();
        void deleteHistory(DetectionHistory history);
        void confirmDeleteHistory(DetectionHistory history);
        void onDestroy();
    }

    interface Model {
        interface OnHistoryLoadedListener {
            void onSuccess(List<DetectionHistory> historyList);
            void onError(String error);
        }

        interface OnHistoryDeletedListener {
            void onSuccess(String message);
            void onError(String error);
        }

        void loadAllHistory(OnHistoryLoadedListener listener);
        void deleteHistory(int id, OnHistoryDeletedListener listener);
    }
}