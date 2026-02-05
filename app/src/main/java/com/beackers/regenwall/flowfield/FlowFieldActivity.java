package com.beackers.regenwall.flowfield;

// com.beackers
import com.beackers.regenwall.R;
import com.beackers.regenwall.datastore.FlowFieldConfigProto;
import com.beackers.regenwall.datastore.FlowFieldConfigMapper;
import com.beackers.regenwall.datastore.FlowFieldConfigStoreKt;
import androidx.datastore.core.DataStore;
import com.beackers.regenwall.RegenwallApp;
import com.beackers.regenwall.PreviewActivity;
import com.beackers.regenwall.BackgroundPresets;
import com.beackers.regenwall.utils.SliderBinding;
import com.beackers.regenwall.utils.LogSliderBinding;
import com.beackers.regenwall.utils.HSVSliderGroup;
import com.beackers.regenwall.utils.ConfigBinding;

import android.os.Handler;
import android.os.Looper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.Map;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class FlowFieldActivity extends Activity {
  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private final Handler mainHandler = new Handler(Looper.getMainLooper());
  private final String[] BG_PRESETS = BackgroundPresets.flowFieldPresets().keySet().toArray(new String[0]);
  private static final List<ConfigBinding<FlowFieldConfig>> SLIDERS = List.of(
    new SliderBinding<FlowFieldConfig>(
      R.id.speedSeek, R.id.speedLabel, "Speed: %.2f", 0.01f,
      c -> c.speed,
      (c,v) -> c.speed = v
    ),
    new SliderBinding<FlowFieldConfig>(R.id.particleCountSeek, R.id.particleCountLabel, "Particles: %.0f", 1f,
      c -> c.particleCount,
      (c,v) -> c.particleCount = (int)v
    ),
    new SliderBinding<FlowFieldConfig>(R.id.angleRangeSeek, R.id.angleRangeLabel, "Angle Range: %.2f", 0.05f,
      c -> c.angleRange,
      (c,v) -> c.angleRange = v
    ),
    new SliderBinding<FlowFieldConfig>(R.id.strokeWidthSeek, R.id.strokeWidthLabel, "Width: %.2f", 0.01f,
      c -> c.strokeWidth,
      (c,v) -> c.strokeWidth = v
    ),
    new LogSliderBinding<FlowFieldConfig>(R.id.noiseScaleSeek, R.id.noiseScaleLabel, "Noise Scale: %.5f", 0.0001f, 0.1f,
      c -> c.noiseScale,
      (c,v) -> c.noiseScale = v
    ),
    new SliderBinding<FlowFieldConfig>(R.id.stepsSeek, R.id.stepsLabel, "Steps: %.0f", 1f,
      c -> c.steps,
      (c,v) -> c.steps = (int)v
    ),
    new SliderBinding<FlowFieldConfig>(R.id.alphaSeek, R.id.alphaLabel, "Alpha: %.0f", 1f,
      c -> c.alpha,
      (c,v) -> c.alpha = (int)v
    ),
    new HSVSliderGroup<FlowFieldConfig>(
        new SliderBinding<FlowFieldConfig>(R.id.bgHueSeek, R.id.bgHueLabel, "Hue: %.0f", 1f,
          c -> c.bgHue,
          (c,v) -> c.bgHue = (int)v
          ),
        new SliderBinding<FlowFieldConfig>(R.id.bgSatSeek, R.id.bgSatLabel, "Saturation: %.2f", .01f,
          c -> c.bgSat,
          (c,v) -> c.bgSat = v
          ),
        new SliderBinding<FlowFieldConfig>(R.id.bgValSeek, R.id.bgValLabel, "Value: %.2f", .01f,
          c -> c.bgVal,
          (c,v) -> c.bgVal = v
          )
        ,
        (activity, hue, saturation, value) ->
            updateBgColorPreview(activity, hue, saturation, value)
        )
  );

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

    // get datastore and convert to config
    FlowFieldConfigProto proto = FlowFieldConfigStoreKt.readFlowFieldConfig(store);
    FlowFieldConfig config = FlowFieldConfigMapper.fromProto(proto);

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
    ArrayAdapter<String> adapter = new ArrayAdapter<>(
        this,
        android.R.layout.simple_spinner_item,
        BG_PRESETS
      );
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);

    // set progress according to config values
    for (ConfigBinding<FlowFieldConfig> s : SLIDERS) {
      s.bind(this);
      s.setProgressFromConfig(this, config);
    }
    // set spinner
    spinner.setSelection(adapter.getPosition(config.bgColorMode));
  }

  private static void updateBgColorPreview(Activity activity, float hue, float saturation, float value) {
    LinearLayout preview = activity.findViewById(R.id.bgColorPreview);
    if (preview == null) {
      return;
    }
    float[] hsv = new float[] { hue, saturation, value };
    preview.setBackgroundColor(Color.HSVToColor(hsv));
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
    int width = getResources().getDisplayMetrics().widthPixels;
    int height = getResources().getDisplayMetrics().heightPixels;
    FlowFieldGenerator generator = new FlowFieldGenerator();
    FlowFieldConfig config = new FlowFieldConfig();

    // bg
    Spinner spinner = findViewById(R.id.bgColorSpinner);
    String choice = spinner.getSelectedItem().toString();

    // save current config
    config.defaultConfig();
    for (ConfigBinding<FlowFieldConfig> s : SLIDERS) {
      s.applyToConfig(this, config);
    }
    config.bgColorMode = choice;
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
