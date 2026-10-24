package com.witherstorm.witherstormport.client.model;

import com.witherstorm.witherstormport.WitherStormPort;
import com.witherstorm.witherstormport.entity.WitherStormEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import static com.witherstorm.witherstormport.client.model.StormBones.*;

/** geometry.witherstorm + animation.wither_storm.default (forma final). */
public class StormModel extends GeoModel<WitherStormEntity> {
    public static final ResourceLocation BEAM_MODEL =
            new ResourceLocation(WitherStormPort.MODID, "geo/entity/wither_storm_beam.geo.json");

    @Override
    public ResourceLocation getModelResource(WitherStormEntity e) {
        return new ResourceLocation(WitherStormPort.MODID, "geo/entity/wither_storm.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WitherStormEntity e) {
        return new ResourceLocation(WitherStormPort.MODID, "textures/entity/wither_storm.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WitherStormEntity e) {
        return new ResourceLocation(WitherStormPort.MODID, "animations/entity/wither_storm.animation.json");
    }

    @Override
    public void setCustomAnimations(WitherStormEntity e, long instanceId, AnimationState<WitherStormEntity> state) {
        super.setCustomAnimations(e, instanceId, state);
        float pt = state.getPartialTick();
        float life = e.stormSeconds(pt);
        float yaw = headYaw(e, pt);
        float pitch = headPitch(e, pt) + (e.hasTarget() ? 0.0F : -15.0F);

        pos(bone("body0"), 0.0F, 3.0F, -13.3F);

        CoreGeoBone h1 = bone("head1v2");
        rot(h1, pitch, yaw, 0);
        scale(h1, 0.73F, 0.73F, 0.73F);
        pos(h1, 0.0F, -10.0F, 0.0F);
        for (String n : new String[]{"head2v2", "head3v2"}) {
            CoreGeoBone h = bone(n);
            rot(h, pitch, yaw, 0);
            scale(h, 0.73F, 0.73F, 0.73F);
            pos(h, 0.0F, 10.0F, 3.5F);
        }

        float open = chinOpen(e.hasTarget(), life);
        rot(bone("chin1"), Math.abs(sind(life * 360.0F)) > 0.85F ? open : 0.0F, 0, 0);
        rot(bone("chin2"), Math.abs(sind(life * 360.0F)) < 0.85F ? open : 0.0F, 0, 0);
        rot(bone("chin3"), Math.abs(cosd(life * 360.0F)) > 0.85F ? open : 0.0F, 0, 0);

        // part_visibility: cabecas so depois de 4 s (ou is_powered); mutantBody0 = is_chested
        boolean heads = life > 4.0F || e.isPowered();
        hide(bone("head1v2"), !heads);
        hide(bone("head2v2"), !heads);
        hide(bone("head3v2"), !heads);
        hide(bone("mutantBody0"), e.visualStage() < 6);
    }

    private CoreGeoBone bone(String name) {
        return getAnimationProcessor().getBone(name);
    }
}
