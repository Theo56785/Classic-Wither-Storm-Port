package com.witherstorm.witherstormport.client.model;

import com.witherstorm.witherstormport.WitherStormPort;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

import java.util.function.Function;

/** Modelo sem logica de ossos (crânio e detritos, que usam geometry.witherstorm_skull). */
public class SimpleGeoModel<T extends GeoAnimatable> extends GeoModel<T> {
    private final String geo;
    private final Function<T, String> texture;

    public SimpleGeoModel(String geo, Function<T, String> texture) {
        this.geo = geo;
        this.texture = texture;
    }

    @Override public ResourceLocation getModelResource(T a) {
        return new ResourceLocation(WitherStormPort.MODID, "geo/entity/" + geo + ".geo.json");
    }

    @Override public ResourceLocation getTextureResource(T a) {
        return new ResourceLocation(WitherStormPort.MODID, "textures/entity/" + texture.apply(a) + ".png");
    }

    @Override public ResourceLocation getAnimationResource(T a) {
        return new ResourceLocation(WitherStormPort.MODID, "animations/entity/empty.animation.json");
    }
}
