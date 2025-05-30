package com.example.chilipestdetection.contracts;

public interface DrawerContract {
    interface  View {
        void showUserItems();
        void showAdminItems();
        void navigateToDeteksi();
        void navigateToHama();
        void navigateToAddHama();
        void navigateToAddSolusi();
        void logoutUser();
    }

    interface Presenter {
        void checkUserType();
        void onLogoutClicked();
        void onDeteksiClicked();
        void onHamaClicked();
        void onAddHamaClicked();
        void onAddSolusiClicked();
    }
}
