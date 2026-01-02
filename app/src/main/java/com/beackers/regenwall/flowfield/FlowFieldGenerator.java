package com.beackers.regenwall.flowfield;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;

import java.util.Random;

public class FlowFieldGenerator implements ArtGenerator<FlowFieldConfig> {
    @Override
    public Bitmap generate(int width, int height, FlowFieldConfig config) {
        PerlinNoise noise = new PerlinNoise(config.seed);
        float noiseScale = config.noiseScale;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawColor(config.backgroundColor);

        int cellSize = config.cellSize;
        int cols = width / cellSize;
        int rows = height / cellSize;

        Random rng = new Random(config.seed);

        Paint paint = new Paint();
        paint.setStrokeWidth(config.strokeWidth);
        paint.setAlpha(config.alpha);

        Particle[] particles =
            Particle.create(rng, config.particleCount, width, height);
        
        final float TWO_PI = (float)(Math.PI * 2);
        final float angleScale = TWO_PI * config.angleRange;
        final float brightnessScale = 1f / TWO_PI;
        float hsv[] = new float[3];
        for (int step = 0; step < config.steps; step++) {
            for (Particle p : particles) {
                if (p.x < 0 || p.y < 0 || p.x >= width || p.y >= height) {
                    p.reset(rng, width, height);
                    continue;
                }
                float n = noise.noise(
                        p.x * noiseScale,
                        p.y * noiseScale
                        );
                float angle = n * angleScale;
                float dx = (float)Math.cos(angle) * config.speed;
                float dy = (float)Math.sin(angle) * config.speed;

                int base = config.palette[
                    Math.abs((int)(angle * 1000)) % config.palette.length
                ];
                Color.colorToHSV(base, hsv);
                hsv[2] = 0.4f + 0.6f * angle * brightnessScale;

                paint.setColor(
                        Color.HSVToColor(hsv)
                );
                
                canvas.drawLine(p.x, p.y, p.x + dx, p.y + dy, paint);
                p.x += dx;
                p.y += dy;
            }
        }

        return bitmap;
    }
}
