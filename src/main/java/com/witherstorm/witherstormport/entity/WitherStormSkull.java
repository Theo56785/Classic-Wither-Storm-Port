package com.witherstorm.witherstormport.entity;

import com.witherstorm.witherstormport.registry.ModEntities;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * minecraft_storymode:witherstorm_skull
 * projectile: impact_damage 131313, power 1.5, gravity 0, mob_effect wither (amp 100),
 * explode power 3 com fogo, damage_sensor "all" cancelado (invulneravel).
 */
public class WitherStormSkull extends AbstractHurtingProjectile implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public WitherStormSkull(EntityType<? extends WitherStormSkull> type, Level level) {
        super(type, level);
    }

    public WitherStormSkull(Level level, LivingEntity owner, double dx, double dy, double dz) {
        super(ModEntities.SKULL.get(), owner, dx, dy, dz, level);
        // velocidade constante (o disparador define o deltaMovement); sem aceleracao nem arrasto
        this.xPower = 0.0D;
        this.yPower = 0.0D;
        this.zPower = 0.0D;
    }

    @Override protected float getInertia() { return 1.0F; }
    @Override protected boolean shouldBurn() { return false; }
    @Override public boolean isPickable() { return false; }
    @Override public boolean hurt(DamageSource source, float amount) { return false; }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.tickCount > 200) this.discard();
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return !StormMechanics.isStormSide(target) && super.canHitEntity(target);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide) return;
        Entity target = result.getEntity();
        Entity owner = this.getOwner();
        target.hurt(this.damageSources().mobProjectile(this, owner instanceof LivingEntity l ? l : null), 131_313.0F);
        if (target instanceof LivingEntity living) {
            int ticks = switch (this.level().getDifficulty()) {   // durationeasy / normal / hard
                case EASY -> 1000;
                case NORMAL -> 2000;
                case HARD -> 8000;
                default -> 0;
            };
            if (ticks > 0) living.addEffect(new MobEffectInstance(MobEffects.WITHER, ticks, 100), owner);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.level().explode(this, getX(), getY(), getZ(), 3.0F, true, Level.ExplosionInteraction.MOB);
            this.discard();
        }
    }

    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return geoCache; }
}
