package com.witherstorm.witherstormport.registry;

import com.witherstorm.witherstormport.WitherStormPort;
import com.witherstorm.witherstormport.item.UselessNetherStarItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, WitherStormPort.MODID);

    /** Substitui minecraft_storymode:wither_storm_primary: o ovo gera direto o estagio 1. */
    public static final RegistryObject<Item> WITHER_STORM_SPAWN_EGG = ITEMS.register("wither_storm_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.WITHER_STORM1, 0x2B0A3D, 0xB04CFF, new Item.Properties()));

    public static final RegistryObject<Item> USELESS_NETHER_STAR = ITEMS.register("witherstorm_netherstar",
            () -> new UselessNetherStarItem(new Item.Properties().rarity(Rarity.EPIC)));

    private ModItems() {}
}
