# DATA_FORMATS.md — Echoes of Aetheria

This document specifies the custom file formats used by the Aetheria Engine.

---

## 1. World Map (`.amap`)
Binary format for tile-based maps.

| Offset | Type | Description |
|---|---|---|
| 0x00 | String(4) | Magic header: "AMAP" |
| 0x04 | Int | Version |
| 0x08 | Int | Map Width (tiles) |
| 0x0C | Int | Map Height (tiles) |
| 0x10 | Int | Layer Count |
| 0x14 | TileData[] | Compressed tile ID arrays (one per layer) |
| EOF-X | EntitySpawn[] | Entity IDs, positions, and initial properties |

**Example (JSON representation):**
```json
{
  "header": "AMAP",
  "version": 1,
  "width": 100, "height": 100,
  "layers": [
    { "name": "ground", "data": [1, 1, 2, 1, ...] },
    { "name": "collision", "data": [0, 1, 0, 0, ...] }
  ],
  "spawns": [
    { "type": "PLAYER_SPAWN", "x": 160, "y": 160 },
    { "type": "NPC", "id": "silas", "x": 200, "y": 180 }
  ]
}
```

---

## 2. Dialogue Script (`.adlg`)
Scripting format for branching conversations.

**Format:**
```
[NODE_ID]
TEXT: "Dialogue text here."
CHOICE: "Option text" -> TARGET_NODE_ID
CONDITION: FLAG_NAME == VALUE
ACTION: SET_FLAG FLAG_NAME VALUE
```

**Example:**
```
[START]
TEXT: "Welcome to the Wastes, boy. Found anything good?"
CHOICE: "Just junk, Silas." -> JUNK_RESPONSE
CHOICE: "Found a pendant." -> PENDANT_RESPONSE (CONDITION: HAS_PENDANT == true)

[JUNK_RESPONSE]
TEXT: "Always junk. World's full of it."
ACTION: SET_FLAG SILAS_MET true
```

---

## 3. Save Data (`.asave`)
Binary snapshot of game state.

| Field | Type | Description |
|---|---|---|
| Header | String(4) | "ASAV" |
| Slot | Int | 0, 1, or 2 |
| Chapter | Int | Current active chapter (1-7) |
| MapID | String | Current map file path |
| PlayerPos | Float(2) | X, Y coordinates |
| Flags | Map<String, Val> | All active StoryFlags |
| Inventory | List<ItemID> | Current player items |

---

## 4. Configuration (`.acfg`)
Plain text key-value pairs for settings.

**Example:**
```
# Display
window_scale=4
vsync=true
show_fps=true

# Audio
master_volume=0.8
music_volume=0.6
sfx_volume=1.0

# Input Bindings
MOVE_UP=VK_W
MOVE_DOWN=VK_S
INTERACT=VK_E
```
