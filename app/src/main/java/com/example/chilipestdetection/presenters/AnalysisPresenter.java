package com.example.chilipestdetection.presenters;

import com.example.chilipestdetection.contracts.AnalysisContract;
import com.example.chilipestdetection.models.AnalysisModel;
import com.example.chilipestdetection.models.DetectionHistory;
import java.util.List;

public class AnalysisPresenter implements AnalysisContract.Presenter {

    private AnalysisContract.View view;
    private AnalysisContract.Model model;

    public AnalysisPresenter(AnalysisContract.View view, AnalysisModel model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void loadHistoryData() {
        if (view != null) {
//            view.showLoading();
        }

        model.loadAllHistory(new AnalysisContract.Model.OnHistoryLoadedListener() {
            @Override
            public void onSuccess(List<DetectionHistory> historyList) {
                if (view != null) {
                    view.hideLoading();
                    if (historyList != null && !historyList.isEmpty()) {
                        view.showHistoryData(historyList);
                    } else {
                        view.showEmptyState();
                    }
                }
            }

            @Override
            public void onError(String error) {
                if (view != null) {
                    view.hideLoading();
                    view.showError(error);
                }
            }
        });
    }

    @Override
    public void deleteHistory(DetectionHistory history) {
        if (view != null) {
            view.confirmDelete(history);
        }
    }

    @Override
    public void confirmDeleteHistory(DetectionHistory history) {
        if (view != null) {
            view.showLoading();
        }

        model.deleteHistory(history.getId(), new AnalysisContract.Model.OnHistoryDeletedListener() {
            @Override
            public void onSuccess(String message) {
                if (view != null) {
                    view.hideLoading();
                    view.showDeleteSuccess(message);
                    // Reload data after successful deletion
                    loadHistoryData();
                }
            }

            @Override
            public void onError(String error) {
                if (view != null) {
                    view.hideLoading();
                    view.showDeleteError(error);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        view = null;
        if (model instanceof AnalysisModel) {
            ((AnalysisModel) model).onDestroy();
        }
    }
}