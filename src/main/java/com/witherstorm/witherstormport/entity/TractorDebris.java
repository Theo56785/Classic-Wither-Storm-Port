package com.witherstorm.witherstormport.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * effect:tractor - pedaco de grama/terra (variant 1/2 -> 0/1) que voa ate a tempestade.
 * animation.tractor: /tp ~ ~-1 ~ e /setblock ~ ~ ~ air no nascimento.
 * explode (poder 0) ao chegar = simplesmente desaparece.
 */
public class TractorDebris extends Entity implements GeoEntity {
    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(TractorDebris.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private int age;

    public TractorDebris(EntityType<? extends TractorDebris> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
        this.entityData.set(DATA_VARIANT, this.random.nextInt(2));   // randomize 50/50 grass|soil
    }

    public int getVariant() { return this.entityData.get(DATA_VARIANT); }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;
        age++;

        if (age == 1) {
            this.setPos(getX(), getY() - 1.0D, getZ());
            BlockPos p = this.blockPosition();
            BlockState st = this.level().getBlockState(p);
            if (!st.isAir() && st.getDestroySpeed(this.level(), p) >= 0.0F) {
                this.level().setBlock(p, Blocks.AIR.defaultBlockState(), 3);
            }
        }

        AbstractWitherStorm storm = nearestStorm();
        if (storm == null) {
            if (age > 100) this.discard();
            return;
        }
        Vec3 to = storm.position().add(0.0D, 2.0D, 0.0D).subtract(this.position());
        double dist = to.length();
        // tag "clean" (r=5) ou contato (target_nearby_sensor 0.5) -> explode (poder 0) -> some
        if (dist < 5.0D || storm.getBoundingBox().inflate(0.5D).contains(this.position())) {
            this.discard();
            return;
        }
        this.setPos(this.position().add(to.normalize().scale(Math.min(0.9D, dist))));
    }

    private AbstractWitherStorm nearestStorm() {
        AbstractWitherStorm best = null;
        double bestD = Double.MAX_VALUE;
        for (AbstractWitherStorm s : this.level().getEntitiesOfClass(AbstractWitherStorm.class,
                this.getBoundingBox().inflate(256.0D))) {
            double d = s.distanceToSqr(this);
            if (d < bestD) { bestD = d; best = s; }
        }
        return best;
    }

    @Override protected void defineSynchedData() { this.entityData.define(DATA_VARIANT, 0); }
    @Override protected void readAdditionalSaveData(CompoundTag tag) { this.entityData.set(DATA_VARIANT, tag.getInt("Variant")); }
    @Override protected void addAdditionalSaveData(CompoundTag tag) { tag.putInt("Variant", getVariant()); }
    @Override public boolean isInvulnerableTo(DamageSource source) { return true; }   // damage_sensor all
    @Override public boolean isPickable() { return false; }
    @Override public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }

    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return geoCache; }
}
