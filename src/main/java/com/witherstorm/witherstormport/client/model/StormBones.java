package com.witherstorm.witherstormport.client.model;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;

/** Utilitarios que substituem as expressoes Molang (query.* / variable.*) das animacoes Bedrock. */
public final class StormBones {
    private static final float DEG = (float) (Math.PI / 180.0D);

    /** math.sin / math.cos do Molang trabalham em GRAUS. */
    public static float sind(float deg) { return Mth.sin(deg * DEG); }
    public static float cosd(float deg) { return Mth.cos(deg * DEG); }

    /** query.target_y_rotation */
    public static float headYaw(LivingEntity e, float pt) {
        return Mth.rotLerp(pt, e.yHeadRotO, e.yHeadRot) - Mth.rotLerp(pt, e.yBodyRotO, e.yBodyRot);
    }

    /** query.target_x_rotation */
    public static float headPitch(LivingEntity e, float pt) {
        return Mth.lerp(pt, e.xRotO, e.getXRot());
    }

    /** variable.chin_open */
    public static float chinOpen(boolean hasTarget, float life) {
        return hasTarget ? Math.abs(sind(life * 250.0F)) * 45.0F : 0.0F;
    }

    public static void rot(CoreGeoBone b, float xDeg, float yDeg, float zDeg) {
        if (b == null) return;
        b.setRotX(xDeg * DEG);
        b.setRotY(yDeg * DEG);
        b.setRotZ(zDeg * DEG);
    }

    public static void pos(CoreGeoBone b, float x, float y, float z) {
        if (b == null) return;
        b.setPosX(x);
        b.setPosY(y);
        b.setPosZ(z);
    }

    public static void scale(CoreGeoBone b, float x, float y, float z) {
        if (b == null) return;
        b.setScaleX(x);
        b.setScaleY(y);
        b.setScaleZ(z);
    }

    /** part_visibility do render controller. */
    public static void hide(CoreGeoBone b, boolean hidden) {
        if (b != null) b.setHidden(hidden);
    }

    /** Copia a pose (rot/pos/escala) dos ossos de mesmo nome de {@code from} para {@code to}. */
    public static void copyPose(BakedGeoModel from, BakedGeoModel to) {
        for (GeoBone b : to.topLevelBones()) copyBone(from, b);
    }

    private static void copyBone(BakedGeoModel from, GeoBone target) {
        from.getBone(target.getName()).ifPresent(src -> {
            target.setRotX(src.getRotX());
            target.setRotY(src.getRotY());
            target.setRotZ(src.getRotZ());
            target.setPosX(src.getPosX());
            target.setPosY(src.getPosY());
            target.setPosZ(src.getPosZ());
            target.setScaleX(src.getScaleX());
            target.setScaleY(src.getScaleY());
            target.setScaleZ(src.getScaleZ());
        });
        for (GeoBone child : target.getChildBones()) copyBone(from, child);
    }

    private StormBones() {}
}
