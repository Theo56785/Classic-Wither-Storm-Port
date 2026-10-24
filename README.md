EN_US


Wither Storm Port — Forge 1.20.1 (GeckoLib 4.4.9)

Port of the Bedrock addon SW Wither Storm V2 to Forge 1.20.1.
Package com.witherstorm.witherstormport · ModID witherstormport.

Bedrock → Forge Mapping


Bedrock	Java
wither_storm_primary (egg)	ForgeSpawnEggItem → WitherStormStage1
wither_storm1 + animation.wither_storm1.stage	WitherStormStage1 (stage in SynchedEntityData, timings in WitherStormConfig)
wither_storm2	WitherStormEntity
witherstorm_skull	WitherStormSkull (AbstractHurtingProjectile)
effect:tractor / tractor0	TractorDebris / TractorSpawner
effect:tractor1 (knockback_roar −3)	StormMechanics.suctionRoar (built into the storm)
command_block, wither_storm_full, storm_lightning	discarded (spawn via egg only; lightning was unused)
Flags is_baby/is_sheared/is_saddled/is_chested/is_charged	visualStage() + isPowered()
part_visibility from render controller	setCustomAnimations → bone.setHidden
query.target_x/y_rotation, life_time, math.sin/cos	StormBones (degrees, same as Molang)
material wither_storm (emissive alpha)	AutoGlowingGeoLayer + *_glowmask.png (generated from alpha 37/63)
material wither_boss_armor	BeamLayer (RenderType.energySwirl)
Tentacles (5 render controllers)	TentacleLayer
/fill, /kill, /tp, /weather, summon lightning	StormMechanics
health 999.999.999	Overridden getMaxHealth() (the vanilla attribute caps at 1024)
loot	data/minecraft_storymode/loot_tables/entities/*.json → Modified Nether Star (useless)
sound_definitions.json	sounds.json + ModSounds (mob.ws.*)


Conscious Deviations (To Avoid Surprises)

• Entity IDs remain in minecraft_storymode:*. Items/sounds are in witherstormport:*.
To unify them, change ModEntities.NAMESPACE to WitherStormPort.MODID and move
assets/minecraft_storymode and data/minecraft_storymode to witherstormport.
• Faithful to the original (destructive): clears 5×4×5 blocks every tick, executes kill within 20 blocks
(includes players/creative mode), deals 5 damage every 4 ticks within 128 blocks, forces thunderstorm weather,
and strikes all entities with lightning every 10 s. Safety lock: max 128 lightning bolts per cycle; bedrock is preserved.
• tractor0 was spared from /kill (in the original it would be killed on the exact same tick) and lives for a max of 60 s;
there is a cap of 150 debris per area.
• Bedrock did not feature "dying": the fatal event only canceled damage. Here, the final form can die
(999,999,999 health) and drops the modified Nether Star.
• Levitation was turned into gravity-free flight; /weather is renewed every 20 ticks (same effect).
• Music: mob.ws.music loops on the client side while the final form exists.
• Skull Wither effect duration (1000/2000/8000) was interpreted as ticks.
• tick_world never_despawn (always-loaded area) was not ported.
• Idle/hurt sounds were converted from stereo to mono (otherwise they do not attenuate by distance).
• Original pack credits: ET / UnryCN. Confirm permission before publishing.

Quick Test

1. Spawn from egg → purple boss bar, ws_spawn1 sound, model grows across stages (config: timings).
For a quick test, reduce the values in config/witherstormport-common.toml.
2. After transformAtSeconds → final form, heads/tentacles/beam appear in ~4 s, looping music kicks in.
3. Stay in Spectator mode at >20 blocks away to observe (anything else nearby dies).

PT_BR


# Wither Storm Port — Forge 1.20.1 (GeckoLib 4.4.9)

Port do addon Bedrock **SW Wither Storm V2** para Forge 1.20.1.
Pacote `com.witherstorm.witherstormport` · ModID `witherstormport`.

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
- **IDs das entidades** ficam em `minecraft_storymode:*`. Itens/sons ficam em `witherstormport:*`.
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
