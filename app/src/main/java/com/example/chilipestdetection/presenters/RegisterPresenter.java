package com.example.chilipestdetection.presenters;
import android.content.Context;

import com.example.chilipestdetection.contracts.RegisterContract;
import com.example.chilipestdetection.models.RegisterResponse;
import com.example.chilipestdetection.services.ApiClient;
import com.example.chilipestdetection.services.ApiService;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterPresenter implements RegisterContract.Presenter {
    private RegisterContract.View view;
    private ApiService apiService;

    public RegisterPresenter(RegisterContract.View view, Context context) {
        this.view = view;
        this.apiService = ApiClient.getApiService();
    }

    @Override
    public void register(String username, String password) {
        if (username.isEmpty() || password.isEmpty() ) {
            view.showMessage("Semua field harus diisi");
            return;
        }

        view.showProgress();

        Map<String, String> registerData = new HashMap<>();
        registerData.put("action", "register");
        registerData.put("username", username);
        registerData.put("password", password);
        registerData.put("type_user", "user");

        Call<RegisterResponse> call = apiService.register(registerData);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                view.hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();
                    if (registerResponse.isSuccess()) {
                        view.onRegisterSuccess(registerResponse.getMessage());
                    } else {
                        view.onRegisterError(registerResponse.getMessage() != null ?
                                registerResponse.getMessage() : "Registrasi gagal");
                    }
                } else {
                    view.onRegisterError("Terjadi kesalahan pada server");
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                view.hideProgress();
                view.onRegisterError("Koneksi gagal: " + t.getMessage());
            }
        });
    }

    @Override
    public void onDestroy() {
        view = null;
    }
}
