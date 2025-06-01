package com.example.chilipestdetection.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.chilipestdetection.R;
import com.example.chilipestdetection.helpers.DatabaseHelper;
import com.example.chilipestdetection.models.DetectionHistory;
import com.example.chilipestdetection.presenters.DrawerPresenter;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends DrawerActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_GALLERY_PERMISSION = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;

    private static final int INPUT_SIZE = 224; // MobileNet input size
    private static final int PIXEL_SIZE = 3; // RGB
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;

    private ImageView imageView;
    private Button btnUpload, btnDetect;
    private TextView tvResult, tvConfidence;
    private Bitmap selectedBitmap;
    private Interpreter tflite;
    private List<String> labelList;
    private String currentPhotoPath;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        initViews();
        initTensorFlowLite();
        initPresenter();
        setupClickListeners();
        setUpToolbar();
    }

    private void initPresenter() {
        DrawerPresenter drawerPresenter = new DrawerPresenter(this, this);

        drawerPresenter.checkUserType();
    }

    private void initViews() {
        imageView = findViewById(R.id.imageView);
        btnUpload = findViewById(R.id.btnUpload);
        btnDetect = findViewById(R.id.btnDetect);
        tvResult = findViewById(R.id.tvResult);
        tvConfidence = findViewById(R.id.tvConfidence);

        btnDetect.setEnabled(false);
    }

    private void initTensorFlowLite() {
        try {
            tflite = new Interpreter(FileUtil.loadMappedFile(this, "mobilent_compatible3.tflite"));
            labelList = FileUtil.loadLabels(this, "coco_labels.txt");
            Toast.makeText(this, "Model berhasil dimuat", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error loading model: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void setupClickListeners() {
        btnUpload.setOnClickListener(v -> showImageSourceDialog());
        btnDetect.setOnClickListener(v -> detectPest());
    }

    private  void setUpToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu); // Pastikan Anda memiliki icon menu
            getSupportActionBar().setTitle("Data Hama");
        }
    }

    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Sumber Gambar");
        builder.setItems(new String[]{"Kamera", "Galeri"}, (dialog, which) -> {
            if (which == 0) {
                openCamera();
            } else {
                openGallery();
            }
        });
        builder.show();
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.chilipestdetection.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        REQUEST_GALLERY_PERMISSION);
                return;
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_GALLERY_PERMISSION);
                return;
            }
        }

        launchImagePicker();
    }

    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir("Pictures");
        try {
            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
            currentPhotoPath = image.getAbsolutePath();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    // Handle camera result
                    selectedBitmap = BitmapFactory.decodeFile(currentPhotoPath);
                    displayImage();
                    break;

                case REQUEST_IMAGE_GALLERY:
                    // Handle gallery result
                    if (data != null && data.getData() != null) {
                        try {
                            Uri uri = data.getData();
                            selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            currentPhotoPath = getRealPathFromURI(uri);
                            displayImage();
                        } catch (IOException e) {
                            Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle toolbar home button click to open drawer
        if (item.getItemId() == android.R.id.home) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    private void displayImage() {
        if (selectedBitmap != null) {
            imageView.setImageBitmap(selectedBitmap);
            btnDetect.setEnabled(true);
            tvResult.setText("Gambar berhasil dimuat. Tekan tombol deteksi untuk menganalisis hama.");
            tvConfidence.setText("");
        }
    }

    private void detectPest() {
        if (selectedBitmap == null || tflite == null) {
            Toast.makeText(this, "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Preprocess image
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(selectedBitmap, INPUT_SIZE, INPUT_SIZE, true);
            ByteBuffer inputBuffer = convertBitmapToByteBuffer(resizedBitmap);

            // Run inference
            float[][] output = new float[1][labelList.size()];
            tflite.run(inputBuffer, output);

            // Get results
            int maxIndex = 0;
            float maxConfidence = output[0][0];

            for (int i = 1; i < output[0].length; i++) {
                if (output[0][i] > maxConfidence) {
                    maxConfidence = output[0][i];
                    maxIndex = i;
                }
            }

            // Display results
            String pestName = labelList.get(maxIndex);
            float confidence = maxConfidence * 100;

            tvResult.setText("Hasil Deteksi: " + pestName);
            tvConfidence.setText(String.format(Locale.getDefault(), "Confidence: %.2f%%", confidence));

            // Show detailed result dialog
            showResultDialog(pestName, confidence);

        } catch (Exception e) {
            Toast.makeText(this, "Error during detection: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * INPUT_SIZE * INPUT_SIZE * PIXEL_SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] intValues = new int[INPUT_SIZE * INPUT_SIZE];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        int pixel = 0;
        for (int i = 0; i < INPUT_SIZE; ++i) {
            for (int j = 0; j < INPUT_SIZE; ++j) {
                final int val = intValues[pixel++];
                byteBuffer.putFloat(((val >> 16) & 0xFF - IMAGE_MEAN) / IMAGE_STD);
                byteBuffer.putFloat(((val >> 8) & 0xFF - IMAGE_MEAN) / IMAGE_STD);
                byteBuffer.putFloat((val & 0xFF - IMAGE_MEAN) / IMAGE_STD);
            }
        }
        return byteBuffer;
    }

    private void showResultDialog(String pestName, float confidence) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hasil Deteksi Hama");

        String message = String.format(Locale.getDefault(),
                "Jenis Hama: %s\nTingkat Keyakinan: %.2f%%\n\n%s",
                pestName, confidence, getPestDescription(pestName));

        builder.setMessage(message);
        builder.setPositiveButton("Simpan ke Riwayat", (dialog, which) -> {
            // Check if we have a valid image path
            if (currentPhotoPath == null || currentPhotoPath.isEmpty()) {
                Toast.makeText(this, "Tidak dapat menyimpan: path gambar tidak valid", Toast.LENGTH_SHORT).show();
                return;
            }
            // Save to history
            saveDetectionToHistory(pestName, confidence, currentPhotoPath);
        });
        builder.setNegativeButton("Deteksi Ulang", (dialog, which) -> {
            tvResult.setText("Silakan pilih gambar baru untuk deteksi");
            tvConfidence.setText("");
        });
        builder.setNeutralButton("Tutup", null);
        builder.show();
    }
    private void saveDetectionToHistory(String pestName, float confidence, String imagePath) {
        try {
            // Check if imagePath is null or empty
            if (imagePath == null || imagePath.isEmpty()) {
                Toast.makeText(this, "Tidak dapat menyimpan: path gambar tidak valid", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseHelper dbHelper = new DatabaseHelper(this);

            // Get current date and time
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String currentDate = sdf.format(new Date());

            // Determine severity based on confidence and pest type
            String severity = determineSeverity(pestName, confidence);

            // Create detection history object
            DetectionHistory history = new DetectionHistory(
                    imagePath,
                    currentDate,
                    pestName,
                    severity,
                    confidence
            );

            // Save to database
            long result = dbHelper.insertDetectionHistory(history);

            if (result != -1) {
                Toast.makeText(this, "Hasil deteksi disimpan ke riwayat", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Gagal menyimpan ke riwayat", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal menyimpan ke riwayat: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String determineSeverity(String pestName, float confidence) {
        // Determine severity based on pest type and confidence
        if (pestName.toLowerCase().contains("healthy") || pestName.toLowerCase().contains("sehat")) {
            return "Sehat";
        }

        if (confidence >= 80) {
            return "Parah";
        } else if (confidence >= 60) {
            return "Sedang";
        } else {
            return "Ringan";
        }
    }

    private String getPestDescription(String pestName) {
        // Add descriptions for different pests
        switch (pestName.toLowerCase()) {
            case "aphids":
            case "kutu daun":
                return "Kutu daun adalah hama kecil yang menghisap cairan tanaman. Pengendalian dapat dilakukan dengan insektisida sistemik atau predator alami.";
            case "thrips":
                return "Thrips menyebabkan kerusakan dengan menghisap cairan sel tanaman. Gunakan perangkap biru atau insektisida kontak.";
            case "whitefly":
            case "kutu kebul":
                return "Kutu kebul dapat menyebarkan virus pada tanaman cabai. Gunakan perangkap kuning atau insektisida sistemik.";
            case "caterpillar":
            case "ulat":
                return "Ulat pemakan daun dapat dikendalikan dengan Bt (Bacillus thuringiensis) atau insektisida kontak.";
            case "healthy":
            case "sehat":
                return "Daun cabai dalam kondisi sehat. Lanjutkan perawatan rutin untuk menjaga kesehatan tanaman.";
            default:
                return "Konsultasikan dengan ahli pertanian untuk penanganan yang tepat.";
        }
    }
}