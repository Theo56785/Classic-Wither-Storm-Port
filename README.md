# Wither Storm Port — Architectury (Fabric / Quilt / Forge) · MC 1.20.1

Port do addon Bedrock **Wither Storm Remake (V2)** · Pacote `com.witherstorm.witherstormport` · ModID `witherstormport`
Dependências: **Architectury API 9.2.x**, **GeckoLib 4.4.9**, Java 17.

> **Não foi compilado/testado** (o ambiente de geração não tem acesso às dependências). As assinaturas do Architectury
> e do GeckoLib foram escritas de memória; espere ajustes pontuais no primeiro `./gradlew build`.

## Estrutura
```
common/   código e assets compartilhados (entidades, itens, cliente, JSONs)   ← 95% do mod
fabric/   entrypoint Fabric + fabric.mod.json                                 ← também roda no Quilt
forge/    entrypoint Forge + mods.toml + pack.mcmeta
tools/    build_assets.py: converte o addon Bedrock (geo 1.10→1.12, animações, texturas, sons)
```
## Quilt
Não há módulo `quilt` separado: o **jar do módulo `fabric/`** é o jar do Quilt (o Quilt Loader lê `fabric.mod.json`).
O próprio Architectury API 9.2.14 para 1.20.1 é distribuído assim (um jar "Fabric + Quilt" e outro Forge).
- No Quilt instale: **Quilt Loader**, **Quilted Fabric API** (QFAPI, no lugar da Fabric API), **Architectury API (versão Fabric)**,
  **GeckoLib (versão Fabric)** e o jar `fabric/build/libs/witherstormport-2.0.0.jar`.
- Ao publicar (Modrinth/CurseForge) marque o jar Fabric com os loaders **Fabric e Quilt**.
- Teste no Quilt com `./gradlew :fabric:build` + uma instância Quilt; não há `runClient` do Quilt neste projeto.

## Como compilar
1. Copie do seu MDK/projeto atual para a raiz deste projeto: `gradlew`, `gradlew.bat` e `gradle/wrapper/gradle-wrapper.jar`
   (o `gradle-wrapper.properties` já está aqui, Gradle 8.8).
2. Use o **JDK 17** (`java -version`) e um caminho **sem acentos/espaços** (ex.: `C:\mc\witherstormport`).
3. `./gradlew build` → `fabric/build/libs/witherstormport-2.0.0.jar` e `forge/build/libs/witherstormport-2.0.0.jar`.
4. Testar: `./gradlew :fabric:runClient` ou `./gradlew :forge:runClient`.
5. Para jogar: coloque o jar do mod + **Architectury API** + **GeckoLib** (+ Fabric API no Fabric) na pasta `mods/`.

## O que mudou na V2 (e como foi portado)
| V2 (Bedrock) | Java |
|---|---|
| Ciclo `wither_storm1` (289 s) → `wither_storm2` (300 s) → `bigbrain` (120 s) → `bigbrain2` | `WitherStormStage1` → `WitherStormEntity` → `BigBrainStorm` → `BigBrainStorm2` (`transformInto`) |
| `bigbrain2` invoca 2 `severed_witherstorm` e ganha cabeças 2/3 em 120 s | `BigBrainStorm2.formTick`, `SeveredStorm` (sem aura; só `area_attack` + caveiras) |
| Dano fatal em `wither_storm2`/`bigbrain` → `witherstorm_dead` → 49 s → `bigbrain2` | `FinalFormStorm.hurt` + `WitherStormDead` |
| `bigbrain2`/`severed` cancelam dano fatal | `onFatalDamage()` vazio (só `/kill` e vazio matam) |
| Raios removidos; `tractor1` só existe de 10 a 15 s a cada 15,1 s | `StormMechanics.suctionActive` |
| `/kill r=20` poupa jogadores e as novas formas; `/weather rain`; regeneração em vez de levitação | `killAround`, `keepRain`, efeito REGENERATION |
| Caveira: efeito Náusea (amp 1, 1000 ticks) | `WitherStormSkull` |
| Músicas `mob.ws.music/2/3` por forma | `AbstractWitherStorm.musicEvent()` + `StormClientHooks` |
| `ultimate_sword` (dano 100000) + `player.json` | `UltimateSwordItem.hurtEnemy` (atributo do jogador limita em 2048) |
| Receitas `ultimate_sword`, `witherstorm_netherstar`, `command_block` | `data/witherstormport/recipes/*.json` |
| Loot `wither_boss` | `minecraft:nether_star` (data/minecraft_storymode/loot_tables) |
| Animação de nascimento do estágio 1 (11 s) | `animation.wither_command.spawn` (GeckoLib) |
| Tempos configuráveis | `config/witherstormport.json` (igual nos 3 loaders) |

## Pontos de atenção
- **IDs das entidades** continuam `minecraft_storymode:*` (registradas via `DeferredRegister.register(ResourceLocation, …)`).
  Se o Architectury reclamar do namespace, troque `ModEntities.NAMESPACE` por `WitherStormPort.MODID`
  e mova `assets/minecraft_storymode` + `data/minecraft_storymode` para `witherstormport`.
- O `area_attack` (15 blocos, dano 999.999.999) **continua matando jogadores em sobrevivência**; só o `/kill r=20` os poupa.
- Descartados: `command_block`, `wither_storm_primary`, `wither_storm_full` (indireções), `storm_lightning` e o spawn pelo padrão de soul sand.
- O hitbox/escala do `witherstorm_dead` (3×3, escala 1) não está definido no addon; ajuste em `ModEntities`/`DeadRenderer`.
- Créditos do pack original: ET / UnryCN — confirme permissão antes de publicar.
