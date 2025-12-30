package com.beackers.regenwall;

import android.os.Bundle;
import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private ImageView preview;
    private SeekBar speedSeek;
    private SeekBar particleCountSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        preview = findViewById(R.id.preview);
        speedSeek = findViewById(R.id.speedSeek);
        particleCountSeek = findViewById(R.id.particleCountSeek);
        Button generate = findViewById(R.id.generateButton);
        generate.setOnClickListener(v -> generateArt());
    }

    private void generateArt() {
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        ArtConfig config = ArtConfig.defaultConfig();
        config.particleCount = Math.max(500, particleCountSeek.getProgress());
        config.speed = speedSeek.getProgress() / 100f;
        config.seed = System.currentTimeMillis();
        ArtGenerator generator = new FlowFieldGenerator();
        Bitmap bitmap = generator.generate(width, height, config);

        preview.setImageBitmap(bitmap);
    }
}
