package com.example.chilipestdetection.presenters;



import android.util.Log;

import com.example.chilipestdetection.contracts.HamaView;
import com.example.chilipestdetection.models.HamaResponse;
import com.example.chilipestdetection.services.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HamaPresenter {

    private HamaView view;

    public HamaPresenter(HamaView view) {
        this.view = view;
    }

    public void loadHamaData() {
        if (view != null) {
            view.showLoading();
        }

        Call<HamaResponse> call = ApiClient.getApiService().readHama("read_hama");
        call.enqueue(new Callback<HamaResponse>() {
            @Override
            public void onResponse(Call<HamaResponse> call, Response<HamaResponse> response) {
                if (view != null) {
                    view.hideLoading();

                    if (response.isSuccessful() && response.body() != null) {
                        HamaResponse HamaResponse = response.body();
                        if (HamaResponse.isSuccess()) {
                            view.showHamaList(HamaResponse.getData());
                        } else {
                            view.showError("Gagal memuat data hama");
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

    public void createHama(String namaHama, String type, String pengendalianRekomndasi, String pestisidaYangDisarankan, String catatanTambahan) {
        if (view != null) {
            view.showLoading();
        }

        Call<HamaResponse> call = ApiClient.getApiService().createHama("create_hama", namaHama, type, pengendalianRekomndasi, pestisidaYangDisarankan, catatanTambahan);
        call.enqueue(new Callback<HamaResponse>() {
            @Override
            public void onResponse(Call<HamaResponse> call, Response<HamaResponse> response) {
                if (view != null) {
                    view.hideLoading();

                    if (response.isSuccessful() && response.body() != null) {
                        HamaResponse HamaResponse = response.body();
                        if (HamaResponse.isSuccess()) {
                            view.showSuccess("Data hama berhasil ditambahkan dengan kode: " + HamaResponse.getKodeHama());
                            loadHamaData(); // Refresh data
                        } else {
                            view.showError("Gagal menambahkan data hama");
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

    public void updateHama(String kodeHama, String namaHama, String type,String pengendalianRekomndasi, String pestisidaYangDisarankan, String catatanTambahan) {
        if (view != null) {
            view.showLoading();
        }

        Call<HamaResponse> call = ApiClient.getApiService().updateHama("update_hama", kodeHama, namaHama, type, pengendalianRekomndasi, pestisidaYangDisarankan, catatanTambahan);
        call.enqueue(new Callback<HamaResponse>() {
            @Override
            public void onResponse(Call<HamaResponse> call, Response<HamaResponse> response) {
                if (view != null) {
                    view.hideLoading();

                    if (response.isSuccessful() && response.body() != null) {
                        HamaResponse HamaResponse = response.body();
                        if (HamaResponse.isSuccess()) {
                            view.showSuccess("Data hama berhasil diperbarui");
                            loadHamaData(); // Refresh data
                        } else {
                            view.showError("Gagal memperbarui data hama");
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

    public void deleteHama(String kodeHama) {
        if (view != null) {
            view.showLoading();
        }

        Call<HamaResponse> call = ApiClient.getApiService().deleteHama("delete_hama", kodeHama);
        call.enqueue(new Callback<HamaResponse>() {
            @Override
            public void onResponse(Call<HamaResponse> call, Response<HamaResponse> response) {
                if (view != null) {
                    view.hideLoading();

                    if (response.isSuccessful() && response.body() != null) {
                        HamaResponse HamaResponse = response.body();
                        if (HamaResponse.isSuccess()) {
                            view.showSuccess("Data hama berhasil dihapus");
                            loadHamaData(); // Refresh data
                        } else {
                            view.showError("Gagal menghapus data hama");
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

    public void onDestroy() {
        view = null;
    }
}