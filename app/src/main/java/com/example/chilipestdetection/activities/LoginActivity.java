package com.example.chilipestdetection.activities;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chilipestdetection.R;
import com.example.chilipestdetection.contracts.LoginContract;
import com.example.chilipestdetection.presenters.LoginPresenter;

public class LoginActivity extends AppCompatActivity implements LoginContract.View {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ProgressBar progressBar;
    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        presenter = new LoginPresenter(this, this);

        // Check if user is already logged in
        presenter.checkLoginStatus();

        setListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setListeners() {
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            presenter.login(username, password);
        });

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
        btnLogin.setEnabled(true);
    }

    @Override
    public void onLoginSuccess(String typeUser) {
        Intent intent;
        if ("admin".equals(typeUser)) {
            intent = new Intent(LoginActivity.this, SecondaryActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoginError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }
}