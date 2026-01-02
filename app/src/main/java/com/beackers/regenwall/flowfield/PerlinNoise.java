package com.beackers.regenwall.flowfield;

import java.utils.Random;

public class PerlinNoise {
    private final int[] p = new int[512];
    
    public PerlinNoise(long seed) {
        int[] perm = new int[256];
        for (int i = 0; i < 256; i++) perm[i] = i;
        Random rand = new Random(seed);
        for (int i = 255; i > 0; i--) {
            int j = rand.nextInt(i+1);
            int tmp = perm[i];
            perm[i] = perm[j];
            perm[j] = tmp;
        }

        for (int i = 0; i < 512; i++) {
            p[i] = perm[i & 255];
        }
    }

    private static float fade(float t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private static float lerp(float t, float a, float b) {
        return a + t * (b - a);
    }

    private static float grad(int hash, float x, float y) {
        int h = hash & 3;
        return switch (h) {
            case 0 -> x + y;
            case 1 -> -x + y;
            case 2 -> x - y;
            default -> -x - y;
        };
    }

    public float noise(float x, float y) {
        int X = (int)Math.floor(x) & 255;
        int Y = (int)Math.floor(y) & 255;

        x -= Math.floor(x);
        y -= Math.floor(y);

        float u = fade(x);
        float v = fade(y);

        int aa = p[p[X] + Y];
        int ab = p[p[X] + Y + 1];
        int ba = p[p[X + 1] + Y];
        int bb = p[p[X + 1] + Y + 1];

        float res = lerp(v,
                lerp(u, grad(aa, x, y), grad(ba, x-1, y)),
                lerp(u, grad(ab, x, y-1), grad(bb, x-1, y-1))
                );
        return (res + 1f) * 0.5f; // normalize to [0,1]
    }
}
