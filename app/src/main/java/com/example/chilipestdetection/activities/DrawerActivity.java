package com.example.chilipestdetection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chilipestdetection.R;
import com.example.chilipestdetection.contracts.DrawerContract;
import com.example.chilipestdetection.presenters.DrawerPresenter;

public abstract class DrawerActivity extends AppCompatActivity implements DrawerContract.View {

    protected  abstract  int getLayoutResourceId();
    private DrawerPresenter presenter;
    private TextView tvDeteksi, tvHama, tvPenanganan, tvAnalisis, tvLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());


        tvDeteksi = findViewById(R.id.tvDeteksi);
        tvHama = findViewById(R.id.tvHama);
        tvPenanganan = findViewById(R.id.tvPenanganan);
        tvAnalisis = findViewById(R.id.tvAnalisis);
        tvLogout = findViewById(R.id.tvLogout);

        presenter = new DrawerPresenter(this, this);

        // Show drawer items based on user type
        presenter.checkUserType();

        // Set click listeners
        tvDeteksi.setOnClickListener(v -> presenter.onDeteksiClicked());
        tvHama.setOnClickListener(v -> presenter.onHamaClicked());
        tvPenanganan.setOnClickListener(v -> presenter.onPenangananClicked());
        tvAnalisis.setOnClickListener(v -> presenter.onAnalisisClicked());
        tvLogout.setOnClickListener(v -> presenter.onLogoutClicked());
    }

    @Override
    public void showUserItems() {
        tvDeteksi.setVisibility(View.VISIBLE);
        tvHama.setVisibility(View.GONE);
        tvPenanganan.setVisibility(View.GONE);
        tvAnalisis.setVisibility(View.VISIBLE);
    }

    @Override
    public void showAdminItems() {
        tvDeteksi.setVisibility(View.GONE);
        tvHama.setVisibility(View.VISIBLE);
        tvPenanganan.setVisibility(View.VISIBLE);
        tvAnalisis.setVisibility(View.GONE);
    }

    @Override
    public void navigateToDeteksi() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void navigateToHama() {
        startActivity(new Intent(this, SecondaryActivity.class));
    }

    @Override
    public void navigateToPenanganan() {
        startActivity(new Intent(this, PenangananActivity.class));
    }

    @Override
    public void navigateToAnalisis() {
//        startActivity(new Intent(this, AddSolusiActivity.class));
    }

    @Override
    public void logoutUser() {
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }
}