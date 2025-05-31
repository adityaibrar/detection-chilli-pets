package com.example.chilipestdetection.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtil {

    public static String saveImageToInternalStorage(Context context, Uri imageUri, String fileName) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);

            File directory = new File(context.getFilesDir(), "images");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, fileName + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void deleteImageFromInternalStorage(String imagePath) {
        if (imagePath != null) {
            File file = new File(imagePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}