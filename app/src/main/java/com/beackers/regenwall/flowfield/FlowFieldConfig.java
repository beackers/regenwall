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
    public int backgroundColor;
    public int[] palette;
    public float noiseScale;

    @Override
    public void defaultConfig() {
        this.seed = System.currentTimeMillis();
        this.particleCount = 5000;
        this.steps = 300;
        this.speed = 1.5f;
        this.angleRange = 1.0f;
        this.alpha = 20;
        this.strokeWidth = 1f;
        this.backgroundColor = Color.BLACK;
        this.palette = new int[]{
                Color.WHITE,
                Color.CYAN,
                Color.MAGENTA
        };
        this.noiseScale = .01f;
    }
}
