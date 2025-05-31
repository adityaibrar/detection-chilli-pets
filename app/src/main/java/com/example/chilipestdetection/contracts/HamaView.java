package com.example.chilipestdetection.contracts;

import com.example.chilipestdetection.models.HamaRequest;
import com.example.chilipestdetection.models.HamaResponse;

import java.util.List;

public interface HamaView {
    void showLoading();
    void hideLoading();
    void showHamaList(List<HamaRequest> hamaList);
    void showError(String error);
    void showSuccess(String message);
}