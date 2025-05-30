package com.example.chilipestdetection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chilipestdetection.R;
import com.example.chilipestdetection.contracts.DrawerContract;
import com.example.chilipestdetection.presenters.DrawerPresenter;

// DrawerActivity.java
public class DrawerActivity extends AppCompatActivity implements DrawerContract.View {

    private DrawerPresenter presenter;
    private TextView tvDeteksi, tvHama, tvAddHama, tvAddSolusi, tvLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        tvDeteksi = findViewById(R.id.tvDeteksi);
        tvHama = findViewById(R.id.tvHama);
        tvAddHama = findViewById(R.id.tvAddHama);
        tvAddSolusi = findViewById(R.id.tvAddSolusi);
        tvLogout = findViewById(R.id.tvLogout);

        presenter = new DrawerPresenter(this, this);

        // Show drawer items based on user type
        presenter.checkUserType();

        // Set click listeners
        tvDeteksi.setOnClickListener(v -> presenter.onDeteksiClicked());
        tvHama.setOnClickListener(v -> presenter.onHamaClicked());
        tvAddHama.setOnClickListener(v -> presenter.onAddHamaClicked());
        tvAddSolusi.setOnClickListener(v -> presenter.onAddSolusiClicked());
        tvLogout.setOnClickListener(v -> presenter.onLogoutClicked());
    }

    @Override
    public void showUserItems() {
        tvDeteksi.setVisibility(View.VISIBLE);
        tvHama.setVisibility(View.VISIBLE);
        tvAddHama.setVisibility(View.GONE);
        tvAddSolusi.setVisibility(View.GONE);
    }

    @Override
    public void showAdminItems() {
        tvDeteksi.setVisibility(View.GONE);
        tvHama.setVisibility(View.GONE);
        tvAddHama.setVisibility(View.VISIBLE);
        tvAddSolusi.setVisibility(View.VISIBLE);
    }

    @Override
    public void navigateToDeteksi() {
//        startActivity(new Intent(this, DeteksiActivity.class));
    }

    @Override
    public void navigateToHama() {
//        startActivity(new Intent(this, HamaActivity.class));
    }

    @Override
    public void navigateToAddHama() {
//        startActivity(new Intent(this, AddHamaActivity.class));
    }

    @Override
    public void navigateToAddSolusi() {
//        startActivity(new Intent(this, AddSolusiActivity.class));
    }

    @Override
    public void logoutUser() {
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }
}