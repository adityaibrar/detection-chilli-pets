package com.example.chilipestdetection.presenters;

import com.example.chilipestdetection.contracts.PenangananContract;
import com.example.chilipestdetection.models.PenangananData;
import com.example.chilipestdetection.models.PenangananResponse;
import com.example.chilipestdetection.services.ApiClient;
import com.example.chilipestdetection.services.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class PenangananPresenter implements PenangananContract.Presenter {
    private PenangananContract.View view;
    private ApiService apiService;

    public PenangananPresenter(PenangananContract.View view) {
        this.view = view;
        this.apiService = ApiClient.getApiService();
    }

    @Override
    public void loadPenangananList() {
        if (view != null) {
            view.showLoading();
        }

        Call<PenangananResponse> call = apiService.readPenanganan("read_penanganan");
        call.enqueue(new Callback<PenangananResponse>() {
            @Override
            public void onResponse(Call<PenangananResponse> call, Response<PenangananResponse> response) {
                if (view != null) {
                    view.hideLoading();

                    if (response.isSuccessful() && response.body() != null) {
                        PenangananResponse penangananResponse = response.body();
                        if (penangananResponse.isSuccess()) {
                            view.showPenangananList(penangananResponse.getData());
                        } else {
                            view.showError("Gagal memuat data");
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
    public void addPenanganan() {
        if (view != null) {
            view.navigateToAddPenanganan();
        }
    }

    @Override
    public void editPenanganan(PenangananData penanganan) {
        if (view != null) {
            view.navigateToEditPenanganan(penanganan);
        }
    }

    @Override
    public void deletePenanganan(PenangananData penanganan) {
        if (view != null) {
            view.showDeleteConfirmation(penanganan);
        }
    }

    @Override
    public void confirmDelete(PenangananData penanganan) {
        if (view != null) {
            view.showLoading();
        }

        Call<PenangananResponse> call = apiService.deletePenanganan("delete_penanganan", penanganan.getKode_p_hama());
        call.enqueue(new Callback<PenangananResponse>() {
            @Override
            public void onResponse(Call<PenangananResponse> call, Response<PenangananResponse> response) {
                if (view != null) {
                    view.hideLoading();

                    if (response.isSuccessful() && response.body() != null) {
                        PenangananResponse penangananResponse = response.body();
                        if (penangananResponse.isSuccess()) {
                            view.showSuccess("Data berhasil dihapus");
                            loadPenangananList();
                        } else {
                            view.showError("Gagal menghapus data");
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
    public void onDestroy() {
        view = null;
    }
}