package com.witherstorm.witherstormport.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.witherstorm.witherstormport.WitherStormPort;
import dev.architectury.platform.Platform;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Tempos (SEGUNDOS) das timelines que no Bedrock ficavam em animations/wither_storm.json.
 * Arquivo: config/witherstormport.json (identico em Forge, Fabric e Quilt).
 */
public final class WitherStormConfig {
    // wither_storm1 (formacao) - animation.wither_storm1.stage
    public double stage2At = 60.0;
    public double stage3At = 120.0;
    public double stage4At = 180.0;
    public double beamAt = 205.0;
    public double stage5At = 269.0;
    public double transformAt = 289.0;
    // wither_storm2 - animation.wither_storm2.stage
    public double completeAt = 4.0;
    public double stage6At = 120.0;
    public double bigbrainAt = 300.0;
    // bigbrain_witherstorm / bigbrain_witherstorm2 / severed_witherstorm
    public double bigbrain2At = 120.0;
    public double headAt = 120.0;
    public double severedCompleteAt = 4.0;
    // witherstorm_dead - animation.wither_storm.respawn
    public double deadRespawnAt = 49.0;

    private static WitherStormConfig instance;

    public static synchronized WitherStormConfig get() {
        if (instance == null) instance = load();
        return instance;
    }

    public static int ticks(double seconds) {
        return (int) Math.round(seconds * 20.0D);
    }

    private static WitherStormConfig load() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Path file = Platform.getConfigFolder().resolve("witherstormport.json");
        WitherStormConfig cfg = new WitherStormConfig();
        try {
            if (Files.exists(file)) {
                try (Reader r = Files.newBufferedReader(file)) {
                    WitherStormConfig read = gson.fromJson(r, WitherStormConfig.class);
                    if (read != null) cfg = read;
                }
            }
            try (Writer w = Files.newBufferedWriter(file)) {   // reescreve para acrescentar chaves novas
                gson.toJson(cfg, w);
            }
        } catch (IOException | RuntimeException e) {
            WitherStormPort.LOGGER.warn("Nao foi possivel ler/gravar {}; usando os padroes.", file, e);
        }
        return cfg;
    }

    private WitherStormConfig() {}
}
