package com.beackers.regenwall;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.IOException;

class PreviewActivity extends Activity {
  public static void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.view_image);

    // vars
    ImageView preview = findViewById(R.id.preview);
    Button dontSave = findViewById(R.id.dontSaveImage);
    Button doSave = findViewById(R.id.doSaveImage);

    // logic
    String path = getIntent().getStringExtra("image_path");
    Bitmap image = BitmapFactory.decodeFile(path);
    preview.setImageBitmap(image);

    dontSave.setOnClickListener(v -> finish());
    doSave.setOnClickListener(v -> {
        SaveImage.SaveToPictures(this, image);
        // "true" denotes only saving as wallpaper image, not as lock screen
        // can be user-configurable later
        try {SaveImage.SaveAsWallpaper(this, image, true);}
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finish();
    });
    }
