package com.witherstorm.witherstormport.entity;

import com.witherstorm.witherstormport.entity.ai.StormSkullAttackGoal;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Base comum das duas formas da Wither Storm.
 * Traduz: minecraft:boss, minecraft:health (limite > 1024), damage_sensor, shooter/ranged_attack,
 * persistent, fire_immune, nameable e o conjunto de behaviors de voo/alvo.
 */
public abstract class AbstractWitherStorm extends Monster implements GeoEntity, RangedAttackMob, StormFamily, StormVisual {
    private static final EntityDataAccessor<Integer> DATA_STAGE =
            SynchedEntityData.defineId(AbstractWitherStorm.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_POWERED =
            SynchedEntityData.defineId(AbstractWitherStorm.class, EntityDataSerializers.BOOLEAN);
    /** Vida propria: o atributo MAX_HEALTH vanilla e limitado a 1024 e getMaxHealth() e final. */
    private static final EntityDataAccessor<Float> DATA_STORM_HEALTH =
            SynchedEntityData.defineId(AbstractWitherStorm.class, EntityDataSerializers.FLOAT);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private final ServerBossEvent bossEvent =
            new ServerBossEvent(bossName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);

    /** Idade propria (persistida em NBT) usada pelas timelines; tickCount nao e salvo. */
    protected int stormAge;

    protected AbstractWitherStorm(EntityType<? extends AbstractWitherStorm> type, Level level, int xp) {
        super(type, level);
        this.xpReward = xp;
        this.moveControl = new FlyingMoveControl(this, 10, true);
        this.setNoGravity(true);
        this.setPersistenceRequired();
        this.setHealth(stormMaxHealth());
    }

    private static Component bossName() {
        Component bars = Component.literal("::::::::::::::::::::")
                .withStyle(ChatFormatting.BOLD, ChatFormatting.OBFUSCATED, ChatFormatting.DARK_PURPLE);
        return Component.literal("")
                .append(bars)
                .append(Component.literal("Wither Storm").withStyle(ChatFormatting.LIGHT_PURPLE))
                .append(bars.copy());
    }

    // ------------------------------------------------------------------ dados
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_STAGE, 1);
        this.entityData.define(DATA_POWERED, Boolean.FALSE);
        this.entityData.define(DATA_STORM_HEALTH, 1.0F);
    }

    @Override public int visualStage() { return this.entityData.get(DATA_STAGE); }
    protected void setStage(int stage) { if (visualStage() != stage) this.entityData.set(DATA_STAGE, stage); }
    @Override public boolean isPowered() { return this.entityData.get(DATA_POWERED); }
    protected void setPowered(boolean v) { if (isPowered() != v) this.entityData.set(DATA_POWERED, v); }
    @Override public float stormSeconds(float partialTick) { return (this.tickCount + partialTick) / 20.0F; }
    @Override public boolean hasTarget() { return this.isAggressive(); }
    public int getStormAge() { return stormAge; }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("StormAge", stormAge);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        stormAge = tag.getInt("StormAge");
    }

    // ------------------------------------------------------------------ vida (> 1024)
    /**
     * O atributo MAX_HEALTH e limitado a 1024 (RangedAttribute) e LivingEntity#getMaxHealth() e final.
     * Por isso getHealth()/setHealth() (nao-finais, usados por hurt/heal/die/NBT) passam a usar um
     * float proprio sincronizado, permitindo 999.999 / 999.999.999.
     */
    protected abstract float stormMaxHealth();

    @Override
    public float getHealth() {
        return this.entityData.get(DATA_STORM_HEALTH);
    }

    @Override
    public void setHealth(float health) {
        this.entityData.set(DATA_STORM_HEALTH, Mth.clamp(health, 0.0F, stormMaxHealth()));
    }

    // ------------------------------------------------------------------ IA
    protected abstract StormSkullAttackGoal createAttackGoal();

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, createAttackGoal());
        this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        // nearest_attackable_target: tudo que NAO e witherstorm nem "inanimate"; must_see = true
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, true, false,
                e -> !(e instanceof StormFamily) && !(e instanceof ArmorStand)));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(true);
        nav.setCanPassDoors(true);
        return nav;
    }

    // ------------------------------------------------------------------ tick / timelines
    protected abstract void serverStormTick();
    protected abstract SoundEvent spawnSound();

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            stormAge++;
            if (stormAge == 1) {
                // events.spawn do sounds.json (volume 1000)
                this.level().playSound(null, getX(), getY(), getZ(), spawnSound(), SoundSource.HOSTILE, 1000.0F, 1.0F);
            }
            serverStormTick();
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(Mth.clamp(getHealth() / stormMaxHealth(), 0.0F, 1.0F));
        this.setAggressive(this.getTarget() != null);
    }

    // ------------------------------------------------------------------ dano
    /** damage_sensor: imune a si mesma, a explosoes; a forma final tambem a raios. */
    protected boolean isImmuneTo(DamageSource source) {
        return source.getEntity() instanceof StormFamily
                || source.getDirectEntity() instanceof StormFamily
                || source.is(DamageTypeTags.IS_EXPLOSION);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isImmuneTo(source)) return false;
        return super.hurt(source, amount);
    }

    // ------------------------------------------------------------------ ataque a distancia
    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        Vec3 from = new Vec3(getX(), getY() + getBbHeight() * 0.8D, getZ());
        Vec3 dir = new Vec3(target.getX() - from.x, target.getY(0.5D) - from.y, target.getZ() - from.z).normalize();
        WitherStormSkull skull = new WitherStormSkull(this.level(), this, dir.x, dir.y, dir.z);
        skull.setPos(from.x, from.y, from.z);
        skull.setDeltaMovement(dir.scale(1.5D));   // projectile.power = 1.5, gravity = 0
        this.level().addFreshEntity(skull);
        this.playSound(SoundEvents.WITHER_SHOOT, 10.0F, 1.0F);
    }

    // ------------------------------------------------------------------ boss bar
    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    // ------------------------------------------------------------------ diversos
    @Override protected float getSoundVolume() { return 100.0F; }
    @Override protected boolean shouldDespawnInPeaceful() { return false; }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public void checkDespawn() { /* minecraft:persistent / never_despawn */ }
    @Override public boolean canChangeDimensions() { return false; }
    @Override public boolean causeFallDamage(float f, float m, DamageSource s) { return false; }
    @Override public boolean isPushedByFluid() { return false; }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.85F;
    }

    /** A malha (escala 6) passa muito da hitbox 2.5 x 35: amplia a caixa usada no frustum culling. */
    @Override
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(cullingInflate(), 4.0D, cullingInflate());
    }

    protected double cullingInflate() { return 8.0D; }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
