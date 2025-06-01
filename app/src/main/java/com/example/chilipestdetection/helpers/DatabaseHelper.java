package com.example.chilipestdetection.helpers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.chilipestdetection.models.DetectionHistory;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "penanganan.db";
    private static final int DATABASE_VERSION = 2; // Updated version

    private static final String TABLE_IMAGES = "images";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_IMAGE_PATH = "image_path";
    private static final String COLUMN_KODE_P_HAMA = "kode_p_hama";

    private static final String TABLE_ANALISIS = "analisis";
    private static final String COLUMN_ID_ANALISIS = "id";
    private static final String COLUMN_IMAGE_PATH_ANALISIS = "image_path";
    private static final String COLUMN_KODE_ANALISIS = "kode_analisis";

    // New table for detection history
    private static final String TABLE_DETECTION_HISTORY = "detection_history";
    private static final String COLUMN_HISTORY_ID = "id";
    private static final String COLUMN_HISTORY_IMAGE_PATH = "image_path";
    private static final String COLUMN_HISTORY_DATE = "detection_date";
    private static final String COLUMN_HISTORY_PEST_TYPE = "pest_type";
    private static final String COLUMN_HISTORY_SEVERITY = "severity";
    private static final String COLUMN_HISTORY_ACCURACY = "accuracy";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_IMAGES_TABLE = "CREATE TABLE " + TABLE_IMAGES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_IMAGE_PATH + " TEXT NOT NULL,"
                + COLUMN_KODE_P_HAMA + " TEXT"
                + ")";
        db.execSQL(CREATE_IMAGES_TABLE);

        String CREATE_ANALISIS_TABLE = "CREATE TABLE " + TABLE_ANALISIS + "("
                + COLUMN_ID_ANALISIS + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_IMAGE_PATH_ANALISIS + " TEXT NOT NULL,"
                + COLUMN_KODE_ANALISIS + " TEXT"
                + ")";
        db.execSQL(CREATE_ANALISIS_TABLE);

        String CREATE_DETECTION_HISTORY_TABLE = "CREATE TABLE " + TABLE_DETECTION_HISTORY + "("
                + COLUMN_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_HISTORY_IMAGE_PATH + " TEXT NOT NULL,"
                + COLUMN_HISTORY_DATE + " TEXT NOT NULL,"
                + COLUMN_HISTORY_PEST_TYPE + " TEXT NOT NULL,"
                + COLUMN_HISTORY_SEVERITY + " TEXT NOT NULL,"
                + COLUMN_HISTORY_ACCURACY + " REAL NOT NULL"
                + ")";
        db.execSQL(CREATE_DETECTION_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            String CREATE_DETECTION_HISTORY_TABLE = "CREATE TABLE " + TABLE_DETECTION_HISTORY + "("
                    + COLUMN_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_HISTORY_IMAGE_PATH + " TEXT NOT NULL,"
                    + COLUMN_HISTORY_DATE + " TEXT NOT NULL,"
                    + COLUMN_HISTORY_PEST_TYPE + " TEXT NOT NULL,"
                    + COLUMN_HISTORY_SEVERITY + " TEXT NOT NULL,"
                    + COLUMN_HISTORY_ACCURACY + " REAL NOT NULL"
                    + ")";
            db.execSQL(CREATE_DETECTION_HISTORY_TABLE);
        }
    }

    // Existing methods remain unchanged
    public long insertImage(String imagePath, String kodePHama) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE_PATH, imagePath);
        values.put(COLUMN_KODE_P_HAMA, kodePHama);

        long id = db.insert(TABLE_IMAGES, null, values);
        db.close();
        return id;
    }

    public long insertImageAnalisis(String imagePath, String kodeAnalisis) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE_PATH_ANALISIS, imagePath);
        values.put(COLUMN_KODE_ANALISIS, kodeAnalisis);

        long id = db.insert(TABLE_ANALISIS, null, values);
        db.close();
        return id;
    }

    // New methods for detection history
    public long insertDetectionHistory(DetectionHistory history) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HISTORY_IMAGE_PATH, history.getImagePath());
        values.put(COLUMN_HISTORY_DATE, history.getDetectionDate());
        values.put(COLUMN_HISTORY_PEST_TYPE, history.getPestType());
        values.put(COLUMN_HISTORY_SEVERITY, history.getSeverity());
        values.put(COLUMN_HISTORY_ACCURACY, history.getAccuracy());

        long id = db.insert(TABLE_DETECTION_HISTORY, null, values);
        db.close();
        return id;
    }

    @SuppressLint("Range")
    public List<DetectionHistory> getAllDetectionHistory() {
        List<DetectionHistory> historyList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DETECTION_HISTORY,
                null, null, null, null, null,
                COLUMN_HISTORY_DATE + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                DetectionHistory history = new DetectionHistory();
                history.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_HISTORY_ID)));
                history.setImagePath(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_IMAGE_PATH)));
                history.setDetectionDate(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_DATE)));
                history.setPestType(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_PEST_TYPE)));
                history.setSeverity(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_SEVERITY)));
                history.setAccuracy(cursor.getFloat(cursor.getColumnIndex(COLUMN_HISTORY_ACCURACY)));
                historyList.add(history);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return historyList;
    }

    public boolean deleteDetectionHistory(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_DETECTION_HISTORY,
                COLUMN_HISTORY_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted > 0;
    }

    @SuppressLint("Range")
    public DetectionHistory getDetectionHistoryById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        DetectionHistory history = null;

        Cursor cursor = db.query(TABLE_DETECTION_HISTORY,
                null,
                COLUMN_HISTORY_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            history = new DetectionHistory();
            history.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_HISTORY_ID)));
            history.setImagePath(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_IMAGE_PATH)));
            history.setDetectionDate(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_DATE)));
            history.setPestType(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_PEST_TYPE)));
            history.setSeverity(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_SEVERITY)));
            history.setAccuracy(cursor.getFloat(cursor.getColumnIndex(COLUMN_HISTORY_ACCURACY)));
            cursor.close();
        }

        db.close();
        return history;
    }

    // Existing methods continue...
    public String getImagePath(String kodePHama) {
        SQLiteDatabase db = this.getReadableDatabase();
        String imagePath = null;

        Cursor cursor = db.query(TABLE_IMAGES,
                new String[]{COLUMN_IMAGE_PATH},
                COLUMN_KODE_P_HAMA + "=?",
                new String[]{kodePHama},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            imagePath = cursor.getString(0);
            cursor.close();
        }

        db.close();
        return imagePath;
    }

    public String getImagePathAnalisis(String kodeAnalisis) {
        SQLiteDatabase db = this.getReadableDatabase();
        String imagePath = null;

        Cursor cursor = db.query(TABLE_ANALISIS,
                new String[]{COLUMN_IMAGE_PATH_ANALISIS},
                COLUMN_KODE_ANALISIS + "=?",
                new String[]{kodeAnalisis},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            imagePath = cursor.getString(0);
            cursor.close();
        }

        db.close();
        return imagePath;
    }

    public void updateImage(String kodePHama, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE_PATH, imagePath);

        db.update(TABLE_IMAGES, values, COLUMN_KODE_P_HAMA + "=?", new String[]{kodePHama});
        db.close();
    }

    public void updateImageAnalisis(String kodeAnalisis, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE_PATH_ANALISIS, imagePath);

        db.update(TABLE_ANALISIS, values, COLUMN_KODE_ANALISIS + "=?", new String[]{kodeAnalisis});
        db.close();
    }

    public void deleteImage(String kodePHama) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_IMAGES, COLUMN_KODE_P_HAMA + "=?", new String[]{kodePHama});
        db.close();
    }

    public void deleteImageAnalisis(String kodeAnalisis) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ANALISIS, COLUMN_KODE_ANALISIS + "=?", new String[]{kodeAnalisis});
        db.close();
    }
}