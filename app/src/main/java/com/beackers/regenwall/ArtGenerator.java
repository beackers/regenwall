package com.beackers.regenwall;

import android.graphics.Bitmap;

public interface ArtGenerator<C extends ArtConfig> {
    Bitmap generate(
            int width,
            int height,
            C config,
            ProgressListener progress
            );
}
