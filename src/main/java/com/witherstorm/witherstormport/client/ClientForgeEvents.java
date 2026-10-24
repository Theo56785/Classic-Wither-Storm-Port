package com.witherstorm.witherstormport.client;

import com.witherstorm.witherstormport.WitherStormPort;
import com.witherstorm.witherstormport.client.sound.StormMusicInstance;
import com.witherstorm.witherstormport.entity.WitherStormEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WitherStormPort.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientForgeEvents {
    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() && event.getEntity() instanceof WitherStormEntity storm) {
            StormMusicInstance.startFor(storm);
        }
    }

    private ClientForgeEvents() {}
}
