package com.witherstorm.witherstormport.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.witherstorm.witherstormport.WitherStormPort;
import com.witherstorm.witherstormport.client.model.StormBones;
import com.witherstorm.witherstormport.entity.AbstractWitherStorm;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.Locale;

/** render controllers wst1..N / bbwst1..6 / swst1..5: tentaculos extras, visiveis apos 3 s ou is_powered. */
public class TentacleLayer<T extends AbstractWitherStorm> extends GeoRenderLayer<T> {
    private final ResourceLocation[] models;

    public TentacleLayer(GeoRenderer<T> renderer, String prefix, int count) {
        super(renderer);
        this.models = new ResourceLocation[count];
        for (int i = 0; i < count; i++) {
            models[i] = new ResourceLocation(WitherStormPort.MODID, "geo/entity/" + prefix + "_tentacles" + (i + 1) + ".geo.json");
        }
    }

    @Override
    public void render(PoseStack poseStack, T storm, BakedGeoModel bakedModel, RenderType renderType,
                       MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {
        float life = storm.stormSeconds(partialTick);
        if (!(life > 3.0F || storm.isPowered())) return;

        float tx = StormBones.cosd(life * 100.0F) * 5.0F;   // animation.*.base: cos(life_time*100)*5
        float ty = StormBones.cosd(life * 45.0F) * 5.0F;

        for (ResourceLocation rl : models) {
            BakedGeoModel model = GeckoLibCache.getBakedModels().get(rl);
            if (model == null) continue;
            StormBones.forEachBone(model, b -> {
                String n = b.getName().toLowerCase(Locale.ROOT);
                if (n.startsWith("body0")) StormBones.pos(b, 0.0F, 3.0F, -13.3F);
                else if (n.startsWith("part") || n.startsWith("tentacles")) StormBones.rot(b, tx, ty, 0.0F);
            });
            getRenderer().reRender(model, poseStack, bufferSource, storm, renderType, buffer, partialTick,
                    packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
