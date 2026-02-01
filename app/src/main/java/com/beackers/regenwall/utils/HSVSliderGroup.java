package com.beackers.regenwall.utils;

import android.app.Activity;

import java.util.List;

public class HSVSliderGroup<T> implements ConfigBinding<T> {
  private List<SliderBinding<T>> hsvSliders;

  public HSVSliderGroup(
      SliderBinding<T> hue,
      SliderBinding<T> sat,
      SliderBinding<T> val
  ) {
    hsvSliders = List.of(
        hue,
        sat,
        val
        );
  }

  @Override
  public void bind(Activity activity) {
    for (SliderBinding<T> s : hsvSliders) {
      s.bind(activity);
    }
  }

  @Override
  public void setProgressFromConfig(Activity a, T config) {
    for (SliderBinding<T> s : hsvSliders) {
      s.setProgressFromConfig(a, config);
    }
  }

  @Override
  public void applyToConfig(Activity a, T config) {
    for (SliderBinding<T> s : hsvSliders) {
      s.applyToConfig(a, config);
    }
  }
}
