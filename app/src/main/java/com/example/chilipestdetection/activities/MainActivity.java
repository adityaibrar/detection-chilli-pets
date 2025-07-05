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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.chilipestdetection.R;
import com.example.chilipestdetection.helpers.DatabaseHelper;
import com.example.chilipestdetection.models.DetectionHistory;
import com.example.chilipestdetection.models.FuzzyRule;
import com.example.chilipestdetection.presenters.DrawerPresenter;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends DrawerActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_GALLERY_PERMISSION = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    // Parameter ukuran input model TensorFlow Lite
    private static final int INPUT_SIZE = 224;         // Ukuran gambar input (224x224)
    private static final int PIXEL_SIZE = 3;           // Jumlah channel warna (RGB)
    private static final int IMAGE_MEAN = 128;         // Nilai rata-rata pixel untuk normalisasi
    private static final float IMAGE_STD = 128.0f;     // Standar deviasi pixel

    private ImageView imageView;
    private Button btnUpload, btnDetect;
    private TextView tvResult, tvConfidence;
    private Bitmap selectedBitmap;
    private Interpreter tflite;
    private List<String> labelList;
    private String currentPhotoPath;

    // Variabel logika fuzzy
    private String[] diseases = {"Kutu Daun", "Lalat Buah", "Thrips", "Tungau"};
    private Map<String, List<FuzzyRule>> ruleBase;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();                // Inisialisasi view (UI components)
        initTensorFlowLite();       // Load model TFLite
        initFuzzyRuleBase();        // Inisialisasi aturan fuzzy
        initPresenter();            // Inisialisasi presenter drawer menu
        setupClickListeners();      // Setup event klik tombol
        setUpToolbar();             // Setup toolbar
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle toolbar home button click to open drawer
        if (item.getItemId() == android.R.id.home) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.START); // Buka drawer saat ikon toolbar diklik
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Inisialisasi presenter untuk drawer menu
    private void initPresenter() {
        DrawerPresenter drawerPresenter = new DrawerPresenter(this, this);

        drawerPresenter.checkUserType();
    }

    // Inisialisasi semua komponen UI
    private void initViews() {
        imageView = findViewById(R.id.imageView);
        btnUpload = findViewById(R.id.btnUpload);
        btnDetect = findViewById(R.id.btnDetect);
        tvResult = findViewById(R.id.tvResult);
        tvConfidence = findViewById(R.id.tvConfidence);
        btnDetect.setEnabled(false); // Tombol deteksi dinonaktifkan sampai gambar dimuat
    }

    // Inisialisasi tensorflow
    private void initTensorFlowLite() {
        try {
            tflite = new Interpreter(FileUtil.loadMappedFile(this, "leaf_model.tflite"));
            labelList = FileUtil.loadLabels(this, "coco_labels.txt");
            Toast.makeText(this, "Model berhasil dimuat", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error loading model: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    // Inisialisasi aturan logika fuzzy
    private void initFuzzyRuleBase() {
        ruleBase = new HashMap<>();

        // Rule Base for Kutu Daun
        List<FuzzyRule> kutuDaunRules = new ArrayList<>();
        kutuDaunRules.add(new FuzzyRule(Arrays.asList("daun_melingkar", "daun_keriput"), 0.85));
        ruleBase.put("Kutu Daun", kutuDaunRules);

        // Rule Base for Lalat Buah
        List<FuzzyRule> lalatBuahRules = new ArrayList<>();
        lalatBuahRules.add(new FuzzyRule(Arrays.asList("bercak_buah_hitam", "buah_busuk"), 0.8));
        ruleBase.put("Lalat Buah", lalatBuahRules);

        // Rule Base for Tungau
        List<FuzzyRule> tungauRules = new ArrayList<>();
        tungauRules.add(new FuzzyRule(Arrays.asList("daun_melengkung", "daun_mengerut"), 0.75));
        ruleBase.put("Tungau", tungauRules);

        // Rule Base for Thrips
        List<FuzzyRule> thripsRules = new ArrayList<>();
        thripsRules.add(new FuzzyRule(Arrays.asList("daun_keriting", "daun_kuning"), 0.7));
        ruleBase.put("Thrips", thripsRules);
    }

    private void setupClickListeners() {
        btnUpload.setOnClickListener(v -> showImageSourceDialog());
        btnDetect.setOnClickListener(v -> detectImage());
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
            getSupportActionBar().setTitle("Deteksi Hama");
        }
    }

    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Sumber Gambar");
        builder.setItems(new String[]{"Kamera", "Galeri"}, (dialog, which) -> {
            if (which == 0) openCamera();
            else openGallery();
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
        startActivityForResult(intent, 2);
    }

    // Membuat file sementara untuk menyimpan foto dari kamera
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
                case 2:
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

    // Mendapatkan path absolut dari URI gambar
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

    // Menampilkan gambar di ImageView dan mengaktifkan tombol deteksi
    private void displayImage() {
        if (selectedBitmap != null) {
            imageView.setImageBitmap(selectedBitmap);
            btnDetect.setEnabled(true);
            tvResult.setText("Gambar berhasil dimuat. Tekan tombol deteksi untuk menganalisis hama.");
            tvConfidence.setText("");
        }
    }

    // Menyimpan hasil deteksi ke database riwayat
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

    // Melakukan deteksi gambar apakah daun cabai menggunakan model TFLite
    private void detectImage() {
        if (selectedBitmap == null || tflite == null) {
            Toast.makeText(this, "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(selectedBitmap, INPUT_SIZE, INPUT_SIZE, true);
            ByteBuffer inputBuffer = convertBitmapToByteBuffer(resizedBitmap);

            float[][] output = new float[1][labelList.size()];
            tflite.run(inputBuffer, output);

            int maxIndex = 0;
            float maxConfidence = output[0][0];
            for (int i = 1; i < output[0].length; i++) {
                if (output[0][i] > maxConfidence) {
                    maxConfidence = output[0][i];
                    maxIndex = i;
                }
            }

            String pestName = labelList.get(maxIndex);
            float confidence = maxConfidence * 100;

            if (pestName.equalsIgnoreCase("background")) {
                Toast.makeText(this, "Hanya bisa upload gambar daun cabai", Toast.LENGTH_LONG).show();
                return;
            }

            tvResult.setText("Gambar: " + pestName);
            tvConfidence.setText(String.format(Locale.getDefault(), "Keyakinan bahwa gambar daun cabai: %.2f%%", confidence));
            showFuzzyLogicDialog();
        } catch (Exception e) {
            Toast.makeText(this, "Error during detection: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    // Mengubah bitmap menjadi buffer input untuk model TFLite
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

    // Dialog checkbox gejala untuk logika fuzzy
    private void showFuzzyLogicDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Gejala Tanaman Cabai");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_fuzzy_symptoms, null);
        dialogBuilder.setView(dialogView);

        CheckBox cbDaunMelingkar = dialogView.findViewById(R.id.cb_daun_melingkar);
        CheckBox cbBercakBuahHitam = dialogView.findViewById(R.id.cb_bercak_buah_hitam);
        CheckBox cbDaunKeriput = dialogView.findViewById(R.id.cb_daun_keriput);
        CheckBox cbDaunMelengkung = dialogView.findViewById(R.id.cb_daun_melengkung);
        CheckBox cbBuahBusuk = dialogView.findViewById(R.id.cb_buah_busuk);
        CheckBox cbDaunMengerut = dialogView.findViewById(R.id.cb_daun_mengerut);
        CheckBox cbDaunKeriting = dialogView.findViewById(R.id.cb_daun_keriting);
        CheckBox cbDaunKuning = dialogView.findViewById(R.id.cb_daun_kuning);

        dialogBuilder.setPositiveButton("Deteksi", (dialog, which) -> {
            List<String> selectedSymptoms = new ArrayList<>();
            if (cbDaunMelingkar.isChecked()) selectedSymptoms.add("daun_melingkar");
            if (cbDaunKeriput.isChecked()) selectedSymptoms.add("daun_keriput");
            if (cbBercakBuahHitam.isChecked()) selectedSymptoms.add("bercak_buah_hitam");
            if (cbBuahBusuk.isChecked()) selectedSymptoms.add("buah_busuk");
            if (cbDaunMelengkung.isChecked()) selectedSymptoms.add("daun_melengkung");
            if (cbDaunMengerut.isChecked()) selectedSymptoms.add("daun_mengerut");
            if (cbDaunKeriting.isChecked()) selectedSymptoms.add("daun_keriting");
            if (cbDaunKuning.isChecked()) selectedSymptoms.add("daun_kuning");

            if (selectedSymptoms.isEmpty()) {
                Toast.makeText(MainActivity.this, "Pilih minimal satu gejala!", Toast.LENGTH_SHORT).show();
                return;
            }

            performFuzzyDetection(selectedSymptoms);
        });

        dialogBuilder.setNegativeButton("Batal", null);
        dialogBuilder.show();
    }

    // Jalankan proses logika fuzzy
    private void performFuzzyDetection(List<String> symptoms) {
        Map<String, Double> fuzzified = fuzzification(symptoms);
        Map<String, Double> inference = fuzzyInference(fuzzified);
        Map<String, Double> defuzzified = defuzzification(inference);
        displayResults(defuzzified, symptoms.size());
    }

    // Proses fuzzifikasi (menghitung derajat keanggotaan fuzzy)
    private Map<String, Double> fuzzification(List<String> symptoms) {
        Map<String, Double> fuzzified = new HashMap<>();
        for (String disease : diseases) {
            double totalMembership = 0.0;
            List<FuzzyRule> rules = ruleBase.get(disease);
            if (rules != null) {
                for (FuzzyRule rule : rules) {
                    boolean allMatch = true;
                    for (String symptom : rule.symptoms) {
                        if (!symptoms.contains(symptom)) {
                            allMatch = false;
                            break;
                        }
                    }
                    if (allMatch) {
                        // Convert membership value to fuzzy value based on percentage
                        double membershipPercentage = rule.membershipValue * 100;
                        double fuzzyValue;
                        if (membershipPercentage <= 40) {
                            fuzzyValue = membershipPercentage / 100.0;
                        } else if (membershipPercentage <= 70) {
                            fuzzyValue = (membershipPercentage - 40) / 30.0 * 0.3 + 0.4;
                        } else {
                            fuzzyValue = (membershipPercentage - 70) / 30.0 * 0.3 + 0.7;
                        }
                        totalMembership += fuzzyValue;
                    }
                }
            }
            fuzzified.put(disease, totalMembership);
        }
        return fuzzified;
    }

    // Proses inferensi fuzzy
    private Map<String, Double> fuzzyInference(Map<String, Double> fuzzified) {
        Map<String, Double> inference = new HashMap<>();
        Map<String, Integer> weights = new HashMap<>(); // Define weights for each gejala
        weights.put("daun_keriting", 4);
        weights.put("daun_berwarna_kuning", 3);
        weights.put("bercak_hitam", 2);
        weights.put("buah_membusuk", 2);
        weights.put("daun_melengkung", 3);
        weights.put("tunas_mengecil", 1);

        for (String disease : diseases) {
            double numeratorSum = 0.0;
            double denominatorSum = 0.0;
            List<FuzzyRule> rules = ruleBase.get(disease);
            if (rules != null) {
                for (FuzzyRule rule : rules) {
                    for (String symptom : rule.symptoms) {
                        double fuzzyValue = fuzzified.get(disease);
                        int weight = weights.getOrDefault(symptom, 1); // Default weight is 1
                        numeratorSum += fuzzyValue * weight;
                        denominatorSum += fuzzyValue;
                    }
                }
            }
            double inferenceValue = denominatorSum > 0 ? numeratorSum / denominatorSum : 0.0;
            inference.put(disease, inferenceValue);
        }
        return inference;
    }

    // Proses defuzzifikasi (konversi nilai fuzzy ke nilai numerik)
    private Map<String, Double> defuzzification(Map<String, Double> inference) {
        Map<String, Double> defuzzified = new HashMap<>();
        double maxWeight = 4; // Maximum bobot (from your perhitungan fuzzy)
        for (String disease : diseases) {
            double crispValue = inference.get(disease);
            double normalizedValue = crispValue / maxWeight;
            defuzzified.put(disease, normalizedValue);
        }
        return defuzzified;
    }

    // Menampilkan hasil akhir analisis fuzzy
    private void displayResults(Map<String, Double> results, int totalSymptoms) {
        List<Map.Entry<String, Double>> sortedResults = new ArrayList<>(results.entrySet());
        sortedResults.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        String mainDisease = sortedResults.get(0).getKey();
        double mainPercentage = sortedResults.get(0).getValue() * 100;

        // Interpret severity level
        String severity;
        if (mainPercentage >= 71) {
            severity = "Parah";
        } else if (mainPercentage >= 41) {
            severity = "Sedang";
        } else {
            severity = "Sedikit";
        }

        String hasilText = String.format("HASIL DETEKSI: %s (%.1f%%) \nTingkat Keparahan: %s \nREKOMENDASI PENANGANAN: %s",
                mainDisease,
                mainPercentage,
                severity,
                getRecommendation(mainDisease));
        AlertDialog.Builder resultBuilder = new AlertDialog.Builder(this);
        resultBuilder.setTitle("Hasil Analisis Fuzzy");
        resultBuilder.setMessage(hasilText);
        resultBuilder.setPositiveButton("Simpan ke Riwayat", (dialog, which) -> {
            if (currentPhotoPath == null || currentPhotoPath.isEmpty()) {
                Toast.makeText(this, "Foto tidak tersedia", Toast.LENGTH_SHORT).show();
                return;
            }
            saveDetectionToHistory(mainDisease, (float) mainPercentage, currentPhotoPath);
        });
        resultBuilder.show();
    }

    // Rekomendasi berdasarkan penyakit
    private String getRecommendation(String disease) {
        switch (disease) {
            case "Kutu Daun": return "• Semprot insektisida sistemik\n• Bersihkan gulma\n• Perbaiki drainase";
            case "Lalat Buah": return "• Pasang perangkap\n• Buang buah busuk\n• Gunakan plastik penutup";
            case "Thrips": return "• Gunakan insektisida kontak\n• Tingkatkan kelembaban\n• Rotasi tanaman";
            case "Tungau": return "• Semprot akarisida\n• Isolasi tanaman\n• Buang daun rusak";
            default: return "Konsultasikan dengan ahli pertanian";
        }
    }

    // Menentukan tingkat keparahan berdasarkan persentase keyakinan
    private String determineSeverity(String pestName, float confidence) {
        // Determine severity based on pest type and confidence
        if (pestName.toLowerCase().contains("healthy") || pestName.toLowerCase().contains("sehat")) {
            return "Sehat";
        }

        if (confidence >= 71) {
            return "Parah";
        } else if (confidence >= 41) {
            return "Sedang";
        } else {
            return "Ringan";
        }
    }
}