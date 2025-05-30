package com.example.chilipestdetection.contracts;

public interface RegisterContract {
    interface View {
        void showProgress();
        void hideProgress();
        void onRegisterSuccess(String message);
        void onRegisterError(String message);
        void showMessage(String message);
    }

    interface Presenter {

        void register(String username, String password);

        void onDestroy();
    }
}