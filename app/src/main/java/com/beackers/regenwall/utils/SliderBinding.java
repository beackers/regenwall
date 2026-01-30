package com.beackers.regenwall.utils;

import android.app.Activity;
import android.widget.SeekBar;
import android.widget.TextView;

import com.beackers.regenwall.flowfield.FlowFieldConfig;

public class SliderBinding {
  public final int seekId;
  public final int labelId;
  public final String format;
  public final float scale;
  public final ConfigGetter getter;
  public final ConfigSetter setter;

  public SliderBinding(int seekId, int labelId, String format, float scale, ConfigGetter getter, ConfigSetter setter) {
    this.seekId = seekId;
    this.labelId = labelId;
    this.format = format;
    this.scale = scale;
    this.getter = getter;
    this.setter = setter;
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

  public void setProgress(Activity a, FlowFieldConfig config) {
    SeekBar seek = (SeekBar)a.findViewById(seekId);
    float value = this.getter.get(config);
    seek.setProgress((int)(value * this.scale));
  }
}
