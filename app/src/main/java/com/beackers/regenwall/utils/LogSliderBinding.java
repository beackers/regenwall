package com.beackers.regenwall.utils;

import android.app.Activity;
import android.widget.SeekBar;
import android.widget.TextView;
import com.beackers.regenwall.ArtConfig;

public class LogSliderBinding extends SliderBinding {
  private final float min;
  private final float max;

  public LogSliderBinding(int seekId, int labelId, String format, float min, float max,
    ConfigGetter getter, ConfigSetter setter
    ) {
    super(seekId, labelId, format, 1f, getter, setter);
    this.min = min;
    this.max = max;
  }

  private float sliderToValue(int progress, int maxProgress) {
    float t = (float)progress / maxProgress;   // 0..1
    float logMin = (float)Math.log10(min);
    float logMax = (float)Math.log10(max);
    return (float)Math.pow(10, logMin + t * (logMax - logMin));
  }

  private int valueToSlider(float value, int maxProgress) {
    float logMin = (float)Math.log10(min);
    float logMax = (float)Math.log10(max);
    float t = ((float)Math.log10(value) - logMin) / (logMax - logMin);
    return (int)(t * maxProgress);
  }

  @Override
  public void bind(Activity activity) {
    SeekBar seek = activity.findViewById(seekId);
    TextView label = activity.findViewById(labelId);

    seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar s, int p, boolean fromUser) {
        float v = sliderToValue(p, s.getMax());
        label.setText(String.format(format, v));
      }
      @Override public void onStartTrackingTouch(SeekBar s) {}
      @Override public void onStopTrackingTouch(SeekBar s) {}
    });
  }

  @Override
  public void setProgressFromConfig(Activity a, ArtConfig config) {
    SeekBar seek = a.findViewById(seekId);
    float v = getter.get(config);
    seek.setProgress(valueToSlider(v, seek.getMax()));
  }

  @Override
  public void applyToConfig(Activity a, ArtConfig config) {
    SeekBar seek = a.findViewById(seekId);
    float v = sliderToValue(seek.getProgress(), seek.getMax());
    setter.set(config, v);
  }
}
