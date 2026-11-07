package com.witherstorm.witherstormport.entity;

/**
 * Estado visual lido pelo cliente. No Bedrock o addon "abusava" de flags de entidade
 * (is_baby, is_sheared, is_saddled, is_chested, is_charged...) so para mostrar partes do modelo;
 * aqui isso vira um inteiro de estagio + um boolean sincronizados.
 */
public interface StormVisual {
    /** Estagio 1..5 (forma de formacao) ou 5..6 (forma final). */
    int visualStage();

    /** query.is_powered: feixe, cabecas e tentaculos da forma final. */
    boolean isPowered();

    /** query.life_time (segundos). */
    float stormSeconds(float partialTick);

    /** variable.has_target. */
    boolean hasTarget();
}
