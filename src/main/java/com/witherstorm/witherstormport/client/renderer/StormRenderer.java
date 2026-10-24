package com.witherstorm.witherstormport.client.renderer;

import com.witherstorm.witherstormport.client.model.StormModel;
import com.witherstorm.witherstormport.entity.WitherStormEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class StormRenderer extends GeoEntityRenderer<WitherStormEntity> {
    public StormRenderer(EntityRendererProvider.Context context) {
        super(context, new StormModel());
        this.withScale(6.0F);                                   // scale 6 do wither_storm2
        this.shadowRadius = 0.0F;
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
        addRenderLayer(new TentacleLayer(this));
        addRenderLayer(new BeamLayer<>(this, StormModel.BEAM_MODEL));
    }
}
