package com.beackers.regenwall.datastore;

import com.beackers.regenwall.datastore.FlowFieldConfigProto;
import com.beackers.regenwall.flowfield.FlowFieldConfig;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;


public final class FlowFieldConfigMapper {
    public static FlowFieldConfigProto toProto(FlowFieldConfig cfg) {
        return FlowFieldConfigProto.newBuilder()
            .setSeed(cfg.seed)
            .setParticleCount(cfg.particleCount)
            .setSteps(cfg.steps)
            .setSpeed(cfg.speed)
            .setAngleRange(cfg.angleRange)
            .setAlpha(cfg.alpha)
            .setStrokeWidth(cfg.strokeWidth)
            .setNoiseScale(cfg.noiseScale)
            .setBgHue(cfg.bgHue)
            .setBgSat(cfg.bgSat)
            .setBgVal(cfg.bgVal)
            .setBgColorMode(cfg.bgColorMode)
            .addAllPalette(
                    Arrays.stream(cfg.palette)
                    .boxed()
                    .collect(Collectors.toList())
                    )
            .build();
    }
    
    public static FlowFieldConfig fromProto(FlowFieldConfigProto proto) {
        FlowFieldConfig cfg = new FlowFieldConfig();
         
        cfg.seed = proto.getSeed();
        cfg.particleCount = proto.getParticleCount();
        cfg.steps = proto.getSteps();
        cfg.speed = proto.getSpeed();
        cfg.angleRange = proto.getAngleRange();
        cfg.alpha = proto.getAlpha();
        cfg.strokeWidth = proto.getStrokeWidth();
        cfg.bgHue = proto.getBgHue();
        cfg.bgSat = proto.getBgSat();
        cfg.bgColorMode = proto.getBgColorMode();
        cfg.bgVal = proto.getBgVal();
        cfg.noiseScale = proto.getNoiseScale();
        cfg.palette = proto.getPaletteList()
            .stream()
            .mapToInt(Integer::intValue)
            .toArray();
        return cfg;
    }
}
