package com.beackers.regenwall.utils;

import android.app.Activity;

public interface ConfigBinding<T> {
  void bind(Activity a);
  void setProgressFromConfig(Activity a, T c);
  void applyToConfig(Activity a, T c);
}
