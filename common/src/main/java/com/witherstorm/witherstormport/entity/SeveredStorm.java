package com.witherstorm.witherstormport.entity;

import com.witherstorm.witherstormport.config.WitherStormConfig;
import com.witherstorm.witherstormport.registry.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * minecraft_storymode:severed_witherstorm (animation.severed_witherstorm.stage, 120 s).
 * Scripts do Bedrock: apenas "stage" => sem aura de kill/trator; mantem area_attack e o disparo de caveiras.
 */
public class SeveredStorm extends FinalFormStorm {
    public SeveredStorm(EntityType<? extends SeveredStorm> type, Level level) {
        super(type, level, "animation.wither_storm.block3");
    }

    @Override protected float stormMaxHealth() { return 999_999_999.0F; }
    @Override protected boolean fullMechanics() { return false; }
    @Override protected SoundEvent spawnSound() { return ModSounds.WS_FINISHED.get(); }

    @Override
    protected void formTick(int t) {
        WitherStormConfig c = WitherStormConfig.get();
        if (!isPowered() && t >= WitherStormConfig.ticks(c.severedCompleteAt)) setPowered(true);   // complete2
        if (visualStage() < 6 && t >= WitherStormConfig.ticks(c.headAt)) {
            setStage(6);                                                                            // head
            playToAllPlayers(ModSounds.SWS_HEAD.get(), 5.0F);
        }
    }

    /** Forma imortal: dano fatal cancelado. */
    @Override protected void onFatalDamage() {}

    /** Barra vermelha: "Severed Wither Storm". */
    @Override
    protected Component displayBossName() {
        Component bars = Component.literal(":::::::::::::")
                .withStyle(ChatFormatting.BOLD, ChatFormatting.OBFUSCATED, ChatFormatting.DARK_RED);
        return Component.literal("").append(bars)
                .append(Component.literal("Severed Wither Storm").withStyle(ChatFormatting.DARK_RED)).append(bars.copy());
    }

    @Override protected BossEvent.BossBarColor bossColor() { return BossEvent.BossBarColor.RED; }
}
