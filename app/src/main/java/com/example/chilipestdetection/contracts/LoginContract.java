package com.example.chilipestdetection.contracts;
public interface LoginContract {
    interface View {
        void showProgress();
        void hideProgress();
        void onLoginSuccess(String typeUser);
        void onLoginError(String message);
        void showMessage(String message);
    }

    interface Presenter {
        void login(String username, String password);
        void checkLoginStatus();
        void onDestroy();
    }
}
