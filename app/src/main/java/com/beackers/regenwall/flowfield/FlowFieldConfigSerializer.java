package com.beackers.regenwall.datastore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.datastore.core.CorruptionException;
import androidx.datastore.core.DataStore;
import androidx.datastore.core.Serializer;
import androidx.datastore.dataStore;

import android.graphics.Color;

public class FlowFieldConfigSerializer implements Serializer<FlowFieldConfigProto> {

    @Override
    public FlowFieldConfigProto getDefaultValue() {
        return FlowFieldConfigProto.newBuilder()
            .setSeed(12345678)
            .setParticleCount(1000)
            .setSteps(300)
            .setSpeed(1f)
            .setAngleRange(1.5f)
            .setAlpha(1.0f)
            .setStrokeWidth(1f)
            .setBackgroundColor(Color.BLACK)
            .setNoiseScale(.01f)
            .addAllPalette(
                    Arrays.stream([
                        Color.MAGENTA,
                        Color.WHITE,
                        Color.CYAN
                    ])
                    .boxed()
                    .collect(Collectors.toList())
                    )
            .build();
    }

    @Override
    public FlowFieldConfigProto readFrom(InputStream input) throws IOException {
        try {
            return FlowFieldConfigProto.parseFrom(input);
        } catch (Exception e) {
            throw new CorruptionException("Cannot read FlowFieldConfig", e);
        }
    }

    @Override
    public void writeTo(FlowFieldConfigProto t, OutputStream output) {
        t.writeTo(output);
    }
}
