package com.witherstorm.witherstormport.client;

import com.witherstorm.witherstormport.WitherStormPort;
import com.witherstorm.witherstormport.client.renderer.DebrisRenderer;
import com.witherstorm.witherstormport.client.renderer.SkullRenderer;
import com.witherstorm.witherstormport.client.renderer.Stage1Renderer;
import com.witherstorm.witherstormport.client.renderer.StormRenderer;
import com.witherstorm.witherstormport.registry.ModEntities;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WitherStormPort.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.WITHER_STORM1.get(), Stage1Renderer::new);
        event.registerEntityRenderer(ModEntities.WITHER_STORM2.get(), StormRenderer::new);
        event.registerEntityRenderer(ModEntities.SKULL.get(), SkullRenderer::new);
        event.registerEntityRenderer(ModEntities.TRACTOR.get(), DebrisRenderer::new);
        event.registerEntityRenderer(ModEntities.TRACTOR0.get(), NoopRenderer::new);   // sem client_entity no Bedrock
    }

    private ClientModEvents() {}
}
