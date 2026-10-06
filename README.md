# AiT Portals Compat

Forge 1.20.1 (47.4.10) addon that re-enables Immersive Portals in the Forge port of Adventures in Time.

Built against:
- AIT Forger `aitforger-1.0.4+1.2.12` (AIT mod id `ait`, version 1.2.12)
- Immersive Portals Forge port `3.0.7` (mod ids `immersive_portals`, `imm_ptl_core`, `q_misc_util`)

## What it does
1. **Flips the flag.** AIT Forger ships `DependencyChecker.hasPortals()` as `return false`. Because that class is loaded before mixins can patch it,
   mixins redirect AIT's calls to it (in ExteriorBlock, ExteriorBlockEntity, DoorBlockEntity) so they get `true` when Immersive Portals is loaded. AIT then uses its own walk-through exterior hitbox and stops
   teleporting players itself for variants whose `hasPortals()` is true.
2. **Spawns the portals.** The port contains no portal-creating code, so `DoorHandlerMixin` hooks
   `DoorHandler.openDoors()/closeDoors()` and `PortalManager` creates an Immersive Portals pair
   (exterior door <-> interior door) when the door is open and the TARDIS is landed, and removes it on close.

## Setup (compile-only jars in `libs/`, never committed)
```bash
mkdir -p libs tmp
unzip -j aitforger-1_0_4_1_2_12.jar META-INF/jarjar/remapped.jar -d tmp
mv tmp/remapped.jar libs/ait-1.2.12.jar
cp immersive-portals-3_0_7-all.jar libs/immersive-portals-3.0.7.jar
cp <your amblekit jar> libs/amblekit-1.0.jar     # whichever Forge-usable amblekit your pack runs AIT with
```
If your file versions differ, change `ip_version`, `ait_version`, `amblekit_version` in `gradle.properties`.

## Build
`gradle wrapper --gradle-version 8.1.1` once (JDK 17), then `./gradlew build` -> `build/libs/`.

## Tuning in game
Edit `config/ait_portals_compat-common.toml`: `flipExteriorFacing`, `flipInteriorFacing`, `planeOffset`,
`yawOffsetDegrees`. `enabled=false` restores stock behaviour.

## Known limitations
- Untested in-game. Orientation/placement are derived from AIT's data and IP's API but may need the config tweaks above.
- Interiors are dynamic per-TARDIS dimensions (`dev.drtheo.multidim`). If the interior never renders through the portal,
  the client probably doesn't know that dimension; that needs separate work.
- Portals are only (re)built on door open/close. Doors left open across a restart keep their saved portals.
