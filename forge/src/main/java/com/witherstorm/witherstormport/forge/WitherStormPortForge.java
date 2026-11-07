package com.witherstorm.witherstormport.forge;

import com.witherstorm.witherstormport.WitherStormPort;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/** Entrypoint Forge. */
@Mod(WitherStormPort.MODID)
public final class WitherStormPortForge {
    public WitherStormPortForge() {
        EventBuses.registerModEventBus(WitherStormPort.MODID, FMLJavaModLoadingContext.get().getModEventBus());
        WitherStormPort.init();
    }
}
