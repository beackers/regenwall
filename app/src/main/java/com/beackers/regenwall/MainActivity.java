package com.beackers.regenwall;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Log;

import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

// datastore
import com.beackers.regenwall.datastore.FlowFieldConfigProto;
import com.beackers.regenwall.datastore.FlowFieldConfigMapper;
import com.beackers.regenwall.datastore.FlowFieldConfigStoreKt;
import androidx.datastore.core.DataStore;

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
        // Headers
        setContentView(R.layout.flow_field);
        generatorType = "FlowField";
        RegenwallApp app = (RegenwallApp) getApplication();
        DataStore<FlowFieldConfigProto> store = app.getFlowFieldConfigStore();

        // Buttons
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> openMainView());
        generateButton = findViewById(R.id.generateButton);
        generateButton.setOnClickListener(v -> flowFieldGenerate());
        Button setLivepaperButton = findViewById(R.id.setLivepaper);
        setLivepaperButton.setOnClickListener(v -> app.setLivepaper(this));
        TextView particleCountLabel = findViewById(R.id.particleCountLabel);
        TextView speedLabel = findViewById(R.id.speedLabel);

        // SeekBars
        SeekBar speedSeek = findViewById(R.id.speedSeek);
        SeekBar particleCountSeek = findViewById(R.id.particleCountSeek);
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
                speedLabel.setText("Particle speed: " + (progress / 100f));
            }
            @Override public void onStartTrackingTouch(SeekBar seekbar) {}
            @Override public void onStopTrackingTouch(SeekBar seekbar) {}
        });
        speedSeek.incrementProgressBy(10);
        particleCountSeek.incrementProgressBy(50);

        // update stuff with last used config
        FlowFieldConfigProto proto = FlowFieldConfigStoreKt.readFlowFieldConfig(store);
        FlowFieldConfig config = FlowFieldConfigMapper.fromProto(proto);
        speedSeek.setProgress((int)(config.speed * 100));
        particleCountSeek.setProgress(config.particleCount);
    }

    private void flowFieldGenerate() {
        // headers
        RegenwallApp app = (RegenwallApp) getApplication();
        DataStore<FlowFieldConfigProto> store = app.getFlowFieldConfigStore();
        generateButton.setEnabled(false);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);

        // variables
        SeekBar particleCountSeek = findViewById(R.id.particleCountSeek);
        SeekBar speedSeek = findViewById(R.id.speedSeek);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        FlowFieldGenerator generator = new FlowFieldGenerator();
        FlowFieldConfig config = new FlowFieldConfig();

        // save current config
        config.defaultConfig();
        config.particleCount = Math.max(500, particleCountSeek.getProgress());
        config.speed = speedSeek.getProgress() / 100f;
        config.seed = System.currentTimeMillis();
        FlowFieldConfigProto proto = FlowFieldConfigMapper.toProto(config);
        FlowFieldConfigStoreKt.writeFlowFieldConfig(store, proto);

        // do some generatin'
        executor.execute(() -> {
            Bitmap bitmap = generator.generate(
                    width,
                    height,
                    config,
                    progress -> mainHandler.post(() ->
                        progressBar.setProgress((int)(progress * 100))
                    )
                    );
            mainHandler.post(() -> {
                generateButton.setEnabled(true);
                progressBar.setVisibility(View.GONE);
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
                // "true" denotes only saving as wallpaper image, not as lock screen
                // can be user-configurable later
                try {SaveImage.SaveAsWallpaper(this, image, true);}
                catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Regenwall", "Error setting wallpaper");
                }
                openFlowFieldView();
            });
        }
    }
}
