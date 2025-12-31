package com.beackers.regenwall;

import android.os.Bundle;
import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private ImageView preview;
    private SeekBar speedSeek;
    private SeekBar particleCountSeek;
    private TextView particleCountLabel;
    private TextView speedLabel;
    private TextView generatorLabel;
    // This will be replaced later by something that pulls in different values.
    private String generatorType = "FlowField";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        preview = findViewById(R.id.preview);
        speedSeek = findViewById(R.id.speedSeek);
        particleCountSeek = findViewById(R.id.particleCountSeek);
        Button generate = findViewById(R.id.generateButton);
        generate.setOnClickListener(v -> generateArt());
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
                speedLabel.setText("Particle count: " + progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekbar) {}
            @Override public void onStopTrackingTouch(SeekBar seekbar) {}
        });

        generatorLabel = findViewById(R.id.generatorLabel);
    }

    private void generateArt() {
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        ArtConfig config = ArtConfig.defaultConfig();
        config.particleCount = Math.max(500, particleCountSeek.getProgress());
        config.speed = speedSeek.getProgress() / 100f;
        config.seed = System.currentTimeMillis();
        if (generatorType == "FlowField") {
            ArtGenerator generator = new FlowFieldGenerator();
            Bitmap bitmap = generator.generate(width, height, config);
            preview.setImageBitmap(bitmap);
        }
    }
}
