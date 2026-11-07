package com.witherstorm.witherstormport.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.witherstorm.witherstormport.WitherStormPort;
import com.witherstorm.witherstormport.client.model.StormBones;
import com.witherstorm.witherstormport.entity.AbstractWitherStorm;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.Locale;

/**
 * render_controllers/*.tractor_beam: geometria extra com material wither_boss_armor (textura rolando)
 * -> RenderType.energySwirl. {@code gateHeads}: cabecas 2/3 so aparecem na fase "head" (bigbrain2/severed).
 * {@code needPower}: true = so com is_powered (stage1, wither_storm2, bigbrain); false = life>4 || powered.
 */
public class BeamLayer<T extends AbstractWitherStorm> extends GeoRenderLayer<T> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(WitherStormPort.MODID, "textures/entity/tractor_beam.png");
    private final ResourceLocation beamModel;
    private final boolean gateHeads;
    private final boolean needPower;

    public BeamLayer(GeoRenderer<T> renderer, ResourceLocation beamModel, boolean gateHeads, boolean needPower) {
        super(renderer);
        this.beamModel = beamModel;
        this.gateHeads = gateHeads;
        this.needPower = needPower;
    }

    @Override
    public void render(PoseStack poseStack, T storm, BakedGeoModel bakedModel, RenderType renderType,
                       MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {
        float life = storm.stormSeconds(partialTick);
        boolean on = needPower ? storm.isPowered() : (life > 4.0F || storm.isPowered());
        if (!on) return;
        BakedGeoModel beam = GeckoLibCache.getBakedModels().get(beamModel);
        if (beam == null) return;

        StormBones.copyPose(bakedModel, beam);   // a cabeca guia o feixe
        boolean head = storm.visualStage() >= 6;
        StormBones.forEachBone(beam, b -> {
            String n = b.getName().toLowerCase(Locale.ROOT);
            boolean h23 = n.startsWith("head2") || n.startsWith("head3");
            b.setHidden(gateHeads && h23 && !head);
        });
        float scroll = ((storm.tickCount + partialTick) * 0.01F) % 1.0F;   // uv_anim
        RenderType swirl = RenderType.energySwirl(TEXTURE, scroll, scroll);
        getRenderer().reRender(beam, poseStack, bufferSource, storm, swirl, bufferSource.getBuffer(swirl),
                partialTick, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
