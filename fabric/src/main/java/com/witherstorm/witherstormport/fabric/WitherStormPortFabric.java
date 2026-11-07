package com.witherstorm.witherstormport.fabric;

import com.witherstorm.witherstormport.WitherStormPort;
import net.fabricmc.api.ModInitializer;

/** Entrypoint Fabric. No Quilt este mesmo jar roda via camada de compatibilidade (fabric.mod.json). */
public final class WitherStormPortFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        WitherStormPort.init();
    }
}
