package com.witherstorm.witherstormport.client.renderer;

import com.witherstorm.witherstormport.client.model.SimpleGeoModel;
import com.witherstorm.witherstormport.entity.TractorDebris;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/** effect:tractor - skull geometry com textura grass_block / soil_block conforme o variant. */
public class DebrisRenderer extends GeoEntityRenderer<TractorDebris> {
    public DebrisRenderer(EntityRendererProvider.Context context) {
        super(context, new SimpleGeoModel<>("witherstorm_skull", e -> e.getVariant() == 0 ? "grass_block" : "soil_block"));
        this.shadowRadius = 0.0F;
    }
}
