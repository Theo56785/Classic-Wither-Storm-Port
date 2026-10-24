package com.witherstorm.witherstormport.registry;

import com.witherstorm.witherstormport.entity.TractorDebris;
import com.witherstorm.witherstormport.entity.TractorSpawner;
import com.witherstorm.witherstormport.entity.WitherStormEntity;
import com.witherstorm.witherstormport.entity.WitherStormSkull;
import com.witherstorm.witherstormport.entity.WitherStormStage1;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * As entidades mantem o namespace ORIGINAL do addon (minecraft_storymode:*), como pedido.
 * O ModID continua sendo "witherstormport" (itens, sons, assets). Para usar o ModID tambem
 * nas entidades, troque apenas a constante NAMESPACE por WitherStormPort.MODID.
 */
public final class ModEntities {
    public static final String NAMESPACE = "minecraft_storymode";

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, NAMESPACE);

    public static final RegistryObject<EntityType<WitherStormStage1>> WITHER_STORM1 = ENTITIES.register("wither_storm1",
            () -> EntityType.Builder.of(WitherStormStage1::new, MobCategory.MONSTER)
                    .sized(1.0F, 3.0F).fireImmune().clientTrackingRange(32).updateInterval(1)
                    .build("wither_storm1"));

    public static final RegistryObject<EntityType<WitherStormEntity>> WITHER_STORM2 = ENTITIES.register("wither_storm2",
            () -> EntityType.Builder.of(WitherStormEntity::new, MobCategory.MONSTER)
                    .sized(2.5F, 35.0F).fireImmune().clientTrackingRange(32).updateInterval(1)
                    .build("wither_storm2"));

    public static final RegistryObject<EntityType<WitherStormSkull>> SKULL = ENTITIES.register("witherstorm_skull",
            () -> EntityType.Builder.<WitherStormSkull>of(WitherStormSkull::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F).fireImmune().clientTrackingRange(16).updateInterval(1)
                    .build("witherstorm_skull"));

    /** effect:tractor */
    public static final RegistryObject<EntityType<TractorDebris>> TRACTOR = ENTITIES.register("tractor",
            () -> EntityType.Builder.<TractorDebris>of(TractorDebris::new, MobCategory.MISC)
                    .sized(0.4F, 0.8F).fireImmune().clientTrackingRange(16).updateInterval(2)
                    .build("tractor"));

    /** effect:tractor0 (invisivel) */
    public static final RegistryObject<EntityType<TractorSpawner>> TRACTOR0 = ENTITIES.register("tractor0",
            () -> EntityType.Builder.<TractorSpawner>of(TractorSpawner::new, MobCategory.MISC)
                    .sized(0.4F, 0.8F).fireImmune().clientTrackingRange(4).updateInterval(20)
                    .build("tractor0"));

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(WITHER_STORM1.get(), WitherStormStage1.createAttributes().build());
        event.put(WITHER_STORM2.get(), WitherStormEntity.createAttributes().build());
    }

    private ModEntities() {}
}
