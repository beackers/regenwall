package com.beackers.regenwall;

import android.graphics.Bitmap;

public interface ArtGenerator {
    Bitmap generate(int width, int height, ArtConfig config);
}
