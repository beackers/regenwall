package com.beackers.regenwall.utils;

import android.app.Activity;

import android.widget.SeekBar;

public class HSVSliderGroup<T> implements ConfigBinding<T> {
  public interface HSVChangeListener {
    void onChanged(Activity activity, float hue, float saturation, float value);
  }

  private final SliderBinding<T> hueSlider;
  private final SliderBinding<T> saturationSlider;
  private final SliderBinding<T> valueSlider;
  private final HSVChangeListener changeListener;

  public HSVSliderGroup(
      SliderBinding<T> hue,
      SliderBinding<T> sat,
      SliderBinding<T> val
  ) {
    this(hue, sat, val, null);
  }

  public HSVSliderGroup(
      SliderBinding<T> hue,
      SliderBinding<T> sat,
      SliderBinding<T> val,
      HSVChangeListener changeListener
  ) {
    this.hueSlider = hue;
    this.saturationSlider = sat;
    this.valueSlider = val;
    this.changeListener = changeListener;
  }

  @Override
  public void bind(Activity activity) {
    if (changeListener != null) {
      SliderBinding.ProgressCallback progressCallback = (SeekBar s, int p, boolean fromUser) -> {
        changeListener.onChanged(
            activity,
            getValue(activity, hueSlider),
            getValue(activity, saturationSlider),
            getValue(activity, valueSlider)
        );
      };
      hueSlider.setProgressCallback(progressCallback);
      saturationSlider.setProgressCallback(progressCallback);
      valueSlider.setProgressCallback(progressCallback);
    }
    hueSlider.bind(activity);
    saturationSlider.bind(activity);
    valueSlider.bind(activity);
  }

  @Override
  public void setProgressFromConfig(Activity a, T config) {
    hueSlider.setProgressFromConfig(a, config);
    saturationSlider.setProgressFromConfig(a, config);
    valueSlider.setProgressFromConfig(a, config);
  }

  @Override
  public void applyToConfig(Activity a, T config) {
    hueSlider.applyToConfig(a, config);
    saturationSlider.applyToConfig(a, config);
    valueSlider.applyToConfig(a, config);
  }

  private float getValue(Activity activity, SliderBinding<T> slider) {
    SeekBar seek = activity.findViewById(slider.seekId);
    return seek.getProgress() * slider.scale;
  }
}
