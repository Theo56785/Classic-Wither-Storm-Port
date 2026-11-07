package com.witherstorm.witherstormport;

import com.mojang.logging.LogUtils;
import com.witherstorm.witherstormport.client.WitherStormPortClient;
import com.witherstorm.witherstormport.config.WitherStormConfig;
import com.witherstorm.witherstormport.registry.ModEntities;
import com.witherstorm.witherstormport.registry.ModItems;
import com.witherstorm.witherstormport.registry.ModSounds;
import com.witherstorm.witherstormport.registry.ModTabs;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import org.slf4j.Logger;

/** Inicializacao comum (Fabric, Quilt e Forge chamam {@link #init()} a partir dos seus entrypoints). */
public final class WitherStormPort {
    public static final String MODID = "witherstormport";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        WitherStormConfig.get();

        ModSounds.SOUNDS.register();
        ModItems.ITEMS.register();
        ModEntities.ENTITIES.register();
        ModTabs.TABS.register();
        ModEntities.registerAttributes();

        // O codigo de cliente so e carregado quando o ambiente e CLIENT (nunca num servidor dedicado).
        EnvExecutor.runInEnv(Env.CLIENT, () -> WitherStormPortClient::init);
    }

    private WitherStormPort() {}
}
