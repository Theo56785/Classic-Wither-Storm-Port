package com.witherstorm.witherstormport.registry;

import com.witherstorm.witherstormport.WitherStormPort;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Mesmos IDs do sound_definitions.json do Bedrock (mob.ws.*). */
public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, WitherStormPort.MODID);

    public static final RegistryObject<SoundEvent> WS_IDLE     = reg("mob.ws.idle");
    public static final RegistryObject<SoundEvent> WS_HURT     = reg("mob.ws.hurt");
    public static final RegistryObject<SoundEvent> WS_DEATH    = reg("mob.ws.death");
    public static final RegistryObject<SoundEvent> WS_FINISHED = reg("mob.ws.finished");
    public static final RegistryObject<SoundEvent> WS_BGM      = reg("mob.ws.bgm");
    public static final RegistryObject<SoundEvent> WS_MUSIC    = reg("mob.ws.music");

    private static RegistryObject<SoundEvent> reg(String name) {
        return SOUNDS.register(name, () ->
                SoundEvent.createVariableRangeEvent(new ResourceLocation(WitherStormPort.MODID, name)));
    }

    private ModSounds() {}
}
