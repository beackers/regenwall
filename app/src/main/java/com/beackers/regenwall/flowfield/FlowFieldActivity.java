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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.AdapterView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.beackers.regenwall.utils.SliderBinding;

public class FlowFieldActivity extends Activity {
  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private final Handler mainHandler = new Handler(Looper.getMainLooper());

  private static final SliderBinding[] SLIDERS = new SliderBinding[] {
    new SliderBinding(R.id.speedSeek, R.id.speedLabel, "Speed: %.2f", 0.01f, c -> c.speed, (c,v) -> c.speed = v),
    new SliderBinding(R.id.particleCountSeek, R.id.particleCountLabel, "Particles: %.0f", 1, c -> c.particleCount, (c,v) -> c.particleCount = v),
    new SliderBinding(R.id.angleRangeSeek, R.id.angleRangeLabel, "Angle Range: %.2f", 1f/25f, c -> c.angleRange, (c,v) -> c.angleRange = v),
    new SliderBinding(R.id.strokeWidthSeek, R.id.strokeWidthLabel, "Width: %.2f", 0.01f, c -> c.strokeWidth, (c,v) -> c.strokeWidth = v),
    new SliderBinding(R.id.noiseScaleSeek, R.id.noiseScaleLabel, "Noise Scale: %.2f", 1f/50f, c -> c.noiseScale, (c,v) -> c.noiseScale = v),
    new SliderBinding(R.id.stepsSeek, R.id.stepsLabel, "Steps: %.0f", 1, c -> c.steps, (c,v) -> c.steps = v),
    new SliderBinding(R.id.alphaSeek, R.id.alphaLabel, "Alpha: %.0f", 1, c -> c.alpha, (c,v) -> c.alpha = v),
  };

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

    // BG Color Spinner
    Spinner spinner = findViewById(R.id.bgColorSpinner);
    LinearLayout customLayout = findViewById(R.id.bgCustomColorLayout);

    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            String choice = parent.getItemAtPosition(pos).toString();
            customLayout.setVisibility(choice.equals("Custom") ? View.VISIBLE : View.GONE);
        }
        @Override public void onNothingSelected(AdapterView<?> parent) {}
    });

    // update stuff with last used config
    FlowFieldConfigProto proto = FlowFieldConfigStoreKt.readFlowFieldConfig(store);
    FlowFieldConfig config = FlowFieldConfigMapper.fromProto(proto);

    // SeekBars
    // (genius move: binding here auto-changes the labels. hopefully.)
    for (SliderBinding s : SLIDERS) {
      s.bind(this);
      s.setProgress(this, config);
    }
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
    SeekBar angleRangeSeek = findViewById(R.id.angleRangeSeek);
    SeekBar stepsSeek = findViewById(R.id.stepsSeek);
    SeekBar alphaSeek = findViewById(R.id.alphaSeek);
    SeekBar strokeWidthSeek = findViewById(R.id.strokeWidthSeek);
    SeekBar noiseScaleSeek = findViewById(R.id.noiseScaleSeek);
    int width = getResources().getDisplayMetrics().widthPixels;
    int height = getResources().getDisplayMetrics().heightPixels;
    FlowFieldGenerator generator = new FlowFieldGenerator();
    FlowFieldConfig config = new FlowFieldConfig();

    // save current config
    config.defaultConfig();
    config.particleCount = Math.max(500, particleCountSeek.getProgress());
    config.speed = speedSeek.getProgress() / 100f;
    config.angleRange = angleRangeSeek.getProgress();
    config.noiseScale = noiseScaleSeek.getProgress();
    config.steps = stepsSeek.getProgress();
    config.alpha = alphaSeek.getProgress();
    config.strokeWidth = strokeWidthSeek.getProgress() / 100f;
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
