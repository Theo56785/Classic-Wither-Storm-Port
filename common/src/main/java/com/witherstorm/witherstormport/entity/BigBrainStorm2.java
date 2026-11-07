package com.witherstorm.witherstormport.entity;

import com.witherstorm.witherstormport.config.WitherStormConfig;
import com.witherstorm.witherstormport.registry.ModEntities;
import com.witherstorm.witherstormport.registry.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * minecraft_storymode:bigbrain_witherstorm2: invoca 2 severed_witherstorm (0 s), "head" em 120 s (cabecas 2 e 3).
 * Forma final: o dano fatal e cancelado (deals_damage false) - so /kill a elimina.
 */
public class BigBrainStorm2 extends FinalFormStorm {
    public BigBrainStorm2(EntityType<? extends BigBrainStorm2> type, Level level) {
        super(type, level, "animation.wither_storm.block4");
        setPowered(true);
    }

    @Override protected float stormMaxHealth() { return 999_999_999.0F; }
    @Override protected SoundEvent spawnSound() { return null; }
    @Override public SoundEvent musicEvent() { return ModSounds.WS_MUSIC3.get(); }

    @Override
    protected void formTick(int t) {
        if (t == 1) {                                        // /summon severed_witherstorm (x2)
            for (int i = 0; i < 2; i++) {
                SeveredStorm s = ModEntities.SEVERED.get().create(this.level());
                if (s == null) continue;
                s.moveTo(getX(), getY(), getZ(), this.random.nextFloat() * 360.0F, 0.0F);
                this.level().addFreshEntity(s);
            }
        }
        if (getStage() < 6 && t >= WitherStormConfig.ticks(WitherStormConfig.get().headAt)) {
            setStage(6);
            playToAllPlayers(ModSounds.SWS_HEAD.get(), 5.0F);
        }
    }

    private int getStage() { return visualStage(); }

    @Override
    protected void onFatalDamage() {
        StormMechanics.discardAllTractors(this.level(), this.blockPosition());   // animation.wither_storm.death
    }
}
