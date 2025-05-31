package com.example.chilipestdetection.helpers;

// DatabaseHelper.java
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "penanganan.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_IMAGES = "images";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_IMAGE_PATH = "image_path";
    private static final String COLUMN_KODE_P_HAMA = "kode_p_hama";

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        onCreate(db);
    }

    public long insertImage(String imagePath, String kodePHama) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE_PATH, imagePath);
        values.put(COLUMN_KODE_P_HAMA, kodePHama);

        long id = db.insert(TABLE_IMAGES, null, values);
        db.close();
        return id;
    }

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

    public void updateImage(String kodePHama, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE_PATH, imagePath);

        db.update(TABLE_IMAGES, values, COLUMN_KODE_P_HAMA + "=?", new String[]{kodePHama});
        db.close();
    }

    public void deleteImage(String kodePHama) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_IMAGES, COLUMN_KODE_P_HAMA + "=?", new String[]{kodePHama});
        db.close();
    }
}

