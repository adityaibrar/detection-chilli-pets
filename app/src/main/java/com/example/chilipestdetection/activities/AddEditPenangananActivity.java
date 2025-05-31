package com.example.chilipestdetection.activities;
// AddEditPenangananActivity.java
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.chilipestdetection.R;
import com.example.chilipestdetection.contracts.PenangananContract;
import com.example.chilipestdetection.models.HamaRequest;
import com.example.chilipestdetection.models.PenangananData;
import com.example.chilipestdetection.presenters.AddEditPenangananPresenter;
import com.example.chilipestdetection.utils.ImageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddEditPenangananActivity extends AppCompatActivity implements PenangananContract.AddEditView {

    private static final int REQUEST_IMAGE_PICK = 1001;
    private static final int REQUEST_PERMISSION = 1002;
    private static final int REQUEST_PERMISSION_READ_MEDIA = 1003;
    private EditText etTanaman, etGejala, etAturanFuzzy;
    private Spinner spinnerHama;
    private ImageView ivPreview, ivSelectImage;
    private Button btnSave;
    private ProgressBar progressBar;

    private AddEditPenangananPresenter presenter;
    private boolean isEditMode = false;
    private PenangananData currentPenanganan;
    private String selectedImagePath;
    private List<HamaRequest> hamaList;
    private ArrayAdapter<String> hamaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_penanganan);

        initViews();
        setupPresenter();
        checkEditMode();
        setupListeners();

        presenter.loadHamaList();
    }

    private void initViews() {
        etTanaman = findViewById(R.id.etTanaman);
        etGejala = findViewById(R.id.etGejala);
        etAturanFuzzy = findViewById(R.id.etAturanFuzzy);
        spinnerHama = findViewById(R.id.spinnerHama);
        ivPreview = findViewById(R.id.ivPreview);
        ivSelectImage = findViewById(R.id.ivSelectImage);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupPresenter() {
        presenter = new AddEditPenangananPresenter(this, this);
    }

    private void checkEditMode() {
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("is_edit", false);

        if (isEditMode) {
            currentPenanganan = intent.getParcelableExtra("penanganan_data");

            setTitle("Edit Penanganan");
            btnSave.setText("Update");

            if (currentPenanganan != null) {
                presenter.loadPenangananData(currentPenanganan);
            }
        } else {
            setTitle("Tambah Penanganan");
            btnSave.setText("Simpan");
        }
    }

    private void setupListeners() {
        ivSelectImage.setOnClickListener(v -> presenter.selectImage());

        btnSave.setOnClickListener(v -> {
            String tanaman = etTanaman.getText().toString().trim();
            String gejala = etGejala.getText().toString().trim();
            String aturanFuzzy = etAturanFuzzy.getText().toString().trim();

            if (validateInput(tanaman, gejala, aturanFuzzy)) {
                String selectedHama = getSelectedHama();

                if (isEditMode && currentPenanganan != null) {
                    presenter.updatePenanganan(
                            currentPenanganan.getKode_p_hama(),
                            tanaman, selectedHama, gejala, aturanFuzzy, selectedImagePath
                    );
                } else {
                    presenter.savePenanganan(tanaman, selectedHama, gejala, aturanFuzzy, selectedImagePath);
                }
            }
        });
    }

    private boolean validateInput(String tanaman, String gejala, String aturanFuzzy) {
        if (tanaman.isEmpty()) {
            etTanaman.setError("Tanaman tidak boleh kosong");
            etTanaman.requestFocus();
            return false;
        }

        if (gejala.isEmpty()) {
            etGejala.setError("Gejala tidak boleh kosong");
            etGejala.requestFocus();
            return false;
        }

        if (aturanFuzzy.isEmpty()) {
            etAturanFuzzy.setError("Aturan fuzzy tidak boleh kosong");
            etAturanFuzzy.requestFocus();
            return false;
        }

        try {
            double fuzzyValue = Double.parseDouble(aturanFuzzy);
            if (fuzzyValue < 0 || fuzzyValue > 1) {
                etAturanFuzzy.setError("Aturan fuzzy harus antara 0-1");
                etAturanFuzzy.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etAturanFuzzy.setError("Format aturan fuzzy tidak valid");
            etAturanFuzzy.requestFocus();
            return false;
        }

        return true;
    }

    private String getSelectedHama() {
        if (hamaList != null && spinnerHama.getSelectedItemPosition() >= 0) {
            return hamaList.get(spinnerHama.getSelectedItemPosition()).getNamaHama();
        }
        return "";
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
        btnSave.setEnabled(true);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void setTanamanText(String tanaman) {
        etTanaman.setText(tanaman);
    }

    @Override
    public void setHamaSpinner(List<HamaRequest> hamaList, String selectedHama) {
        this.hamaList = hamaList;

        List<String> hamaNames = new ArrayList<>();
        int selectedPosition = 0;

        for (int i = 0; i < hamaList.size(); i++) {
            hamaNames.add(hamaList.get(i).getNamaHama());
            if (selectedHama != null && hamaList.get(i).getNamaHama().equals(selectedHama)) {
                selectedPosition = i;
            }
        }

        hamaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hamaNames);
        hamaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHama.setAdapter(hamaAdapter);

        if (selectedHama != null) {
            spinnerHama.setSelection(selectedPosition);
        }
    }

    @Override
    public void setGejalaText(String gejala) {
        etGejala.setText(gejala);
    }

    @Override
    public void setAturanFuzzyText(String aturanFuzzy) {
        etAturanFuzzy.setText(aturanFuzzy);
    }

    @Override
    public void setImagePreview(String imagePath) {
        selectedImagePath = imagePath;
        if (imagePath != null && new File(imagePath).exists()) {
            ivPreview.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            ivPreview.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void openImagePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ (API 33+) menggunakan READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        REQUEST_PERMISSION_READ_MEDIA);
            } else {
                launchImagePicker();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10-12 (API 29-32) tidak memerlukan izin untuk akses gambar
            launchImagePicker();
        } else {
            // Android 9 dan bawah (API 28-) menggunakan READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION);
            } else {
                launchImagePicker();
            }
        }
    }

    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case REQUEST_PERMISSION:
                case REQUEST_PERMISSION_READ_MEDIA:
                    launchImagePicker();
                    break;
            }
        } else {
            // Jika izin ditolak, tampilkan penjelasan jika perlu
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                // Tampilkan dialog penjelasan mengapa izin dibutuhkan
                new AlertDialog.Builder(this)
                        .setTitle("Izin Diperlukan")
                        .setMessage("Izin akses penyimpanan diperlukan untuk memilih gambar dari galeri")
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Coba minta izin lagi
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                ActivityCompat.requestPermissions(this,
                                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                                        REQUEST_PERMISSION_READ_MEDIA);
                            } else {
                                ActivityCompat.requestPermissions(this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        REQUEST_PERMISSION);
                            }
                        })
                        .setNegativeButton("Batal", null)
                        .create()
                        .show();
            } else {
                // Izin ditolak permanen, arahkan ke pengaturan aplikasi
                Toast.makeText(this,
                        "Izin ditolak. Silakan aktifkan izin di Pengaturan Aplikasi",
                        Toast.LENGTH_LONG).show();

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                String fileName = System.currentTimeMillis() + "_penanganan";
                String imagePath = ImageUtil.saveImageToInternalStorage(this, imageUri, fileName);

                if (imagePath != null) {
                    presenter.onImageSelected(imagePath);
                } else {
                    Toast.makeText(this, "Gagal menyimpan gambar", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }
}