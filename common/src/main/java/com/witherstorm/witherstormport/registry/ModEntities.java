package com.witherstorm.witherstormport.registry;

import com.witherstorm.witherstormport.WitherStormPort;
import com.witherstorm.witherstormport.entity.BigBrainStorm;
import com.witherstorm.witherstormport.entity.BigBrainStorm2;
import com.witherstorm.witherstormport.entity.SeveredStorm;
import com.witherstorm.witherstormport.entity.TractorDebris;
import com.witherstorm.witherstormport.entity.TractorSpawner;
import com.witherstorm.witherstormport.entity.WitherStormDead;
import com.witherstorm.witherstormport.entity.WitherStormEntity;
import com.witherstorm.witherstormport.entity.WitherStormSkull;
import com.witherstorm.witherstormport.entity.WitherStormStage1;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/**
 * Entidades mantem o namespace ORIGINAL do addon (minecraft_storymode:*), como pedido.
 * Itens, sons e abas usam o ModID "witherstormport". Para unificar, troque NAMESPACE por WitherStormPort.MODID.
 */
public final class ModEntities {
    public static final String NAMESPACE = "minecraft_storymode";

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(WitherStormPort.MODID, Registries.ENTITY_TYPE);

    private static ResourceLocation id(String path) { return new ResourceLocation(NAMESPACE, path); }

    public static final RegistrySupplier<EntityType<WitherStormStage1>> WITHER_STORM1 = ENTITIES.register(id("wither_storm1"),
            () -> EntityType.Builder.of(WitherStormStage1::new, MobCategory.MONSTER)
                    .sized(1.0F, 3.0F).fireImmune().clientTrackingRange(32).updateInterval(1).build("wither_storm1"));

    public static final RegistrySupplier<EntityType<WitherStormEntity>> WITHER_STORM2 = ENTITIES.register(id("wither_storm2"),
            () -> EntityType.Builder.of(WitherStormEntity::new, MobCategory.MONSTER)
                    .sized(2.5F, 35.0F).fireImmune().clientTrackingRange(32).updateInterval(1).build("wither_storm2"));

    public static final RegistrySupplier<EntityType<BigBrainStorm>> BIGBRAIN = ENTITIES.register(id("bigbrain_witherstorm"),
            () -> EntityType.Builder.of(BigBrainStorm::new, MobCategory.MONSTER)
                    .sized(2.5F, 35.0F).fireImmune().clientTrackingRange(32).updateInterval(1).build("bigbrain_witherstorm"));

    public static final RegistrySupplier<EntityType<BigBrainStorm2>> BIGBRAIN2 = ENTITIES.register(id("bigbrain_witherstorm2"),
            () -> EntityType.Builder.of(BigBrainStorm2::new, MobCategory.MONSTER)
                    .sized(2.5F, 35.0F).fireImmune().clientTrackingRange(32).updateInterval(1).build("bigbrain_witherstorm2"));

    public static final RegistrySupplier<EntityType<SeveredStorm>> SEVERED = ENTITIES.register(id("severed_witherstorm"),
            () -> EntityType.Builder.of(SeveredStorm::new, MobCategory.MONSTER)
                    .sized(2.5F, 35.0F).fireImmune().clientTrackingRange(32).updateInterval(1).build("severed_witherstorm"));

    public static final RegistrySupplier<EntityType<WitherStormDead>> WITHER_STORM_DEAD = ENTITIES.register(id("witherstorm_dead"),
            () -> EntityType.Builder.of(WitherStormDead::new, MobCategory.MONSTER)
                    .sized(3.0F, 3.0F).fireImmune().clientTrackingRange(32).updateInterval(2).build("witherstorm_dead"));

    public static final RegistrySupplier<EntityType<WitherStormSkull>> SKULL = ENTITIES.register(id("witherstorm_skull"),
            () -> EntityType.Builder.<WitherStormSkull>of(WitherStormSkull::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F).fireImmune().clientTrackingRange(16).updateInterval(1).build("witherstorm_skull"));

    /** effect:tractor */
    public static final RegistrySupplier<EntityType<TractorDebris>> TRACTOR = ENTITIES.register(id("tractor"),
            () -> EntityType.Builder.<TractorDebris>of(TractorDebris::new, MobCategory.MISC)
                    .sized(0.4F, 0.8F).fireImmune().clientTrackingRange(16).updateInterval(2).build("tractor"));

    /** effect:tractor0 (invisivel) */
    public static final RegistrySupplier<EntityType<TractorSpawner>> TRACTOR0 = ENTITIES.register(id("tractor0"),
            () -> EntityType.Builder.<TractorSpawner>of(TractorSpawner::new, MobCategory.MISC)
                    .sized(0.4F, 0.8F).fireImmune().clientTrackingRange(4).updateInterval(20).build("tractor0"));

    public static void registerAttributes() {
        EntityAttributeRegistry.register(WITHER_STORM1, WitherStormStage1::createAttributes);
        EntityAttributeRegistry.register(WITHER_STORM2, WitherStormEntity::createAttributes);
        EntityAttributeRegistry.register(BIGBRAIN, BigBrainStorm::createAttributes);
        EntityAttributeRegistry.register(BIGBRAIN2, BigBrainStorm2::createAttributes);
        EntityAttributeRegistry.register(SEVERED, SeveredStorm::createAttributes);
        EntityAttributeRegistry.register(WITHER_STORM_DEAD, WitherStormDead::createAttributes);
    }

    private ModEntities() {}
}
