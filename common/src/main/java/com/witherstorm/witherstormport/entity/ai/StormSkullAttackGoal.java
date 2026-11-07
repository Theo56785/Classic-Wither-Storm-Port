package com.witherstorm.witherstormport.entity.ai;

import com.witherstorm.witherstormport.entity.AbstractWitherStorm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * minecraft:behavior.ranged_attack (+ burst_shots / burst_interval da forma final).
 * Tambem aproxima a tempestade do alvo (substitui wither_random_attack_pos_goal).
 */
public class StormSkullAttackGoal extends Goal {
    private final AbstractWitherStorm storm;
    private final int minCooldown;
    private final int maxCooldown;
    private final int burstShots;
    private final int burstGap;
    private final double range;
    private final double closeIn;

    private int cooldown;
    private int shotsLeft;
    private int burstCooldown;

    public StormSkullAttackGoal(AbstractWitherStorm storm, int minCooldown, int maxCooldown,
                                int burstShots, int burstGap, double range, double closeIn) {
        this.storm = storm;
        this.minCooldown = minCooldown;
        this.maxCooldown = maxCooldown;
        this.burstShots = burstShots;
        this.burstGap = burstGap;
        this.range = range;
        this.closeIn = closeIn;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity t = storm.getTarget();
        return t != null && t.isAlive();
    }

    @Override
    public void start() {
        cooldown = nextCooldown();
        shotsLeft = 0;
    }

    @Override
    public void stop() {
        shotsLeft = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = storm.getTarget();
        if (target == null) return;

        storm.getLookControl().setLookAt(target, 30.0F, 30.0F);
        double dist2 = storm.distanceToSqr(target);
        if (dist2 > closeIn * closeIn) {
            storm.getMoveControl().setWantedPosition(target.getX(), target.getY() + 4.0D, target.getZ(), 1.0D);
        }

        if (shotsLeft > 0) {
            if (--burstCooldown <= 0) {
                storm.performRangedAttack(target, 1.0F);
                shotsLeft--;
                burstCooldown = burstGap;
            }
            return;
        }

        if (dist2 > range * range || !storm.getSensing().hasLineOfSight(target)) return;
        if (--cooldown <= 0) {
            shotsLeft = burstShots;
            burstCooldown = 0;
            cooldown = nextCooldown();
        }
    }

    private int nextCooldown() {
        return minCooldown + storm.getRandom().nextInt(Math.max(1, maxCooldown - minCooldown + 1));
    }
}
