package com.witherstorm.witherstormport.client.renderer;

import com.witherstorm.witherstormport.client.model.SimpleGeoModel;
import com.witherstorm.witherstormport.entity.WitherStormDead;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

/** controller.render.witherstorm_dead (sem script de escala: escala 1). */
public class DeadRenderer extends GeoEntityRenderer<WitherStormDead> {
    public DeadRenderer(EntityRendererProvider.Context context) {
        super(context, new SimpleGeoModel<>("witherstorm_dead", e -> "ws_dead", "witherstorm_dead"));
        this.shadowRadius = 0.0F;
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
