# Wither Storm Port — Forge 1.20.1 (GeckoLib 4.4.9)

Port of the **SW Wither Storm V2** Bedrock add-on to Forge 1.20.1.  
Package `com.witherstorm.witherstormport` · ModID `witherstormport`.

## Bedrock → Forge Mapping

| Bedrock | Java |
|---|---|
| `wither_storm_primary` (spawn egg) | `ForgeSpawnEggItem` → `WitherStormStage1` |
| `wither_storm1` + `animation.wither_storm1.stage` | `WitherStormStage1` (stage stored in `SynchedEntityData`, timings in `WitherStormConfig`) |
| `wither_storm2` | `WitherStormEntity` |
| `witherstorm_skull` | `WitherStormSkull` (`AbstractHurtingProjectile`) |
| `effect:tractor` / `tractor0` | `TractorDebris` / `TractorSpawner` |
| `effect:tractor1` (`knockback_roar` −3) | `StormMechanics.suctionRoar` (built into the storm) |
| `command_block`, `wither_storm_full`, `storm_lightning` | Discarded (spawn only via spawn egg; lightning was unused) |
| `is_baby/is_sheared/is_saddled/is_chested/is_charged` flags | `visualStage()` + `isPowered()` |
| Render controller's `part_visibility` | `setCustomAnimations` → `bone.setHidden` |
| `query.target_x/y_rotation`, `life_time`, `math.sin/cos` | `StormBones` (degrees, as in Molang) |
| `wither_storm` material (emissive alpha) | `AutoGlowingGeoLayer` + `*_glowmask.png` (generated from alpha 37/63) |
| `wither_boss_armor` material | `BeamLayer` (`RenderType.energySwirl`) |
| Tentacles (5 render controllers) | `TentacleLayer` |
| `/fill`, `/kill`, `/tp`, `/weather`, `summon lightning` | `StormMechanics` |
| `health 999,999,999` | Overridden `getMaxHealth()` (the vanilla attribute is capped at 1024) |
| `loot` | `data/minecraft_storymode/loot_tables/entities/*.json` → modified Nether Star (useless) |
| `sound_definitions.json` | `sounds.json` + `ModSounds` (`mob.ws.*`) |

## Intentional Deviations (to Avoid Surprises)

- **Entity IDs** remain under `minecraft_storymode:*`. Items/sounds use `witherstormport:*`.
  To unify them, change `ModEntities.NAMESPACE` to `WitherStormPort.MODID` and move
  `assets/minecraft_storymode` and `data/minecraft_storymode` to `witherstormport`.
- **Faithful to the original (destructive):** clears 5×4×5 blocks every tick, runs `kill` on entities within 20 blocks
  (including players in Creative mode), deals 5 damage every 4 ticks within 128 blocks, forces stormy weather,
  and strikes all entities with lightning every 10 seconds. Safety limit: max. 128 lightning strikes per cycle; bedrock is preserved.
- `tractor0` is exempt from `/kill` (in the original, it would be killed in the same tick) and lives for a maximum of 60 seconds;
  there is a cap of 150 debris entities per area.
- The Bedrock version had no actual "death" mechanic: the `fatal` event only canceled damage. Here, the final form **can die**
  (999,999,999 health) and drops the modified Nether Star.
- Levitation was converted to flight without gravity; `/weather` is refreshed every 20 ticks (same effect).
- Music: `mob.ws.music` loops on the client while the final form exists.
- Wither duration for the skull (1000/2000/8000) is interpreted as **ticks**.
- `tick_world never_despawn` (area always loaded) was not ported.
- Idle/hurt sounds were converted from stereo to mono (otherwise, they do not attenuate with distance).
- Original pack credits: ET / UnryCN. Confirm permission before publishing.

## Quick Test

1. Spawn using the spawn egg → purple boss bar, `ws_spawn1` sound, model grows through the stages (timings are configurable).
   For quick testing, reduce the values in `config/witherstormport-common.toml`.
2. After `transformAtSeconds` → final form; heads, tentacles, and beam appear in approximately 4 seconds, with looping music.
3. Stay in **Spectator** mode more than 20 blocks away to observe (anything else nearby will die).
