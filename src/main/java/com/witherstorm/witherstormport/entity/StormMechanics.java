package com.witherstorm.witherstormport.entity;

import com.witherstorm.witherstormport.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Traducao dos comandos que o addon Bedrock executava dentro de animations/*.json e functions/*.mcfunction.
 *
 *   /fill ~-2 ~ ~-2 ~2 ~3 ~2 air          -> clearBlocksAround
 *   execute @e[...] tp ^ ^0.1 ^0.1 facing  -> pullObjects        (tractor.mcfunction)
 *   /kill @e[r=8,type=item]                -> consumeItems
 *   /kill @e[type=!...,r=20]               -> killAround
 *   /weather thunder 100                   -> keepThunderstorm
 *   execute @e ... summon lightning_bolt   -> lightningOnEverything
 *   /summon effect:tractor0 ...            -> tickTractorSpawners
 *   area_attack / knockback_roar           -> areaAttack / suctionRoar
 */
public final class StormMechanics {
    private static final int MAX_BOLTS_PER_CYCLE = 128;   // trava de seguranca do servidor
    private static final int SPAWNER_CYCLE_TICKS = 602;   // animation_length 30.1 s

    /** Tudo que o addon trata como "lado da tempestade" nos filtros type=!... */
    public static boolean isStormSide(Entity e) {
        return e instanceof StormFamily || e instanceof TractorDebris || e instanceof WitherStormSkull;
    }

    // ---------------------------------------------------------------- /fill ... air
    public static void clearBlocksAround(AbstractWitherStorm s) {
        Level level = s.level();
        BlockPos o = s.blockPosition();
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = 0; dy <= 3; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    p.set(o.getX() + dx, o.getY() + dy, o.getZ() + dz);
                    BlockState st = level.getBlockState(p);
                    if (st.isAir() || st.getDestroySpeed(level, p) < 0.0F) continue; // preserva bedrock
                    level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }

    // ---------------------------------------------------------------- tp ^ ^0.1 ^0.1 facing <storm>
    public static void pullObjects(AbstractWitherStorm s, boolean itemsOnly) {
        final double r = 128.0D;
        Vec3 c = s.position();
        AABB box = new AABB(s.blockPosition()).inflate(r);
        for (Entity e : s.level().getEntitiesOfClass(Entity.class, box, x -> isPullable(x, itemsOnly))) {
            if (e.distanceToSqr(c) > r * r) continue;
            Vec3 d = c.subtract(e.position());
            if (d.lengthSqr() < 1.0E-4D) continue;
            Vec3 np = e.position().add(0.0D, 0.1D, 0.0D).add(d.normalize().scale(0.1D));
            e.setPos(np.x, np.y, np.z);
            e.setDeltaMovement(Vec3.ZERO);
            e.hurtMarked = true;
        }
    }

    private static boolean isPullable(Entity e, boolean itemsOnly) {
        if (e instanceof ItemEntity) return true;
        if (itemsOnly) return false;
        if (e instanceof WitherStormSkull) return false;
        return e instanceof Projectile || e instanceof EyeOfEnder || e instanceof EnderDragon;
    }

    public static void consumeItems(AbstractWitherStorm s, double radius) {
        AABB box = new AABB(s.blockPosition()).inflate(radius);
        for (ItemEntity item : s.level().getEntitiesOfClass(ItemEntity.class, box,
                i -> i.distanceToSqr(s) <= radius * radius)) {
            item.discard();
        }
    }

    // ---------------------------------------------------------------- /kill @e[type=!...,r=20]
    public static void killAround(AbstractWitherStorm s, double radius) {
        AABB box = new AABB(s.blockPosition()).inflate(radius);
        for (Entity e : s.level().getEntitiesOfClass(Entity.class, box,
                x -> !isStormSide(x) && x.distanceToSqr(s) <= radius * radius)) {
            e.kill();   // inclui jogadores (inclusive criativo), como o /kill do Bedrock
        }
    }

    // ---------------------------------------------------------------- /weather thunder 100
    public static void keepThunderstorm(AbstractWitherStorm s) {
        if (s.getStormAge() % 20 == 0 && s.level() instanceof ServerLevel sl) {
            sl.setWeatherParameters(0, 2000, true, true);   // 100 s, renovado a cada segundo
        }
    }

    // ---------------------------------------------------------------- minecraft:area_attack
    public static void areaAttack(AbstractWitherStorm s, double range) {
        AABB box = s.getBoundingBox().inflate(range);
        for (LivingEntity e : s.level().getEntitiesOfClass(LivingEntity.class, box,
                x -> !isStormSide(x))) {
            e.hurt(s.damageSources().mobAttack(s), 999_999_999.0F);
        }
    }

    // ---------------------------------------------------------------- effect:tractor1 (knockback_roar -3)
    public static void suctionRoar(AbstractWitherStorm s, double range) {
        Vec3 c = s.position();
        AABB box = new AABB(s.blockPosition()).inflate(range);
        for (LivingEntity e : s.level().getEntitiesOfClass(LivingEntity.class, box,
                x -> !isStormSide(x) && x.distanceToSqr(s) <= range * range)) {
            // knockback_filters: nao-jogadores OU jogadores em modo sobrevivencia (tag survival_mode)
            boolean pull = !(e instanceof Player p) || (!p.isCreative() && !p.isSpectator());
            if (pull) {
                Vec3 dir = c.subtract(e.position()).normalize();
                e.setDeltaMovement(e.getDeltaMovement().add(dir.scale(0.5D)));
                e.hurtMarked = true;
            }
            e.hurt(s.damageSources().mobAttack(s), 5.0F);   // knockback_damage 5
        }
    }

    // ---------------------------------------------------------------- raios em todas as entidades
    public static void lightningOnEverything(AbstractWitherStorm s) {
        if (!(s.level() instanceof ServerLevel sl)) return;
        List<BlockPos> spots = new ArrayList<>();
        for (Entity e : sl.getAllEntities()) {
            if (isStormSide(e) || e instanceof TractorSpawner || e instanceof LightningBolt) continue;
            spots.add(e.blockPosition());
            if (spots.size() >= MAX_BOLTS_PER_CYCLE) break;
        }
        for (BlockPos pos : spots) {
            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(sl);
            if (bolt == null) continue;
            bolt.moveTo(Vec3.atBottomCenterOf(pos));
            sl.addFreshEntity(bolt);
        }
    }

    // ---------------------------------------------------------------- /summon effect:tractor0
    public static void tickTractorSpawners(AbstractWitherStorm s) {
        int t = s.getStormAge() % SPAWNER_CYCLE_TICKS;
        if (t != 1 && t != 101) return;                  // timeline: 0.0 s e 5.0 s
        if (!(s.level() instanceof ServerLevel sl)) return;
        int[][] offsets = {{0, 10}, {10, 0}, {10, 10}};
        for (int[] o : offsets) {
            TractorSpawner sp = ModEntities.TRACTOR0.get().create(sl);
            if (sp == null) continue;
            double x = s.getX() + o[0];
            double z = s.getZ() + o[1];
            sp.setPos(x, surfaceY(sl, x, z), z);
            sl.addFreshEntity(sp);
        }
    }

    public static void discardAllTractors(AbstractWitherStorm s) {
        AABB box = new AABB(s.blockPosition()).inflate(400.0D);
        for (Entity e : s.level().getEntitiesOfClass(Entity.class, box,
                x -> x instanceof TractorDebris || x instanceof TractorSpawner)) {
            e.discard();
        }
    }

    public static double surfaceY(Level level, double x, double z) {
        return level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(x), Mth.floor(z));
    }

    private StormMechanics() {}
}
