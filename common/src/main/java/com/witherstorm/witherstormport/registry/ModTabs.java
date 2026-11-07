package com.witherstorm.witherstormport.registry;

import com.witherstorm.witherstormport.WitherStormPort;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(WitherStormPort.MODID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> MAIN = TABS.register("main",
            () -> CreativeTabRegistry.create(builder -> builder
                    .title(Component.translatable("itemGroup.witherstormport.main"))
                    .icon(() -> new ItemStack(ModItems.NETHER_STAR.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.WITHER_STORM_SPAWN_EGG.get());
                        output.accept(ModItems.NETHER_STAR.get());
                        output.accept(ModItems.ULTIMATE_SWORD.get());
                    })));

    private ModTabs() {}
}
