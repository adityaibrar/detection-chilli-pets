package com.example.chilipestdetection.contracts;

import com.example.chilipestdetection.models.HamaRequest;

import java.util.List;

public interface HamaView {
    void showLoading();
    void hideLoading();
    void showHamaList(List<HamaRequest> hamaList);
    void showError(String error);
    void showSuccess(String message);
}