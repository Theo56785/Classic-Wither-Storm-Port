package com.witherstorm.witherstormport.registry;

import com.witherstorm.witherstormport.WitherStormPort;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

/** Mesmos IDs do sound_definitions.json do Bedrock (mob.ws.*, mob.wc.*, mob.sws.*). */
public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(WitherStormPort.MODID, Registries.SOUND_EVENT);

    public static final RegistrySupplier<SoundEvent> WS_IDLE     = reg("mob.ws.idle");
    public static final RegistrySupplier<SoundEvent> WS_HURT     = reg("mob.ws.hurt");
    public static final RegistrySupplier<SoundEvent> WS_DEATH    = reg("mob.ws.death");
    public static final RegistrySupplier<SoundEvent> WS_SHOOT    = reg("mob.ws.shoot");
    public static final RegistrySupplier<SoundEvent> WS_FINISHED = reg("mob.ws.finished");
    public static final RegistrySupplier<SoundEvent> WS_BGM      = reg("mob.ws.bgm");
    public static final RegistrySupplier<SoundEvent> WS_MUSIC    = reg("mob.ws.music");
    public static final RegistrySupplier<SoundEvent> WS_MUSIC2   = reg("mob.ws.music2");
    public static final RegistrySupplier<SoundEvent> WS_MUSIC3   = reg("mob.ws.music3");
    public static final RegistrySupplier<SoundEvent> WC_HURT     = reg("mob.wc.hurt");
    public static final RegistrySupplier<SoundEvent> WC_DEATH    = reg("mob.wc.death");
    public static final RegistrySupplier<SoundEvent> SWS_HEAD    = reg("mob.sws.head");

    private static RegistrySupplier<SoundEvent> reg(String name) {
        return SOUNDS.register(name, () ->
                SoundEvent.createVariableRangeEvent(new ResourceLocation(WitherStormPort.MODID, name)));
    }

    private ModSounds() {}
}
