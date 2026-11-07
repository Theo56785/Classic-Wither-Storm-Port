package com.witherstorm.witherstormport.client.renderer;

import com.witherstorm.witherstormport.client.model.StormModel;
import com.witherstorm.witherstormport.entity.AbstractWitherStorm;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

/** Renderer unico das formas finais (scale 6 em todos os client_entity). */
public class StormRenderer<T extends AbstractWitherStorm> extends GeoEntityRenderer<T> {
    public StormRenderer(EntityRendererProvider.Context ctx, StormModel<T> model, String tentaclePrefix, int tentacles,
                         ResourceLocation beamModel, boolean gateHeads, boolean beamNeedsPower) {
        super(ctx, model);
        this.withScale(6.0F);
        this.shadowRadius = 0.0F;
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
        addRenderLayer(new TentacleLayer<>(this, tentaclePrefix, tentacles));
        addRenderLayer(new BeamLayer<>(this, beamModel, gateHeads, beamNeedsPower));
    }
}
