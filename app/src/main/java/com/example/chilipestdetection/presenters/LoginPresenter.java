package com.example.chilipestdetection.presenters;

import android.content.Context;

import com.example.chilipestdetection.contracts.LoginContract;
import com.example.chilipestdetection.models.LoginResponse;
import com.example.chilipestdetection.services.ApiClient;
import com.example.chilipestdetection.services.ApiService;
import com.example.chilipestdetection.utils.SharedPreferencesManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.Map;

public class LoginPresenter implements LoginContract.Presenter {
    private LoginContract.View view;
    private ApiService apiService;
    private SharedPreferencesManager prefManager;

    public LoginPresenter(LoginContract.View view, Context context) {
        this.view = view;
        this.apiService = ApiClient.getApiService();
        this.prefManager = new SharedPreferencesManager(context);
    }

    @Override
    public void login(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            view.showMessage("Username dan password tidak boleh kosong");
            return;
        }

        view.showProgress();

        // Membuat Map untuk form data
        Map<String, String> loginData = new HashMap<>();
        loginData.put("action", "login");
        loginData.put("username", username);
        loginData.put("password", password);

        Call<LoginResponse> call = apiService.login(loginData);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                view.hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse.isSuccess()) {
                        prefManager.saveUserData(username, password, loginResponse.getType_user());
                        view.onLoginSuccess(loginResponse.getType_user());
                    } else {
                        view.onLoginError(loginResponse.getMessage() != null ?
                                loginResponse.getMessage() : "Login gagal");
                    }
                } else {
                    view.onLoginError("Terjadi kesalahan pada server");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                view.hideProgress();
                view.onLoginError("Koneksi gagal: " + t.getMessage());
            }
        });
    }

    @Override
    public void checkLoginStatus() {
        if (prefManager.isLoggedIn()) {
            String typeUser = prefManager.getTypeUser();
            view.onLoginSuccess(typeUser);
        }
    }

    @Override
    public void onDestroy() {
        view = null;
    }
}