package com.example.chilipestdetection.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.chilipestdetection.R;

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

public class MainActivity extends AppCompatActivity {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initTensorFlowLite();
        setupClickListeners();
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
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
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
                    selectedBitmap = BitmapFactory.decodeFile(currentPhotoPath);
                    displayImage();
                    break;

                case REQUEST_IMAGE_GALLERY:
                    if (data != null && data.getData() != null) {
                        try {
                            Uri uri = data.getData();
                            selectedBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                            displayImage();
                        } catch (IOException e) {
                            Toast.makeText(this, "Gagal membaca gambar dari galeri", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
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
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Deteksi Ulang", (dialog, which) -> {
            tvResult.setText("Silakan pilih gambar baru untuk deteksi");
            tvConfidence.setText("");
        });
        builder.show();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_GALLERY_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Izin akses galeri diperlukan", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Izin kamera diperlukan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tflite != null) {
            tflite.close();
        }
    }
}