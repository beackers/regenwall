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

// my own stuff
import com.beackers.regenwall.flowfield.FlowFieldGenerator;
import com.beackers.regenwall.flowfield.FlowFieldConfig;

public class MainActivity extends AppCompatActivity {
    // text + settings

    // This will be replaced later by something that pulls in different values.
    // By the way: don't polymorph generator or config up here. javac gets mad.
    private String generatorType;

    // List of generator buttons
    private Button flowFieldButton;

    // List of vars needed for generators
    private Button generateButton;
    private SeekBar speedSeek;
    private SeekBar particleCountSeek;

    // Thread handling
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openMainView();
    }

    private void openMainView() {
        setContentView(R.layout.activity_main);
        flowFieldButton = findViewById(R.id.flowFieldButton);
        flowFieldButton.setOnClickListener(v -> openFlowFieldView());
    }

    private void openFlowFieldView() {
        setContentView(R.layout.flow_field);
        generatorType = "FlowField";
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> openMainView());
        speedSeek = findViewById(R.id.speedSeek);
        particleCountSeek = findViewById(R.id.particleCountSeek);
        generateButton = findViewById(R.id.generateButton);
        generateButton.setOnClickListener(v -> flowFieldGenerate());
        TextView particleCountLabel = findViewById(R.id.particleCountLabel);
        TextView speedLabel = findViewById(R.id.speedLabel);
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
        TextView generatorLabel = findViewById(R.id.generatorLabel);    
    }

    private void flowFieldGenerate() {
        generateButton.setEnabled(false);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        FlowFieldGenerator generator = new FlowFieldGenerator();
        FlowFieldConfig config = new FlowFieldConfig();
        config.defaultConfig();
        config.particleCount = Math.max(500, particleCountSeek.getProgress());
        config.speed = speedSeek.getProgress() / 100f;
        config.seed = System.currentTimeMillis();

        executor.execute(() -> {
            Bitmap bitmap = generator.generate(width, height, config);
            mainHandler.post(() -> {
                generateButton.setEnabled(true);
                showImage(bitmap);
            });
        });
    }

    private void showImage(Bitmap image) {
        setContentView(R.layout.view_image);

        ImageView preview = findViewById(R.id.preview);
        Button dontSave = findViewById(R.id.dontSaveImage);
        Button doSave = findViewById(R.id.doSaveImage);
        preview.setImageBitmap(image);

        if ("FlowField".equals(generatorType)) {
            dontSave.setOnClickListener(v -> openFlowFieldView());
            doSave.setOnClickListener(v -> {
                SaveImage.SaveToPictures(this, image);
                openFlowFieldView();
            });
        }
    }
}
