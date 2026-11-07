# Wither Storm Port — Architectury (Fabric / Quilt / Forge) · MC 1.20.1

Port of the **Wither Storm Remake (V2)** Bedrock add-on · Package: `com.witherstorm.witherstormport` · ModID: `witherstormport`  
Dependencies: **Architectury API 9.2.x**, **GeckoLib 4.4.9**, Java 17.

> **Uncompiled/Untested** (the generation environment lacks access to dependencies). Architectury and GeckoLib 
> signatures were written from memory; expect minor adjustments during the first `./gradlew build`.

## Structure
```text
common/   shared code & assets (entities, items, client, JSONs)   ← 95% of the mod
fabric/   Fabric entrypoint + fabric.mod.json                      ← also runs on Quilt
forge/    Forge entrypoint + mods.toml + pack.mcmeta
tools/    build_assets.py: converts the Bedrock add-on (geo 1.10→1.12, animations, textures, sounds)
```

## Quilt
There is no separate `quilt` module: the **jar from the `fabric/` module** doubles as the Quilt jar (Quilt Loader reads `fabric.mod.json`).  
Architectury API 9.2.14 for 1.20.1 itself is distributed this way (one "Fabric + Quilt" jar and one Forge jar).
- On Quilt, install: **Quilt Loader**, **Quilted Fabric API** (QFAPI, instead of Fabric API), **Architectury API (Fabric version)**, **GeckoLib (Fabric version)**, and the `fabric/build/libs/witherstormport-2.0.0.jar`.
- When publishing (Modrinth/CurseForge), mark the Fabric jar with both **Fabric and Quilt** loaders.
- Test on Quilt using `./gradlew :fabric:build` + a Quilt instance; there is no Quilt `runClient` task in this project.

## How to Compile
1. Copy `gradlew`, `gradlew.bat`, and `gradle/wrapper/gradle-wrapper.jar` from your current MDK/project to this project's root folder (`gradle-wrapper.properties` is already present, configured for Gradle 8.8).
2. Ensure you are using **JDK 17** (`java -version`) and a directory path **without accents or spaces** (e.g., `C:\mc\witherstormport`).
3. Run `./gradlew build` → outputs `fabric/build/libs/witherstormport-2.0.0.jar` and `forge/build/libs/witherstormport-2.0.0.jar`.
4. To test: run `./gradlew :fabric:runClient` or `./gradlew :forge:runClient`.
5. To play: drop the mod jar + **Architectury API** + **GeckoLib** (+ Fabric API if using Fabric) into your `mods/` folder.

## Bedrock → Java Mapping (V2 Changes)

| Bedrock (V2) | Java |
|---|---|
| Cycle: `wither_storm1` (289 s) → `wither_storm2` (300 s) → `bigbrain` (120 s) → `bigbrain2` | `WitherStormStage1` → `WitherStormEntity` → `BigBrainStorm` → `BigBrainStorm2` (`transformInto`) |
| `bigbrain2` summons 2 `severed_witherstorm` and gains heads 2/3 in 120 s | `BigBrainStorm2.formTick`, `SeveredStorm` (no aura; only `area_attack` + skulls) |
| Fatal damage on `wither_storm2`/`bigbrain` → `witherstorm_dead` → 49 s → `bigbrain2` | `FinalFormStorm.hurt` + `WitherStormDead` |
| `bigbrain2`/`severed` cancel fatal damage | Empty `onFatalDamage()` (only `/kill` and void can kill them) |
| Lightning strikes removed; `tractor1` only active for 10 to 15 s every 15.1 s | `StormMechanics.suctionActive` |
| `/kill r=20` spares players and new forms; `/weather rain`; Regeneration instead of Levitation | `killAround`, `keepRain`, REGENERATION effect |
| Skull: Nausea effect (amp 1, 1000 ticks) | `WitherStormSkull` |
| Tracks `mob.ws.music/2/3` per form | `AbstractWitherStorm.musicEvent()` + `StormClientHooks` |
| `ultimate_sword` (damage: 100,000) + `player.json` | `UltimateSwordItem.hurtEnemy` (player attributes cap it at 2048) |
| Recipes for `ultimate_sword`, `witherstorm_netherstar`, `command_block` | `data/witherstormport/recipes/*.json` |
| `wither_boss` loot drop | `minecraft:nether_star` (data/minecraft_storymode/loot_tables) |
| Stage 1 spawn animation (11 s) | `animation.wither_command.spawn` (GeckoLib) |
| Configurable timings | `config/witherstormport.json` (identical across all 3 loaders) |

## Important Notes
- **Entity IDs** remain under the `minecraft_storymode:*` namespace (registered via `DeferredRegister.register(ResourceLocation, …)`). If Architectury complains about the namespace, replace `ModEntities.NAMESPACE` with `WitherStormPort.MODID` and move `assets/minecraft_storymode` + `data/minecraft_storymode` to `witherstormport`.
- The `area_attack` (15 blocks, 999,999,999 damage) **will still kill players in Survival mode**; only the `/kill r=20` command spares them.
- Discarded elements: `command_block`, `wither_storm_primary`, `wither_storm_full` (indirections), `storm_lightning`, and spawning via the soul sand pattern.
- The hitbox/scale for `witherstorm_dead` (3×3, scale 1) is not defined in the original add-on; adjust it inside `ModEntities`/`DeadRenderer`.
- Original pack credits: ET / UnryCN — ensure you get permission before publishing.

***

*Original add-on by: 鸥吃鱼*  
*Re-created by Claude AI*
