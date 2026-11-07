package com.witherstorm.witherstormport.entity;

import com.witherstorm.witherstormport.config.WitherStormConfig;
import com.witherstorm.witherstormport.registry.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * minecraft_storymode:witherstorm_dead - corpo da tempestade. damage_sensor "all" cancelado, vida 1.
 * animation.wither_storm.respawn: remove os tractors (0 s) e, em 49 s, vira bigbrain_witherstorm2.
 */
public class WitherStormDead extends Mob implements GeoEntity, StormFamily {
    private static final RawAnimation COLLAPSE = RawAnimation.begin().thenPlayAndHold("animation.witherstorm_dead.bone");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private int age;

    public WitherStormDead(EntityType<? extends WitherStormDead> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 1.0D).add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;
        age++;
        if (age == 1) StormMechanics.discardAllTractors(this.level(), this.blockPosition());
        if (age >= WitherStormConfig.ticks(WitherStormConfig.get().deadRespawnAt)) {
            BigBrainStorm2 next = ModEntities.BIGBRAIN2.get().create(this.level());
            if (next != null) {
                next.moveTo(getX(), getY(), getZ(), getYRot(), getXRot());
                this.level().addFreshEntity(next);
            }
            this.discard();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && super.hurt(source, amount);
    }

    @Override public boolean isPushable() { return false; }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public void checkDespawn() {}
    @Override public boolean canChangeDimensions() { return false; }
    @Override public AABB getBoundingBoxForCulling() { return this.getBoundingBox().inflate(40.0D, 10.0D, 40.0D); }

    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putInt("DeadAge", age); }
    @Override public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); age = tag.getInt("DeadAge"); }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "collapse", 0, state -> state.setAndContinue(COLLAPSE)));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return geoCache; }
}
