package com.beackers.regenwall.flowfield;

import android.graphics.Color;
import com.beackers.regenwall.ArtConfig;

public class FlowFieldConfig extends ArtConfig {
    public long seed;
    public int particleCount;
    public int steps;
    public float speed;
    public float angleRange;
    public int alpha;
    public float strokeWidth;
    public int[] palette;
    public float noiseScale;
    public int bgHue;
    public float bgSat;
    public float bgVal;
    public String bgColorMode;

    @Override
    public void defaultConfig() {
        this.seed = System.currentTimeMillis();
        this.particleCount = 5000;
        this.steps = 300;
        this.speed = 1.5f;
        this.angleRange = 1.0f;
        this.alpha = 20;
        this.strokeWidth = 1f;
        this.bgHue = 0;
        this.bgSat = 100;
        this.bgVal = 100;
        this.bgColorMode = "Black";
        this.palette = new int[]{
                Color.WHITE,
                Color.CYAN,
                Color.MAGENTA
        };
        this.noiseScale = .01f;
    }
}
