package com.witherstorm.witherstormport.entity;

import com.witherstorm.witherstormport.config.WitherStormConfig;
import com.witherstorm.witherstormport.entity.ai.StormSkullAttackGoal;
import com.witherstorm.witherstormport.registry.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/**
 * minecraft_storymode:wither_storm2 - forma final.
 * Reune: area_attack, knockback_roar do tractor1, scripts animation.wither_storm.default / attack / tractor0.
 */
public class WitherStormEntity extends AbstractWitherStorm {
    private static final RawAnimation SPIN = RawAnimation.begin().thenLoop("animation.wither_storm.block2");

    public WitherStormEntity(EntityType<? extends WitherStormEntity> type, Level level) {
        super(type, level, 10_000);
        setStage(5);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.02D)
                .add(Attributes.FLYING_SPEED, 0.02D)
                .add(Attributes.FOLLOW_RANGE, 128.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 2048.0D);
    }

    @Override protected float stormMaxHealth() { return 999_999_999.0F; }
    @Override protected double cullingInflate() { return 60.0D; }

    @Override
    protected StormSkullAttackGoal createAttackGoal() {
        // burst_shots 6, burst_interval 0.1 s (2 ticks), attack_radius 64
        return new StormSkullAttackGoal(this, 30, 50, 6, 2, 64.0D, 48.0D);
    }

    @Override protected SoundEvent spawnSound() { return ModSounds.WS_FINISHED.get(); }

    @Override
    protected void serverStormTick() {
        int t = stormAge;
        setStage(t >= WitherStormConfig.ticks(WitherStormConfig.STAGE6_AT) ? 6 : 5);

        if (!isPowered() && t >= WitherStormConfig.ticks(WitherStormConfig.COMPLETE_AT)) {
            setPowered(true);   // event "complete" (+ /playsound mob.ws.finished @a)
            if (this.level() instanceof ServerLevel sl) {
                for (ServerPlayer p : sl.players()) {
                    p.playNotifySound(ModSounds.WS_FINISHED.get(), SoundSource.HOSTILE, 5.0F, 1.0F);
                }
            }
        }

        // animation.wither_storm.default (cada tick)
        StormMechanics.clearBlocksAround(this);
        StormMechanics.pullObjects(this, false);
        StormMechanics.killAround(this, 20.0D);
        StormMechanics.keepThunderstorm(this);

        // minecraft:area_attack (damage_range 15, 999999999 por tick)
        StormMechanics.areaAttack(this, 15.0D);
        // effect:tractor1 -> minecraft:behavior.knockback_roar (cooldown 0, ~0.2 s)
        if (t % 4 == 0) StormMechanics.suctionRoar(this, 128.0D);

        // animation.wither_storm.attack: raios a cada 10 s
        if (t > 0 && t % 200 == 0) StormMechanics.lightningOnEverything(this);

        // animation.wither_storm.tractor0
        StormMechanics.tickTractorSpawners(this);
    }

    @Override
    protected boolean isImmuneTo(DamageSource source) {
        return super.isImmuneTo(source) || source.is(DamageTypes.LIGHTNING_BOLT);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        // animation.wither_storm.death: remove todos os tractors
        StormMechanics.discardAllTractors(this);
    }

    @Override protected SoundEvent getAmbientSound() { return ModSounds.WS_IDLE.get(); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return ModSounds.WS_HURT.get(); }
    @Override protected SoundEvent getDeathSound() { return ModSounds.WS_DEATH.get(); }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "blocks", 0, state -> state.setAndContinue(SPIN)));
    }
}
