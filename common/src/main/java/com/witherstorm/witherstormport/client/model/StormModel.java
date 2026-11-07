package com.witherstorm.witherstormport.client.model;

import com.witherstorm.witherstormport.WitherStormPort;
import com.witherstorm.witherstormport.entity.AbstractWitherStorm;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import static com.witherstorm.witherstormport.client.model.StormBones.*;

/**
 * Modelo unico das formas finais. As 4 geometrias so diferem no sufixo dos ossos
 * (wither_storm: head1v2/chin1; bigbrain: *v4; severed: *v3) e nas regras de visibilidade.
 */
public class StormModel<T extends AbstractWitherStorm> extends GeoModel<T> {
    /** Regra de visibilidade das cabecas 2 e 3 (e do osso mutantBody0). */
    public enum Heads { WITH_FIRST, NEED_HEAD_PHASE }
    public enum Body { NONE, WITH_FIRST, STAGE6 }

    private final String name;           // geo/anim: <name>.geo.json
    private final String headSuffix;     // "v2" | "v4" | "v3"
    private final String chinSuffix;     // "" | "v4" | "v3"
    private final String mutantBone;     // nullable
    private final Heads heads;
    private final Body body;

    public StormModel(String name, String headSuffix, String chinSuffix, String mutantBone, Heads heads, Body body) {
        this.name = name;
        this.headSuffix = headSuffix;
        this.chinSuffix = chinSuffix;
        this.mutantBone = mutantBone;
        this.heads = heads;
        this.body = body;
    }

    @Override public ResourceLocation getModelResource(T e) {
        return new ResourceLocation(WitherStormPort.MODID, "geo/entity/" + name + ".geo.json");
    }
    @Override public ResourceLocation getTextureResource(T e) {
        return new ResourceLocation(WitherStormPort.MODID, "textures/entity/wither_storm.png");
    }
    @Override public ResourceLocation getAnimationResource(T e) {
        return new ResourceLocation(WitherStormPort.MODID, "animations/entity/" + name + ".animation.json");
    }

    @Override
    public void setCustomAnimations(T e, long instanceId, AnimationState<T> state) {
        super.setCustomAnimations(e, instanceId, state);
        float pt = state.getPartialTick();
        float life = e.stormSeconds(pt);
        float yaw = headYaw(e, pt);
        float pitch = headPitch(e, pt) + (e.hasTarget() ? 0.0F : -15.0F);

        pos(bone("body0" + (name.equals("wither_storm") ? "" : headSuffix)), 0.0F, 3.0F, -13.3F);

        for (int i = 1; i <= 3; i++) {
            CoreGeoBone h = bone("head" + i + headSuffix);
            rot(h, pitch, yaw, 0);
            scale(h, 0.73F, 0.73F, 0.73F);
            pos(h, 0.0F, i == 1 ? -10.0F : 10.0F, i == 1 ? 0.0F : 3.5F);
        }
        float open = chinOpen(e.hasTarget(), life);
        rot(bone("chin1" + chinSuffix), Math.abs(sind(life * 360.0F)) > 0.85F ? open : 0.0F, 0, 0);
        rot(bone("chin2" + chinSuffix), Math.abs(sind(life * 360.0F)) < 0.85F ? open : 0.0F, 0, 0);
        rot(bone("chin3" + chinSuffix), Math.abs(cosd(life * 360.0F)) > 0.85F ? open : 0.0F, 0, 0);

        boolean first = life > 4.0F || e.isPowered();          // query.anim_time>4 || is_powered
        boolean head = e.visualStage() >= 6;                   // is_saddled / is_chested
        boolean show2 = heads == Heads.WITH_FIRST ? first : head;
        hide(bone("head1" + headSuffix), !first);
        hide(bone("chin1" + chinSuffix), !first);
        for (int i = 2; i <= 3; i++) {
            hide(bone("head" + i + headSuffix), !show2);
            hide(bone("chin" + i + chinSuffix), !show2);
        }
        if (mutantBone != null && body != Body.NONE) hide(bone(mutantBone), body == Body.STAGE6 ? !head : !first);
    }

    private CoreGeoBone bone(String n) { return getAnimationProcessor().getBone(n); }

    public String modelName() { return name; }
}
