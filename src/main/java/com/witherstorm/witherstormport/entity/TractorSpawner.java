package com.witherstorm.witherstormport.entity;

import com.witherstorm.witherstormport.registry.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

/**
 * effect:tractor0 (invisivel). minecraft:behavior.summon_entity: circulo (5, size 5) + linha (5, size 10)
 * de effect:tractor a cada 1.3 s; animation.random = /spreadplayers ~ ~ 10 15.
 */
public class TractorSpawner extends Entity implements StormFamily {
    private int age;

    public TractorSpawner(EntityType<? extends TractorSpawner> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;
        age++;

        if (age == 1) scatter();
        if (age > 20 && (age - 20) % 26 == 0) cast();                 // base_delay 1.0 s, cooldown 1.3 s
        if (age >= 1200) this.discard();                              // evita acumulo infinito
        if (age % 40 == 0 && this.level().getEntitiesOfClass(AbstractWitherStorm.class,
                this.getBoundingBox().inflate(256.0D)).isEmpty()) this.discard();
    }

    /** /spreadplayers ~ ~ 10 15 @s */
    private void scatter() {
        double angle = this.random.nextDouble() * Math.PI * 2.0D;
        double dist = 10.0D + this.random.nextDouble() * 5.0D;
        double x = getX() + Math.cos(angle) * dist;
        double z = getZ() + Math.sin(angle) * dist;
        this.setPos(x, StormMechanics.surfaceY(this.level(), x, z), z);
        this.setYRot(this.random.nextFloat() * 360.0F);
    }

    private void cast() {
        if (!(this.level() instanceof ServerLevel sl)) return;
        if (sl.getEntitiesOfClass(TractorDebris.class, this.getBoundingBox().inflate(64.0D)).size() > 150) return;

        for (int i = 0; i < 5; i++) {                                  // shape: circle
            double a = i * (Math.PI * 2.0D / 5.0D);
            spawnDebris(sl, getX() + Math.cos(a) * 5.0D, getZ() + Math.sin(a) * 5.0D);
        }
        double yaw = Math.toRadians(getYRot());                        // shape: line
        for (int i = 1; i <= 5; i++) {
            spawnDebris(sl, getX() - Math.sin(yaw) * i * 2.0D, getZ() + Math.cos(yaw) * i * 2.0D);
        }
    }

    private void spawnDebris(ServerLevel sl, double x, double z) {
        TractorDebris d = ModEntities.TRACTOR.get().create(sl);
        if (d == null) return;
        d.setPos(x, StormMechanics.surfaceY(sl, x, z), z);
        sl.addFreshEntity(d);
    }

    @Override protected void defineSynchedData() {}
    @Override protected void readAdditionalSaveData(CompoundTag tag) { age = tag.getInt("Age"); }
    @Override protected void addAdditionalSaveData(CompoundTag tag) { tag.putInt("Age", age); }
    @Override public boolean isInvulnerableTo(DamageSource source) { return true; }
    @Override public boolean isPickable() { return false; }
    @Override public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
}
