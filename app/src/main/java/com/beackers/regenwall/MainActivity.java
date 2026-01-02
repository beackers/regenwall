package com.beackers.regenwall;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    // text + settings
    private Button generateButton;
    private ImageView preview;
    private SeekBar speedSeek;
    private SeekBar particleCountSeek;
    private TextView particleCountLabel;
    private TextView speedLabel;
    private TextView generatorLabel;
    // This will be replaced later by something that pulls in different values.
    private String generatorType = "FlowField";

    // Thread handling
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        preview = findViewById(R.id.preview);
        speedSeek = findViewById(R.id.speedSeek);
        particleCountSeek = findViewById(R.id.particleCountSeek);
        generateButton = findViewById(R.id.generateButton);
        generateButton.setOnClickListener(v -> generateArt());
        particleCountLabel = findViewById(R.id.particleCountLabel);
        speedLabel = findViewById(R.id.speedLabel);
        particleCountSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
                particleCountLabel.setText("Particle count: " + progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekbar) {}
            @Override public void onStopTrackingTouch(SeekBar seekbar) {}
        });
        speedSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
                speedLabel.setText("Particle speed: " + progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekbar) {}
            @Override public void onStopTrackingTouch(SeekBar seekbar) {}
        });

        generatorLabel = findViewById(R.id.generatorLabel);
    }

    private void generateArt() {
        generateButton.setEnabled(false);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        if (generatorType == "FlowField") {
            FlowFieldGenerator generator = new FlowFieldGenerator();
            FlowFieldConfig config = new FlowFieldConfig();
            config.defaultConfig();
            config.particleCount = Math.max(500, particleCountSeek.getProgress());
            config.speed = speedSeek.getProgress() / 100f;
            config.seed = System.currentTimeMillis();

            executor.execute(() -> {
                Bitmap bitmap = generator.generate(width, height, config);
                mainHandler.post(() -> {
                    preview.setImageBitmap(bitmap);
                    SaveImage.SaveToPictures(this, bitmap);
                    generateButton.setEnabled(true);
                });
            });
        }
    }
}
