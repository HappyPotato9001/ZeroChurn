# ZeroChurn Workspace

This repository ships **two separate Fabric mods**. They are designed to work alone or together.

| Mod | Role | Jar |
| --- | --- | --- |
| **ZeroChurn** | GC hitch reduction via thread-local pooling of short-lived math objects | `dist/zerochurn-*.jar` |
| **InfoForge** | Client HUD (coords, FPS, clock, config UI; waypoints/minimap later) | `dist/infoforge-*.jar` |

## Compatibility

Both mods soft-detect common Fabric performance packs and avoid fighting them:

- Lithium, Sodium, ImmediatelyFast, FerriteCore, ModernFix, C2ME
- Iris (InfoForge stays on Fabric `HudElementRegistry`)
- Each other: InfoForge may show an optional ZeroChurn stats line via reflection only

ZeroChurn never depends on InfoForge. InfoForge never depends on ZeroChurn.

## Build

Supported Minecraft targets for **both** mods:

- `1.21.11`
- `26.1`
- `26.2`
- `26.3`

Build every target:

```bash
./scripts/build-all.sh
```

Build one target:

```bash
./scripts/build-all.sh 26.3
```

Artifacts land in `dist/` as:

- `zerochurn-1.2.0+<mc>.jar`
- `infoforge-1.2.0+<mc>.jar`

Toolchain notes:

- `26.1` / `26.2` / `26.3` use Java **25** + non-remap Loom
- `1.21.11` uses Java **21** + remap Loom (`legacy121/`)

## Config

- ZeroChurn: `config/zerochurn.json`
- InfoForge: `config/infoforge.json` (open in-game with **Right Shift**)
