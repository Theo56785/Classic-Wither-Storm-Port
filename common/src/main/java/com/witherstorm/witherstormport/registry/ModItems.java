package com.witherstorm.witherstormport.registry;

import com.witherstorm.witherstormport.WitherStormPort;
import com.witherstorm.witherstormport.item.UltimateSwordItem;
import com.witherstorm.witherstormport.item.WitherStormNetherStarItem;
import dev.architectury.core.item.ArchitecturySpawnEggItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(WitherStormPort.MODID, Registries.ITEM);

    /** Substitui minecraft_storymode:wither_storm_primary: o ovo gera direto o estagio 1. */
    public static final RegistrySupplier<Item> WITHER_STORM_SPAWN_EGG = ITEMS.register("wither_storm_spawn_egg",
            () -> new ArchitecturySpawnEggItem(ModEntities.WITHER_STORM1, 0x2B0A3D, 0xB04CFF, new Item.Properties()));

    public static final RegistrySupplier<Item> NETHER_STAR = ITEMS.register("witherstorm_netherstar",
            () -> new WitherStormNetherStarItem(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistrySupplier<Item> ULTIMATE_SWORD = ITEMS.register("ultimate_sword",
            () -> new UltimateSwordItem(new Item.Properties().stacksTo(1).durability(100_000).rarity(Rarity.EPIC)));

    private ModItems() {}
}
