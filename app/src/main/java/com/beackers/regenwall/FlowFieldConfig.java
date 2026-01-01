package com.beackers.regenwall;

import android.graphics.Color;

public class FlowFieldConfig {
    public long seed;
    public int particleCount;
    public int steps;
    public int cellSize;
    public float speed;
    public float angleRange;
    public int alpha;
    public float strokeWidth;
    public int backgroundColor;
    public int[] palette;

    public static FlowFieldConfig defaultConfig() {
        ArtConfig c = new ArtConfig();
        c.seed = System.currentTimeMillis();
        c.particleCount = 5000;
        c.steps = 300;
        c.cellSize = 40;
        c.speed = 1.5f;
        c.angleRange = 1.0f;
        c.alpha = 20;
        c.strokeWidth = 1f;
        c.backgroundColor = Color.BLACK;
        c.palette = new int[]{
                Color.WHITE,
                Color.CYAN,
                Color.MAGENTA
        };
        return c;
    }
}
