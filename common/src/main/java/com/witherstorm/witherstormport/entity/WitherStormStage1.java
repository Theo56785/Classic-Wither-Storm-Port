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
 * minecraft_storymode:wither_storm1 - fase de formacao (animation.wither_storm1.stage, 289 s na V2).
 * stage2 (is_chested) -> stage3 (is_sheared) -> stage4 (is_saddled) -> beam (is_charged) -> stage5 (is_baby) -> into.
 */
public class WitherStormStage1 extends AbstractWitherStorm {
    private static final RawAnimation SPIN = RawAnimation.begin().thenLoop("animation.wither_storm.block1");
    private static final RawAnimation RISE = RawAnimation.begin().thenPlay("animation.wither_command.spawn");

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
        return new StormSkullAttackGoal(this, 20, 30, 1, 0, 128.0D, 24.0D);   // 1.0-1.5 s, raio 128
    }

    @Override protected SoundEvent spawnSound() { return ModSounds.WS_BGM.get(); }

    @Override
    protected void serverStormTick() {
        WitherStormConfig c = WitherStormConfig.get();
        int t = stormAge;
        int stage = 1;
        if (t >= WitherStormConfig.ticks(c.stage2At)) stage = 2;
        if (t >= WitherStormConfig.ticks(c.stage3At)) stage = 3;
        if (t >= WitherStormConfig.ticks(c.stage4At)) stage = 4;
        if (t >= WitherStormConfig.ticks(c.stage5At)) stage = 5;
        setStage(stage);
        setPowered(t >= WitherStormConfig.ticks(c.beamAt));

        StormMechanics.clearBlocksAround(this);               // animation.wither_storm.tractor
        StormMechanics.pullObjects(this, true);
        StormMechanics.consumeItems(this, 8.0D);
        StormMechanics.tickTractorSpawners(this);             // animation.wither_storm.tractor0

        if (t >= WitherStormConfig.ticks(c.transformAt)) {    // event "into" (wither_storm_full -> wither_storm2)
            transformInto(ModEntities.WITHER_STORM2.get());
        }
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.WITHER_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return ModSounds.WC_HURT.get(); }
    @Override protected SoundEvent getDeathSound() { return ModSounds.WC_DEATH.get(); }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "blocks", 0, state -> state.setAndContinue(SPIN)));
        controllers.add(new AnimationController<>(this, "rise", 0, state -> state.setAndContinue(RISE)));
    }
}
