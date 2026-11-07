package com.witherstorm.witherstormport.client.renderer;

import com.witherstorm.witherstormport.client.model.SimpleGeoModel;
import com.witherstorm.witherstormport.entity.WitherStormSkull;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class SkullRenderer extends GeoEntityRenderer<WitherStormSkull> {
    public SkullRenderer(EntityRendererProvider.Context context) {
        super(context, new SimpleGeoModel<>("witherstorm_skull", e -> "witherstorm_skull"));
        this.shadowRadius = 0.0F;
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
