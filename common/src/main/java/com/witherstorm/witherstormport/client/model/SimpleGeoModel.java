package com.witherstorm.witherstormport.client.model;

import com.witherstorm.witherstormport.WitherStormPort;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

import java.util.function.Function;

/** Modelo sem logica de ossos (cranio, detritos e o corpo morto). */
public class SimpleGeoModel<T extends GeoAnimatable> extends GeoModel<T> {
    private final String geo;
    private final Function<T, String> texture;
    private final String animation;

    public SimpleGeoModel(String geo, Function<T, String> texture) {
        this(geo, texture, "empty");
    }

    public SimpleGeoModel(String geo, Function<T, String> texture, String animation) {
        this.geo = geo;
        this.texture = texture;
        this.animation = animation;
    }

    @Override public ResourceLocation getModelResource(T a) {
        return new ResourceLocation(WitherStormPort.MODID, "geo/entity/" + geo + ".geo.json");
    }

    @Override public ResourceLocation getTextureResource(T a) {
        return new ResourceLocation(WitherStormPort.MODID, "textures/entity/" + texture.apply(a) + ".png");
    }

    @Override public ResourceLocation getAnimationResource(T a) {
        return new ResourceLocation(WitherStormPort.MODID, "animations/entity/" + animation + ".animation.json");
    }
}
