package com.beackers.regenwall.flowfield;

// com.beackers
import com.beackers.regenwall.R;
import com.beackers.regenwall.datastore.FlowFieldConfigProto;
import com.beackers.regenwall.datastore.FlowFieldConfigMapper;
import com.beackers.regenwall.datastore.FlowFieldConfigStoreKt;
import androidx.datastore.core.DataStore;
import com.beackers.regenwall.RegenwallApp;
import com.beackers.regenwall.PreviewActivity;

import android.os.Handler;
import android.os.Looper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.graphics.Bitmap;

import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.view.View;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FlowFieldActivity extends Activity {
  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private final Handler mainHandler = new Handler(Looper.getMainLooper());

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    // Headers
    setContentView(R.layout.flow_field);
    RegenwallApp app = (RegenwallApp) getApplication();
    DataStore<FlowFieldConfigProto> store = app.getFlowFieldConfigStore();

    // Buttons
    Button backButton = findViewById(R.id.backButton);
    backButton.setOnClickListener(v -> finish());
    Button generateButton = findViewById(R.id.generateButton);
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
    Button generateButton = findViewById(R.id.generateButton);
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
    File outFile = new File(getCacheDir(), "preview.png");

    try (FileOutputStream fos = new FileOutputStream(outFile)) {
      image.compress(Bitmap.CompressFormat.PNG, 100, fos);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    Intent intent = new Intent(this, PreviewActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.putExtra("image_path", outFile.getAbsolutePath());
    startActivity(intent);
  }
}
