package com.witherstorm.witherstormport.client;

import com.witherstorm.witherstormport.client.model.StormModel;
import com.witherstorm.witherstormport.client.renderer.DebrisRenderer;
import com.witherstorm.witherstormport.client.renderer.DeadRenderer;
import com.witherstorm.witherstormport.client.renderer.SkullRenderer;
import com.witherstorm.witherstormport.client.renderer.Stage1Renderer;
import com.witherstorm.witherstormport.client.renderer.StormRenderer;
import com.witherstorm.witherstormport.client.sound.StormMusicInstance;
import com.witherstorm.witherstormport.WitherStormPort;
import com.witherstorm.witherstormport.registry.ModEntities;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.resources.ResourceLocation;

import static com.witherstorm.witherstormport.client.model.StormModel.Body;
import static com.witherstorm.witherstormport.client.model.StormModel.Heads;

/** Registro de renderers e hooks de cliente (so carregada quando Env == CLIENT). */
public final class WitherStormPortClient {
    private static ResourceLocation geo(String n) {
        return new ResourceLocation(WitherStormPort.MODID, "geo/entity/" + n + ".geo.json");
    }

    public static void init() {
        StormClientHooks.musicStarter = storm -> {
            if (storm.musicEvent() != null) StormMusicInstance.startFor(storm, storm.musicEvent());
        };

        EntityRendererRegistry.register(ModEntities.WITHER_STORM1, Stage1Renderer::new);

        // wither_storm2: geometry.witherstorm (head1v2, chin1..3), 5 tentaculos, feixe so com is_powered
        EntityRendererRegistry.register(ModEntities.WITHER_STORM2, ctx -> new StormRenderer<>(ctx,
                new StormModel<>("wither_storm", "v2", "", "mutantBody0", Heads.WITH_FIRST, Body.STAGE6),
                "wither_storm", 5, geo("wither_storm_beam"), false, true));

        // bigbrain_witherstorm: sufixo v4, 6 tentaculos, cabecas 2/3 junto com a primeira
        EntityRendererRegistry.register(ModEntities.BIGBRAIN, ctx -> new StormRenderer<>(ctx,
                new StormModel<>("bigbrain_storm", "v4", "v4", "mutantBody0v4", Heads.WITH_FIRST, Body.WITH_FIRST),
                "bigbrain_storm", 6, geo("bigbrain_storm_beam"), false, true));

        // bigbrain_witherstorm2: cabecas 2/3 so na fase "head" (is_saddled)
        EntityRendererRegistry.register(ModEntities.BIGBRAIN2, ctx -> new StormRenderer<>(ctx,
                new StormModel<>("bigbrain_storm", "v4", "v4", "mutantBody0v4", Heads.NEED_HEAD_PHASE, Body.WITH_FIRST),
                "bigbrain_storm", 6, geo("bigbrain_storm_beam"), true, false));

        // severed_witherstorm: sufixo v3, 5 tentaculos, sem mutantBody0
        EntityRendererRegistry.register(ModEntities.SEVERED, ctx -> new StormRenderer<>(ctx,
                new StormModel<>("severed_storm", "v3", "v3", null, Heads.NEED_HEAD_PHASE, Body.NONE),
                "severed_storm", 5, geo("severed_storm_beam"), true, false));

        EntityRendererRegistry.register(ModEntities.WITHER_STORM_DEAD, DeadRenderer::new);
        EntityRendererRegistry.register(ModEntities.SKULL, SkullRenderer::new);
        EntityRendererRegistry.register(ModEntities.TRACTOR, DebrisRenderer::new);
        EntityRendererRegistry.register(ModEntities.TRACTOR0, NoopRenderer::new);   // sem client_entity no Bedrock
    }

    private WitherStormPortClient() {}
}
