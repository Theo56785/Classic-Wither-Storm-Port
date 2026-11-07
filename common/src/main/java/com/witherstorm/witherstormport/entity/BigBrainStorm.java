package com.witherstorm.witherstormport.entity;

import com.witherstorm.witherstormport.config.WitherStormConfig;
import com.witherstorm.witherstormport.registry.ModEntities;
import com.witherstorm.witherstormport.registry.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** minecraft_storymode:bigbrain_witherstorm (animation.bigbrain_witherstorm.stage, 120 s) -> bigbrain_witherstorm2. */
public class BigBrainStorm extends FinalFormStorm {
    public BigBrainStorm(EntityType<? extends BigBrainStorm> type, Level level) {
        super(type, level, "animation.wither_storm.block4");
        setPowered(true);                                   // "complete" em 0.0 s
    }

    @Override protected float stormMaxHealth() { return 999_999.0F; }
    @Override protected SoundEvent spawnSound() { return null; }
    @Override public SoundEvent musicEvent() { return ModSounds.WS_MUSIC2.get(); }

    @Override
    protected void formTick(int t) {
        if (t >= WitherStormConfig.ticks(WitherStormConfig.get().bigbrain2At)) transformInto(ModEntities.BIGBRAIN2.get());
    }
}
