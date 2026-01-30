package com.beackers.regenwall.utils;

import android.app.Activity;
import android.widget.SeekBar;
import android.widget.TextView;

public class SliderBinding {
  public final int seekId;
  public final int labelId;
  public final String format;
  public final float scale;

  public SliderBinding(int seekId, int labelId, String format, float scale) {
    this.seekId = seekId;
    this.labelId = labelId;
    this.format = format;
    this.scale = scale;
  }

  public void bind(Activity activity) {
    SeekBar seek = activity.findViewById(seekId);
    TextView label = activity.findViewById(labelId);

    seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar s, int p, boolean fromUser) {
        label.setText(String.format(format, p * scale));
      }
      @Override public void onStartTrackingTouch(SeekBar s) {}
      @Override public void onStopTrackingTouch(SeekBar s) {}
    });
  }
}
