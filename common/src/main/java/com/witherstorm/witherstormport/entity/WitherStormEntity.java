package com.witherstorm.witherstormport.entity;

import com.witherstorm.witherstormport.config.WitherStormConfig;
import com.witherstorm.witherstormport.registry.ModEntities;
import com.witherstorm.witherstormport.registry.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * minecraft_storymode:wither_storm2 (animation.wither_storm2.stage, 300 s):
 * stage5 + som "finished" (0 s), complete (4 s), stage6 (120 s), bigbrain (300 s). HP 999.999.
 */
public class WitherStormEntity extends FinalFormStorm {
    public WitherStormEntity(EntityType<? extends WitherStormEntity> type, Level level) {
        super(type, level, "animation.wither_storm.block2");
    }

    @Override protected float stormMaxHealth() { return 999_999.0F; }
    @Override protected SoundEvent spawnSound() { return ModSounds.WS_FINISHED.get(); }
    @Override public SoundEvent musicEvent() { return ModSounds.WS_MUSIC.get(); }

    @Override
    protected void formTick(int t) {
        WitherStormConfig c = WitherStormConfig.get();
        setStage(t >= WitherStormConfig.ticks(c.stage6At) ? 6 : 5);
        if (!isPowered() && t >= WitherStormConfig.ticks(c.completeAt)) setPowered(true);
        if (t >= WitherStormConfig.ticks(c.bigbrainAt)) transformInto(ModEntities.BIGBRAIN.get());
    }
}
