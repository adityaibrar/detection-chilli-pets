package com.example.chilipestdetection.presenters;

import android.content.Context;

import com.example.chilipestdetection.contracts.DrawerContract;
import com.example.chilipestdetection.utils.Constants;
import com.example.chilipestdetection.utils.SharedPreferencesManager;

public class DrawerPresenter implements DrawerContract.Presenter {

    private DrawerContract.View drawerView;
    private SharedPreferencesManager sharedPrefManager;

    public DrawerPresenter(DrawerContract.View drawerView, Context context) {
        this.drawerView = drawerView;
        this.sharedPrefManager = new SharedPreferencesManager(context);
    }

    @Override
    public void checkUserType() {
        String userType = sharedPrefManager.getTypeUser();

        if (userType != null) {
            if (userType.equals(Constants.USER)) {
                drawerView.showUserItems();
            } else if (userType.equals(Constants.ADMIN)) {
                drawerView.showAdminItems();
            }
        }
    }

    @Override
    public void onLogoutClicked() {
        sharedPrefManager.clearUserData();
        drawerView.logoutUser();
    }

    @Override
    public void onDeteksiClicked() {
        drawerView.navigateToDeteksi();
    }

    @Override
    public void onHamaClicked() {
        drawerView.navigateToHama();
    }

    @Override
    public void onAnalisisClicked() {
        drawerView.navigateToAnalisis();
    }

    @Override
    public void onPenangananClicked() {
        drawerView.navigateToPenanganan();
    }
}
