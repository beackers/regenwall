package com.beackers.regenwall;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;

import java.util.Random;

public class FlowFieldGenerator implements ArtGenerator {
    @Override
    public Bitmap generate(int width, int height, ArtConfig config) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawColor(config.backgroundColor);

        int cellSize = config.cellSize;
        int cols = width / cellSize;
        int rows = height / cellSize;

        Random rng = new Random(config.seed);

        float[][] angles = new float[cols][rows];
        int[][] colors = new int[cols][rows];

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                angles[x][y] = 
                    rng.nextFloat() * (float)(Math.PI * 2 * config.angleRange);
                colors[x][y] =
                    config.palette[rng.nextInt(config.palette.length)];
            }
        }
        Paint paint = new Paint();
        paint.setStrokeWidth(config.strokeWidth);
        paint.setAlpha(config.alpha);

        Particle[] particles =
            Particle.create(rng, config.particleCount, width, height);
        
        for (int step = 0; step < config.steps; step++) {
            for (Particle p : particles) {
                int cx = (int)(p.x / cellSize);
                int cy = (int)(p.y / cellSize);

                if (cx < 0 || cy < 0 || cx >= cols || cy >= rows) {
                    p.reset(rng, width, height);
                    continue;
                }
                float angle = angles[cx][cy];
                float dx = (float)Math.cos(angle) * config.speed;
                float dy = (float)Math.sin(angle) * config.speed;

                paint.setColor(
                        colors[cx][cy]
                );
                
                canvas.drawLine(p.x, p.y, p.x + dx, p.y + dy, paint);
                p.x += dx;
                p.y += dy;
            }
        }

        return bitmap;
    }
}
                

