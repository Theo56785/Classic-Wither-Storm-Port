package com.witherstorm.witherstormport.client;

import com.witherstorm.witherstormport.entity.AbstractWitherStorm;

import java.util.function.Consumer;

/**
 * Ponte entre as entidades (codigo comum) e o cliente: o Minecraft dedicado nunca carrega classes de cliente.
 * {@link WitherStormPortClient#init()} troca o no-op abaixo pela implementacao real.
 */
public final class StormClientHooks {
    public static Consumer<AbstractWitherStorm> musicStarter = e -> {};

    public static void startMusic(AbstractWitherStorm storm) {
        musicStarter.accept(storm);
    }

    private StormClientHooks() {}
}
