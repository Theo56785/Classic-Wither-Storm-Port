package com.witherstorm.witherstormport.entity;

import com.witherstorm.witherstormport.registry.ModEntities;
import com.witherstorm.witherstormport.registry.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import com.witherstorm.witherstormport.entity.ai.StormSkullAttackGoal;

/**
 * Base das formas "grandes" (wither_storm2, bigbrain, bigbrain2, severed).
 * Reune: area_attack, spell_effects (regeneration), damage_sensor "fatal", e (se fullMechanics) os scripts
 * animation.wither_storm.default / attack / tractor0.
 */
public abstract class FinalFormStorm extends AbstractWitherStorm {
    private final RawAnimation spin;

    protected FinalFormStorm(EntityType<? extends FinalFormStorm> type, Level level, String spinAnimation) {
        super(type, level, 10_000);
        this.spin = RawAnimation.begin().thenLoop(spinAnimation);
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

    /** Linha do tempo especifica da forma (flags, transformacoes, sons). */
    protected abstract void formTick(int t);

    /** Falso so no severed_witherstorm (scripts: apenas "stage"). */
    protected boolean fullMechanics() { return true; }

    /** Dano fatal: o padrao e virar o corpo (witherstorm_dead). Formas imortais sobrescrevem. */
    protected void onFatalDamage() { becomeCorpse(); }

    /** minecraft:transformation into witherstorm_dead (o corpo nao e uma AbstractWitherStorm, entao e criado aqui). */
    protected void becomeCorpse() {
        StormMechanics.discardAllTractors(this.level(), this.blockPosition());
        WitherStormDead dead = ModEntities.WITHER_STORM_DEAD.get().create(this.level());
        if (dead == null) return;
        dead.moveTo(getX(), getY(), getZ(), getYRot(), getXRot());
        if (hasCustomName()) dead.setCustomName(getCustomName());
        this.level().addFreshEntity(dead);
        this.discard();
    }

    @Override
    protected void serverStormTick() {
        formTick(stormAge);
        if (isRemoved()) return;

        if (fullMechanics()) {
            StormMechanics.clearBlocksAround(this);                 // animation.wither_storm.default
            StormMechanics.pullObjects(this, false);                // function tractor
            StormMechanics.killAround(this, 20.0D);
            StormMechanics.keepRain(this);
            if (StormMechanics.suctionActive(this) && stormAge % 4 == 0) {
                StormMechanics.suctionRoar(this, 128.0D);           // effect:tractor1 (10.0-15.0 s do ciclo)
            }
            StormMechanics.tickTractorSpawners(this);               // animation.wither_storm.tractor0
        }
        StormMechanics.areaAttack(this, 15.0D);                     // minecraft:area_attack

        if (stormAge % 5 == 0) {                                     // minecraft:spell_effects regeneration 8
            this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 10, 8, true, true));
        }
    }

    @Override
    protected boolean isImmuneTo(DamageSource source) {
        return super.isImmuneTo(source) || source.is(DamageTypes.LIGHTNING_BOLT);
    }

    /** damage_sensor has_damage "fatal". /kill, vazio etc. (bypass de invulnerabilidade) matam de verdade. */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isImmuneTo(source)) return false;
        if (!this.level().isClientSide && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && amount >= getHealth()) {
            onFatalDamage();
            return false;
        }
        return super.hurt(source, amount);
    }

    protected void playToAllPlayers(SoundEvent sound, float volume) {
        if (this.level() instanceof ServerLevel sl) {
            for (ServerPlayer p : sl.players()) p.playNotifySound(sound, SoundSource.HOSTILE, volume, 1.0F);
        }
    }

    @Override protected SoundEvent shootSound() { return ModSounds.WS_SHOOT.get(); }
    @Override protected SoundEvent getAmbientSound() { return ModSounds.WS_IDLE.get(); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return ModSounds.WS_HURT.get(); }
    @Override protected SoundEvent getDeathSound() { return ModSounds.WS_DEATH.get(); }
    @Override protected double cullingInflate() { return 60.0D; }

    @Override
    protected StormSkullAttackGoal createAttackGoal() {
        // burst_shots 6, burst_interval 0.1 s (2 ticks), attack_radius 64
        return new StormSkullAttackGoal(this, 30, 50, 6, 2, 64.0D, 48.0D);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "blocks", 0, state -> state.setAndContinue(spin)));
    }
}
