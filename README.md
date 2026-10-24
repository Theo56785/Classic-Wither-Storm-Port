# Wither Storm Port — Forge 1.20.1 (GeckoLib 4.4.9)

Port do addon Bedrock **SW Wither Storm V2** para Forge 1.20.1.
Pacote `com.witherstorm.witherstormport` · ModID `witherstormport`.

## Como usar
1. Baixe o **Forge MDK 1.20.1 (47.x)** e extraia.
2. Apague `src/main/java/com/example` e copie a pasta `src/` deste projeto por cima da do MDK.
3. Substitua o `build.gradle` do MDK pelo deste projeto (só acrescenta o repositório e a dependência do
   GeckoLib 4.4.9) e ajuste `mod_id`, `mod_group_id` etc. no `gradle.properties` (veja `gradle.properties.reference`).
   O `mods.toml` já usa as propriedades expandidas pelo `processResources` do MDK.
4. `./gradlew genIntellijRuns` (ou `genEclipseRuns`) e `./gradlew runClient`.
5. Modo criativo → aba **Spawn Eggs** → *Wither Storm*.

> **Este código NÃO foi compilado** (o ambiente de geração não tem Forge/GeckoLib/javac). Foi
> escrito contra as APIs do Forge 47 e GeckoLib 4.4.x e passou em checagens estruturais, mas
> espere ajustes pontuais de compilação no primeiro build. Mande os erros que eu corrijo.

## Mapeamento Bedrock → Forge
| Bedrock | Java |
|---|---|
| `wither_storm_primary` (ovo) | `ForgeSpawnEggItem` → `WitherStormStage1` |
| `wither_storm1` + `animation.wither_storm1.stage` | `WitherStormStage1` (estágio em `SynchedEntityData`, tempos em `WitherStormConfig`) |
| `wither_storm2` | `WitherStormEntity` |
| `witherstorm_skull` | `WitherStormSkull` (`AbstractHurtingProjectile`) |
| `effect:tractor` / `tractor0` | `TractorDebris` / `TractorSpawner` |
| `effect:tractor1` (`knockback_roar` −3) | `StormMechanics.suctionRoar` (embutido na tempestade) |
| `command_block`, `wither_storm_full`, `storm_lightning` | descartados (spawn só por ovo; lightning não era usado) |
| Flags `is_baby/is_sheared/is_saddled/is_chested/is_charged` | `visualStage()` + `isPowered()` |
| `part_visibility` do render controller | `setCustomAnimations` → `bone.setHidden` |
| `query.target_x/y_rotation`, `life_time`, `math.sin/cos` | `StormBones` (graus, como no Molang) |
| `material wither_storm` (alfa emissivo) | `AutoGlowingGeoLayer` + `*_glowmask.png` (gerado do alfa 37/63) |
| `material wither_boss_armor` | `BeamLayer` (`RenderType.energySwirl`) |
| Tentáculos (5 render controllers) | `TentacleLayer` |
| `/fill`, `/kill`, `/tp`, `/weather`, `summon lightning` | `StormMechanics` |
| `health 999.999.999` | `getMaxHealth()` sobrescrito (o atributo vanilla trava em 1024) |
| `loot` | `data/minecraft_storymode/loot_tables/entities/*.json` → Nether Star modificada (inútil) |
| `sound_definitions.json` | `sounds.json` + `ModSounds` (`mob.ws.*`) |

## Desvios conscientes (para não surpreender)
- **IDs das entidades** ficam em `minecraft_storymode:*` (pedido). Itens/sons ficam em `witherstormport:*`.
  Para unificar, troque `ModEntities.NAMESPACE` por `WitherStormPort.MODID` e mova
  `assets/minecraft_storymode` e `data/minecraft_storymode` para `witherstormport`.
- **Fiel ao original (destrutivo):** limpa blocos 5×4×5 a cada tick, `kill` em 20 blocos
  (inclui jogadores/criativo), 5 de dano a cada 4 ticks em 128 blocos, clima de tempestade forçado,
  raios em todas as entidades a cada 10 s. Trava de segurança: máx. 128 raios por ciclo; bedrock é preservado.
- `tractor0` foi poupado do `/kill` (no original seria morto no mesmo tick) e vive no máx. 60 s;
  há teto de 150 detritos por área.
- O Bedrock não tinha o "morrer": o evento `fatal` só cancelava o dano. Aqui a forma final **pode morrer**
  (999.999.999 de vida) e dropa a Nether Star modificada.
- Levitação virou voo sem gravidade; `/weather` é renovado a cada 20 ticks (mesmo efeito).
- Música: loop de `mob.ws.music` no cliente enquanto a forma final existir.
- Duração do Wither do crânio (1000/2000/8000) interpretada como **ticks**.
- `tick_world never_despawn` (área sempre carregada) não foi portado.
- Sons de idle/hurt foram convertidos de estéreo para mono (senão não atenuam por distância).
- Créditos do pack original: ET / UnryCN. Confirme permissão antes de publicar.

## Teste rápido
1. Spawn do ovo → boss bar roxa, som `ws_spawn1`, modelo cresce nos estágios (config: tempos).
   Para testar rápido, reduza os valores em `config/witherstormport-common.toml`.
2. Após `transformAtSeconds` → forma final, cabeças/tentáculos/feixe em ~4 s, música em loop.
3. Fique em **Spectator** a >20 blocos para observar (qualquer outra coisa perto morre).
