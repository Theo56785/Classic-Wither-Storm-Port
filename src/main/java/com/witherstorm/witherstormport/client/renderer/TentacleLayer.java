package com.witherstorm.witherstormport.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.witherstorm.witherstormport.WitherStormPort;
import com.witherstorm.witherstormport.client.model.StormBones;
import com.witherstorm.witherstormport.entity.WitherStormEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/** Os 5 render controllers wst1..wst5 (geometry.witherstorm.tentacles1..5), visiveis apos 3 s / is_powered. */
public class TentacleLayer extends GeoRenderLayer<WitherStormEntity> {
    private static final ResourceLocation[] MODELS = new ResourceLocation[5];

    static {
        for (int i = 0; i < 5; i++) {
            MODELS[i] = new ResourceLocation(WitherStormPort.MODID, "geo/entity/wither_storm_tentacles" + (i + 1) + ".geo.json");
        }
    }

    public TentacleLayer(GeoRenderer<WitherStormEntity> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, WitherStormEntity storm, BakedGeoModel bakedModel, RenderType renderType,
                       MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {
        float life = storm.stormSeconds(partialTick);
        if (!(life > 3.0F || storm.isPowered())) return;

        float tx = StormBones.cosd(life * 100.0F) * 10.0F;   // variable.tentacles_x_rotation
        float ty = StormBones.cosd(life * 45.0F) * 10.0F;    // variable.tentacles_y_rotation

        for (ResourceLocation rl : MODELS) {
            BakedGeoModel model = GeckoLibCache.getBakedModels().get(rl);
            if (model == null) continue;

            model.getBone("body0").ifPresent(b -> StormBones.pos(b, 0.0F, 3.0F, -13.3F));
            model.getBone("tentacles").ifPresent(b -> StormBones.scale(b, 0.6F, 0.6F, 0.95F));
            for (int i = 1; i <= 20; i++) {
                final int idx = i;
                model.getBone("part" + i).ifPresent(b -> {
                    StormBones.rot(b, tx, ty, 0.0F);
                    if (idx == 1) StormBones.scale(b, 0.6F, 0.6F, 1.5F);
                });
            }
            getRenderer().reRender(model, poseStack, bufferSource, storm, renderType, buffer, partialTick,
                    packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
