# ASSETS.md — Echoes of Aetheria

## Asset Naming Convention (STRICT)
All assets must follow this naming pattern to ensure compatibility with automated loading scripts.

| Category | Pattern |
|---|---|
| **Sprites (Individual)** | `sprites/{category}/{name}_{direction}_{state}_{frame}.png` |
| **Sprite Sheets** | `sheets/{category}/{name}_sheet.png` |
| **Tilesets** | `tiles/{biome}/{biome}_tileset.png` |
| **SFX** | `audio/sfx/{category}/{name}.wav` |
| **Music** | `audio/music/{name}.wav` |
| **Fonts** | `fonts/{name}_font.png` |
| **UI** | `ui/{element_name}.png` |

## Required Assets List

### Sprites & Sheets
- `sheets/player/kaelen_sheet.png`: 4-direction walk (3 frames), idle, attack (2 frames).
- `sheets/npc/silas_sheet.png`: Idle, simple walk.
- `sheets/npc/elara_sheet.png`: Idle, combat stance.
- `sheets/enemy/scrap_drone_sheet.png`: Hover animation, laser fire.
- `sheets/enemy/spectral_shade_sheet.png`: Pulsing idle, swoop attack.

### Tilesets (16x16 pixels)
- `tiles/wastes/wastes_tileset.png`: Rust, scrap metal, dust, cracked earth.
- `tiles/forest/forest_tileset.png`: Bioluminescent flora, spectral vines, ancient stone.
- `tiles/fortress/fortress_tileset.png`: Cold iron, gears, pipes, hazard stripes.

### UI
- `ui/dialogue_box.png`: 9-slice panel for conversations.
- `ui/hud_elements.png`: Health bars, Aether-meter, item icons.
- `ui/cursor.png`: Pixel-perfect pointer.

### Audio
- `audio/music/wastes_theme.wav`: Ambient, metallic wind.
- `audio/music/combat_theme.wav`: Driving, synth-orchestral.
- `audio/sfx/ui/click.wav`: High-pitched mechanical click.
- `audio/sfx/combat/slash.wav`: Sharp Aether-cut sound.

## Asset Generation Pipeline
The pipeline uses Python scripts and ImageMagick to process raw artwork into game-ready formats.

1. **Source:** Hand-drawn pixel art (stored in `assets_source/`).
2. **Process:** `tools/pipeline/process_sheets.py` crops, aligns, and packs frames.
3. **Verify:** `tools/pipeline/check_naming.sh` ensures all files follow the STRICT convention.
4. **Deploy:** `tools/gen_all_assets.sh` copies processed files to the final `assets/` directory.
