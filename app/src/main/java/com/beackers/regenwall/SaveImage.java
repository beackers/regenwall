package com.beackers.regenwall;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.OutputStream;
import java.io.IOException;


public class SaveImage {
    static void SaveToPictures(Context context, Bitmap bitmap) {
        String filename = "regenwall-" + System.currentTimeMillis() + ".png";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ReGenWall");

        Uri uri = context.getContentResolver()
            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (uri == null) {
            throw new RunTimeException("Failed to create MediaStore entry");
        }

        try (OutputStream out = context.getContentResolver().openOutputStream(uri)) {
            if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                throw new RuntimeException("Failed to save file");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
