package com.witherstorm.witherstormport;

import com.mojang.logging.LogUtils;
import com.witherstorm.witherstormport.config.WitherStormConfig;
import com.witherstorm.witherstormport.registry.ModEntities;
import com.witherstorm.witherstormport.registry.ModItems;
import com.witherstorm.witherstormport.registry.ModSounds;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/** Ponto de entrada do mod. Todos os registros usam DeferredRegister. */
@Mod(WitherStormPort.MODID)
public class WitherStormPort {
    public static final String MODID = "witherstormport";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WitherStormPort() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModSounds.SOUNDS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModEntities.ENTITIES.register(modBus);

        modBus.addListener(ModEntities::registerAttributes);
        modBus.addListener(this::addCreativeTabs);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, WitherStormConfig.SPEC);
    }

    private void addCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.WITHER_STORM_SPAWN_EGG);
        }
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.USELESS_NETHER_STAR);
        }
    }
}
