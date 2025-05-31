package com.example.chilipestdetection.presenters;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;

import com.example.chilipestdetection.contracts.PenangananContract;
import com.example.chilipestdetection.helpers.DatabaseHelper;
import com.example.chilipestdetection.models.HamaRequest;
import com.example.chilipestdetection.models.HamaResponse;
import com.example.chilipestdetection.models.PenangananData;
import com.example.chilipestdetection.models.PenangananResponse;
import com.example.chilipestdetection.services.ApiClient;
import com.example.chilipestdetection.services.ApiService;

public class AddEditPenangananPresenter implements PenangananContract.AddEditPresenter {
    private PenangananContract.AddEditView view;
    private ApiService apiService;
    private DatabaseHelper databaseHelper;
    private Context context;
    private boolean isEditMode = false;
    private String currentKodePHama;
    private PenangananData currentPenanganan;

    public AddEditPenangananPresenter(PenangananContract.AddEditView view, Context context) {
        this.view = view;
        this.context = context;
        this.apiService = ApiClient.getApiService();
        this.databaseHelper = new DatabaseHelper(context);
    }

    @Override
    public void savePenanganan(String tanaman, String hama, String gejala, String aturanFuzzy, String imagePath) {
        if (view != null) {
            view.showLoading();
        }

        // Save image to internal storage and get relative path for API
        String relativeImagePath = "/images/" + System.currentTimeMillis() + ".jpg";

        Call<PenangananResponse> call = apiService.createPenanganan(
                "create_penanganan", tanaman, hama, relativeImagePath, gejala, aturanFuzzy
        );

        call.enqueue(new Callback<PenangananResponse>() {
            @Override
            public void onResponse(Call<PenangananResponse> call, Response<PenangananResponse> response) {
                if (view != null) {
                    view.hideLoading();

                    if (response.isSuccessful() && response.body() != null) {
                        PenangananResponse penangananResponse = response.body();
                        if (penangananResponse.isSuccess()) {
                            // Save image to local database
                            if (imagePath != null && penangananResponse.getKode_p_hama() != null) {
                                databaseHelper.insertImage(imagePath, penangananResponse.getKode_p_hama());
                            }

                            view.showSuccess("Data berhasil disimpan");
                            view.finishActivity();
                        } else {
                            view.showError("Gagal menyimpan data");
                        }
                    } else {
                        view.showError("Gagal terhubung ke server");
                    }
                }
            }

            @Override
            public void onFailure(Call<PenangananResponse> call, Throwable t) {
                if (view != null) {
                    view.hideLoading();
                    view.showError("Kesalahan jaringan: " + t.getMessage());
                }
            }
        });
    }

    @Override
    public void updatePenanganan(String kodePHama, String tanaman, String hama, String gejala, String aturanFuzzy, String imagePath) {
        if (view != null) {
            view.showLoading();
        }

        String relativeImagePath = "/images/" + kodePHama + ".jpg";

        Call<PenangananResponse> call = apiService.updatePenanganan(
                "update_penanganan", kodePHama, tanaman, hama, relativeImagePath, gejala, aturanFuzzy
        );

        call.enqueue(new Callback<PenangananResponse>() {
            @Override
            public void onResponse(Call<PenangananResponse> call, Response<PenangananResponse> response) {
                if (view != null) {
                    view.hideLoading();

                    if (response.isSuccessful() && response.body() != null) {
                        PenangananResponse penangananResponse = response.body();
                        if (penangananResponse.isSuccess()) {
                            // Update image in local database
                            if (imagePath != null) {
                                databaseHelper.updateImage(kodePHama, imagePath);
                            }

                            view.showSuccess("Data berhasil diperbarui");
                            view.finishActivity();
                        } else {
                            view.showError("Gagal memperbarui data");
                        }
                    } else {
                        view.showError("Gagal terhubung ke server");
                    }
                }
            }

            @Override
            public void onFailure(Call<PenangananResponse> call, Throwable t) {
                if (view != null) {
                    view.hideLoading();
                    view.showError("Kesalahan jaringan: " + t.getMessage());
                }
            }
        });
    }

    @Override
    public void loadHamaList() {
        if (view != null) {
            view.showLoading();
        }

        Call<HamaResponse> call = apiService.readHama("read_hama");
        call.enqueue(new Callback<HamaResponse>() {
            @Override
            public void onResponse(Call<HamaResponse> call, Response<HamaResponse> response) {
                if (view != null) {
                    view.hideLoading();

                    if (response.isSuccessful() && response.body() != null) {
                        HamaResponse hamaResponse = response.body();
                        if (hamaResponse.isSuccess() && hamaResponse.getData() != null) {
                            // Langsung menggunakan List<HamaRequest> dari response
                            List<HamaRequest> hamaList = hamaResponse.getData();

                            // Jika dalam mode edit, set selected hama
                            String selectedHama = isEditMode ? currentPenanganan.getHama() : null;
                            view.setHamaSpinner(hamaList, selectedHama);
                        } else {
                            view.showError(hamaResponse.getMessage() != null ?
                                    hamaResponse.getMessage() : "Gagal memuat data hama");

                        }
                    } else {
                        view.showError("Response tidak valid");

                    }
                }
            }

            @Override
            public void onFailure(Call<HamaResponse> call, Throwable t) {
                if (view != null) {
                    view.hideLoading();
                    view.showError("Koneksi bermasalah: " + t.getMessage());

                }
            }
        });
    }

    @Override
    public void loadPenangananData(PenangananData penanganan) {
        isEditMode = true;
        currentKodePHama = penanganan.getKode_p_hama();
        currentPenanganan = penanganan;

        if (view != null) {
            view.setTanamanText(penanganan.getTanaman());
            view.setGejalaText(penanganan.getGejala());
            view.setAturanFuzzyText(String.valueOf(penanganan.getAturan_fuzzy()));

            // Load image from local database
            String imagePath = databaseHelper.getImagePath(penanganan.getKode_p_hama());
            if (imagePath != null) {
                view.setImagePreview(imagePath);
            }

            // Load hama list
            loadHamaList();
        }
    }

    @Override
    public void selectImage() {
        if (view != null) {
            view.openImagePicker();
        }
    }

    @Override
    public void onImageSelected(String imagePath) {
        if (view != null) {
            view.setImagePreview(imagePath);
        }
    }

    @Override
    public void onDestroy() {
        view = null;
    }

}