package com.witherstorm.witherstormport.client.renderer;

import com.witherstorm.witherstormport.client.model.Stage1Model;
import com.witherstorm.witherstormport.entity.WitherStormStage1;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class Stage1Renderer extends GeoEntityRenderer<WitherStormStage1> {
    public Stage1Renderer(EntityRendererProvider.Context context) {
        super(context, new Stage1Model());
        this.withScale(2.0F);                                   // scale 2 do wither_storm1
        this.shadowRadius = 0.0F;
        addRenderLayer(new AutoGlowingGeoLayer<>(this));        // emissive_alpha -> *_glowmask.png
        addRenderLayer(new BeamLayer<>(this, Stage1Model.BEAM_MODEL));
    }
}
