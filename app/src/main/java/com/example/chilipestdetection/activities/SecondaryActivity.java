package com.example.chilipestdetection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.chilipestdetection.R;
import com.example.chilipestdetection.contracts.DrawerContract;
import com.example.chilipestdetection.presenters.DrawerPresenter;
import com.example.chilipestdetection.utils.SharedPreferencesManager;


public class SecondaryActivity extends AppCompatActivity implements DrawerContract.View {
    private DrawerPresenter presenter;
    private SharedPreferencesManager sharedPreferencesManager;
    private TextView tvWelcome, tvUserInfo, tvDeteksi, tvHama, tvAddHama, tvAddSolusi, tvLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary);

        // Inisialisasi view drawer
        tvDeteksi = findViewById(R.id.tvDeteksi);
        tvHama = findViewById(R.id.tvHama);
        tvAddHama = findViewById(R.id.tvAddHama);
        tvAddSolusi = findViewById(R.id.tvAddSolusi);
        tvLogout = findViewById(R.id.tvLogout);

        // Init Presenter
        presenter = new DrawerPresenter(this, this);

        // Cek tipe user dan tampilkan item drawer
        presenter.checkUserType();

        // Set click listeners
        tvDeteksi.setOnClickListener(v -> presenter.onDeteksiClicked());
        tvHama.setOnClickListener(v -> presenter.onHamaClicked());
        tvAddHama.setOnClickListener(v -> presenter.onAddHamaClicked());
        tvAddSolusi.setOnClickListener(v -> presenter.onAddSolusiClicked());
        tvLogout.setOnClickListener(v -> presenter.onLogoutClicked());

        initViews();
        initServices();
        setupToolbar();
        displayUserInfo();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvUserInfo = findViewById(R.id.tvUserInfo);
    }

    private void initServices() {
        sharedPreferencesManager = new SharedPreferencesManager(this);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu); // gunakan icon sesuai asset kamu
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin Dashboard");
        }
            DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        toolbar.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });
    }

    private void displayUserInfo() {

        String username = sharedPreferencesManager.getUsername();
        String userType = sharedPreferencesManager.getTypeUser();

        tvWelcome.setText("Selamat Datang Admin, " + username + "!");
        tvUserInfo.setText("Tipe User: " + userType + " (Administrator)");
    }

//    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            showLogoutDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Apakah Anda yakin ingin logout?")
                .setPositiveButton("Ya", (dialog, which) -> logout())
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void logout() {
        sharedPreferencesManager.clearUserData();
        Intent intent = new Intent(SecondaryActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show();
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

    }

    @Override
    public void navigateToHama() {

    }

    @Override
    public void navigateToAddHama() {

    }

    @Override
    public void navigateToAddSolusi() {

    }

    @Override
    public void logoutUser() {

    }
}