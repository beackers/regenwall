package com.beackers.regenwall.datastore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.datastore.core.CorruptionException;
import androidx.datastore.core.Serializer;

import android.graphics.Color;

public final class FlowFieldConfigSerializer implements Serializer<FlowFieldConfigProto> {

    @Override
    public FlowFieldConfigProto getDefaultValue() {
        return FlowFieldConfigProto.getDefaultInstance();
    }

    @Override
    public FlowFieldConfigProto readFrom(InputStream input) throws IOException, CorruptionException {
        try {
            return FlowFieldConfigProto.parseFrom(input);
        } catch (IOException e) {
            throw new CorruptionException("Cannot read FlowFieldConfig", e);
        }
    }

    @Override
    public void writeTo(
            FlowFieldConfigProto t,
            OutputStream output
            ) throws IOException {
        t.writeTo(output);
    }
}
