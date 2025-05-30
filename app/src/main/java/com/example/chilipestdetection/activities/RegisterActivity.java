package com.example.chilipestdetection.activities;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chilipestdetection.R;
import com.example.chilipestdetection.contracts.RegisterContract;
import com.example.chilipestdetection.presenters.RegisterPresenter;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity implements RegisterContract.View {

    private TextInputEditText etUsername, etPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private ProgressBar progressBar;
    private RegisterPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        presenter = new RegisterPresenter(this, this);
        setListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setListeners() {
        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            presenter.register(username, password);
        });

        tvLogin.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
        btnRegister.setEnabled(true);
    }

    @Override
    public void onRegisterSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onRegisterError(String message) {
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