package com.witherstorm.witherstormport.client.model;

import com.witherstorm.witherstormport.WitherStormPort;
import com.witherstorm.witherstormport.entity.WitherStormStage1;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import static com.witherstorm.witherstormport.client.model.StormBones.*;

/**
 * geometry.wither_command + render_controllers/wither_storm (part_visibility) +
 * animation.wither_storm.default da fase de formacao.
 */
public class Stage1Model extends GeoModel<WitherStormStage1> {
    public static final ResourceLocation BEAM_MODEL =
            new ResourceLocation(WitherStormPort.MODID, "geo/entity/wither_storm_stage1_beam.geo.json");

    @Override
    public ResourceLocation getModelResource(WitherStormStage1 e) {
        return new ResourceLocation(WitherStormPort.MODID, "geo/entity/wither_storm_stage1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WitherStormStage1 e) {
        return new ResourceLocation(WitherStormPort.MODID, "textures/entity/wither_storm.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WitherStormStage1 e) {
        return new ResourceLocation(WitherStormPort.MODID, "animations/entity/wither_storm_stage1.animation.json");
    }

    @Override
    public void setCustomAnimations(WitherStormStage1 e, long instanceId, AnimationState<WitherStormStage1> state) {
        super.setCustomAnimations(e, instanceId, state);
        float pt = state.getPartialTick();
        float life = e.stormSeconds(pt);
        float yaw = headYaw(e, pt);
        float pitch = headPitch(e, pt);

        // variable.body_base_rotation / upper_body_rotation
        float base = cosd(life * 114.6F);
        rot(bone("upperBodyPart2"), (0.065F + 0.05F * base) * 180.0F + pitch, 0, 0);
        CoreGeoBone p3 = bone("upperBodyPart3");
        pos(p3, -2.0F, -16.9F, -0.5F);
        rot(p3, (0.2F + 0.1F * base) * 180.0F, 0, 0);

        for (String h : new String[]{"head1", "head2", "head3"}) rot(bone(h), pitch, yaw, 0);

        float chin = Math.abs(sind(life * 360.0F)) > 0.85F ? chinOpen(e.hasTarget(), life) : 0.0F;
        rot(bone("chin"), chin, 0, 0);

        // part_visibility (is_baby=est.2, is_sheared=est.3, is_saddled=est.4, is_chested=est.5)
        int st = e.visualStage();
        hide(bone("head1_self"), st >= 3);   // head1: is_sheared == 0
        hide(bone("head"), st < 3);          // head + chin: is_sheared == 1
        hide(bone("mutantBody1"), st < 2);
        hide(bone("mutantBody2"), st < 4);
        hide(bone("mutantBody3"), st < 5);
        hide(bone("bone"), st < 2);          // blocos girando (is_baby)
        hide(bone("bone2"), st < 2);
    }

    private CoreGeoBone bone(String name) {
        return getAnimationProcessor().getBone(name);
    }
}
