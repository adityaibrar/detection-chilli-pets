package com.example.chilipestdetection.contracts;

import com.example.chilipestdetection.models.HamaRequest;
import com.example.chilipestdetection.models.PenangananData;

import java.util.List;

public interface PenangananContract {

    interface View {
        void showLoading();
        void hideLoading();
        void showPenangananList(List<PenangananData> penangananList);
        void showError(String message);
        void showSuccess(String message);
        void navigateToAddPenanganan();
        void navigateToEditPenanganan(PenangananData penanganan);
        void showDeleteConfirmation(PenangananData penanganan);
    }

    interface Presenter {
        void loadPenangananList();
        void addPenanganan();
        void editPenanganan(PenangananData penanganan);
        void deletePenanganan(PenangananData penanganan);
        void confirmDelete(PenangananData penanganan);
        void onDestroy();
    }

    interface AddEditView {
        void showLoading();
        void hideLoading();
        void showError(String message);
        void showSuccess(String message);
        void finishActivity();
        void setTanamanText(String tanaman);
        void setHamaSpinner(List<HamaRequest> hamaList, String selectedHama);
        void setGejalaText(String gejala);
        void setAturanFuzzyText(String aturanFuzzy);
        void setImagePreview(String imagePath);
        void openImagePicker();
    }

    interface AddEditPresenter {
        void savePenanganan(String tanaman, String hama, String gejala, String aturanFuzzy, String imagePath);
        void updatePenanganan(String kodePHama, String tanaman, String hama, String gejala, String aturanFuzzy, String imagePath);
        void loadHamaList();
        void loadPenangananData(PenangananData penanganan);
        void selectImage();
        void onImageSelected(String imagePath);
        void onDestroy();
    }
}