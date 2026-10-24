package com.witherstorm.witherstormport.entity;

import com.witherstorm.witherstormport.config.WitherStormConfig;
import com.witherstorm.witherstormport.entity.ai.StormSkullAttackGoal;
import com.witherstorm.witherstormport.registry.ModEntities;
import com.witherstorm.witherstormport.registry.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/**
 * minecraft_storymode:wither_storm1 - fase de formacao.
 * Timeline animation.wither_storm1.stage -> estagios 1..5 + feixe + transformacao em wither_storm2.
 */
public class WitherStormStage1 extends AbstractWitherStorm {
    private static final RawAnimation SPIN = RawAnimation.begin().thenLoop("animation.wither_storm.block1");

    public WitherStormStage1(EntityType<? extends WitherStormStage1> type, Level level) {
        super(type, level, 50);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FLYING_SPEED, 0.4D)
                .add(Attributes.FOLLOW_RANGE, 128.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 2048.0D);
    }

    @Override protected float stormMaxHealth() { return 999_999.0F; }

    @Override
    protected StormSkullAttackGoal createAttackGoal() {
        // attack_interval_min 1s / max 1.5s, attack_radius 128
        return new StormSkullAttackGoal(this, 20, 30, 1, 0, 128.0D, 24.0D);
    }

    @Override protected SoundEvent spawnSound() { return ModSounds.WS_BGM.get(); }

    @Override
    protected void serverStormTick() {
        int t = stormAge;
        int stage = 1;
        if (t >= WitherStormConfig.ticks(WitherStormConfig.STAGE2_AT)) stage = 2;
        if (t >= WitherStormConfig.ticks(WitherStormConfig.STAGE3_AT)) stage = 3;
        if (t >= WitherStormConfig.ticks(WitherStormConfig.STAGE4_AT)) stage = 4;
        if (t >= WitherStormConfig.ticks(WitherStormConfig.STAGE5_AT)) stage = 5;
        setStage(stage);
        setPowered(t >= WitherStormConfig.ticks(WitherStormConfig.BEAM_AT));

        // animation.wither_storm.tractor (loop)
        StormMechanics.clearBlocksAround(this);
        StormMechanics.pullObjects(this, true);
        StormMechanics.consumeItems(this, 8.0D);
        // animation.wither_storm.tractor0 (loop de 30.1 s)
        StormMechanics.tickTractorSpawners(this);

        if (t >= WitherStormConfig.ticks(WitherStormConfig.TRANSFORM_AT)) {
            transformIntoFinalForm();
        }
    }

    /** event "into" -> minecraft:transformation into wither_storm2. */
    private void transformIntoFinalForm() {
        WitherStormEntity next = ModEntities.WITHER_STORM2.get().create(this.level());
        if (next == null) return;
        next.moveTo(getX(), getY(), getZ(), getYRot(), getXRot());
        if (hasCustomName()) next.setCustomName(getCustomName());
        this.level().addFreshEntity(next);
        this.discard();
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.WITHER_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.WITHER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.WITHER_DEATH; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "blocks", 0, state -> state.setAndContinue(SPIN)));
    }
}
