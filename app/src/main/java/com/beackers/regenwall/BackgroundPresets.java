package com.beackers.regenwall;

import java.util.Map;

public class BackgroundPresets {
  public static Map<String, float[]> flowFieldPresets() {
    Map<String, float[]> BG_PRESETS = Map.of(
        "Black", new float[]{ 0f, 0f, 0f },
        "White", new float[]{ 0f, 0f, 1f },
        "Dark Gray", new float[]{ 0f, 0f, .25f},
        "Light Gray", new float[]{ 0f, 0f, .75f},
        "Forest Green", new float[]{ 120f, .75f, .3f },
        "Navy", new float[]{ 240f, .8f, .25f },
        "Custom", new float[]{ 0, 0, 0 }
        );
    return BG_PRESETS;
  }
}
