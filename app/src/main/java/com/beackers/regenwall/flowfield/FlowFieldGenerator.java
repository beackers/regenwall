package com.beackers.regenwall.flowfield;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;

import java.util.Random;
import java.util.Arrays;
import com.beackers.regenwall.ArtGenerator;

public class FlowFieldGenerator implements ArtGenerator<FlowFieldConfig> {
    @Override
    public Bitmap generate(
            int width,
            int height,
            FlowFieldConfig config,
            ProgressListener progress) {
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
            Arrays.stream(particles).forEach(p -> {
                // movement logic
                if (p.x < 0 || p.y < 0 || p.x >= width || p.y >= height) {
                    p.reset(rng, width, height);
                    return;
                }
                float n = noise.noise(
                        p.x * noiseScale,
                        p.y * noiseScale
                        );
                float angle = n * angleScale;
                float dx = (float)Math.cos(angle) * config.speed;
                float dy = (float)Math.sin(angle) * config.speed;


                // color logic
                // could add a colorDrift variable to control color noise scale seperate from noiseScale
                Color.colorToHSV(base, hsv);
                float colorNoise = noise.noise(
                        p.x * .3f * noiseScale,
                        p.y * .3f * noiseScale
                        );
                float hue = (colorNoise * 360f + 360f) % 360f;
                float valueNoise = noise.noise(
                        p.x * noiseScale * .25f,
                        p.y * noiseScale * .25f
                        );
                hsv[0] = hue;
                hsv[1] = .75f;
                hsv[2] = .5f + .5f * Math.round(valueNoise * 3f) / 3f;
                paint.setColor(
                        Color.HSVToColor(hsv)
                );

                // drawing
                canvas.drawLine(p.x, p.y, p.x + dx, p.y + dy, paint);
                p.x += dx;
                p.y += dy;
            });
            // progress reporting
            if (progress != null && step % 5 == 0) {
                progress.onProgress((step + 1f) / config.steps);
            }
        }

        return bitmap;
    }
}

