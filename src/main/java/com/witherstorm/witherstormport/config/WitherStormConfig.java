package com.witherstorm.witherstormport.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Tempos (em SEGUNDOS) das timelines que no Bedrock ficavam em animations/wither_storm.json.
 * Arquivo gerado: config/witherstormport-common.toml
 */
public final class WitherStormConfig {
    public static final ForgeConfigSpec SPEC;

    // Fase de formacao (wither_storm1) - animation.wither_storm1.stage
    public static final ForgeConfigSpec.DoubleValue STAGE2_AT;
    public static final ForgeConfigSpec.DoubleValue STAGE3_AT;
    public static final ForgeConfigSpec.DoubleValue BEAM_AT;
    public static final ForgeConfigSpec.DoubleValue STAGE4_AT;
    public static final ForgeConfigSpec.DoubleValue STAGE5_AT;
    public static final ForgeConfigSpec.DoubleValue TRANSFORM_AT;
    // Forma final (wither_storm2) - animation.wither_storm2.stage
    public static final ForgeConfigSpec.DoubleValue COMPLETE_AT;
    public static final ForgeConfigSpec.DoubleValue STAGE6_AT;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();

        b.comment("Fase de formacao (segundos desde o spawn)").push("formation");
        STAGE2_AT    = b.defineInRange("stage2AtSeconds",    60.0,  0.0, 86400.0);
        STAGE3_AT    = b.defineInRange("stage3AtSeconds",   120.0,  0.0, 86400.0);
        BEAM_AT      = b.defineInRange("beamAtSeconds",     120.8,  0.0, 86400.0);
        STAGE4_AT    = b.defineInRange("stage4AtSeconds",   180.0,  0.0, 86400.0);
        STAGE5_AT    = b.defineInRange("stage5AtSeconds",   240.0,  0.0, 86400.0);
        TRANSFORM_AT = b.comment("Quando vira a forma final").defineInRange("transformAtSeconds", 260.0, 0.0, 86400.0);
        b.pop();

        b.comment("Forma final (segundos desde a transformacao)").push("final_form");
        COMPLETE_AT = b.comment("Cabecas, tentaculos e feixe aparecem + som de finalizacao")
                .defineInRange("completeAtSeconds", 4.1, 0.0, 86400.0);
        STAGE6_AT   = b.comment("Aparece o osso mutantBody0").defineInRange("stage6AtSeconds", 120.0, 0.0, 86400.0);
        b.pop();

        SPEC = b.build();
    }

    public static int ticks(ForgeConfigSpec.DoubleValue seconds) {
        return (int) Math.round(seconds.get() * 20.0);
    }

    private WitherStormConfig() {}
}
