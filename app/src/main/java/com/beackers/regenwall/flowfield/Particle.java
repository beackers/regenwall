package com.beackers.regenwall.flowfield;

import java.util.Random;

public class Particle {
    public float x, y;

    public static Particle[] create(Random rng, int count, int w, int h) {
        Particle[] p = new Particle[count];
        int[] palette = config.palette;
        for (int i = 0; i < count; i++) {
            p[i] = new Particle();
            p[i].reset(rng, w, h);
        }
        return p;
    }

    public void reset(Random rng, int w, int h) {
        x = rng.nextInt(w);
        y = rng.nextInt(h);
    }
}
