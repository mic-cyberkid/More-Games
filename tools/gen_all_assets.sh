#!/bin/bash
set -e

echo "Starting asset generation..."

# Create final asset directories
mkdir -p assets/sheets/player
mkdir -p assets/tiles
mkdir -p assets/ui
mkdir -p assets/fonts
mkdir -p assets/audio/sfx
mkdir -p assets/audio/music

# Run Python generators
python3 tools/sprites/gen_player.py
python3 tools/sprites/gen_tilesets.py
python3 tools/sprites/gen_ui.py

# Create dummy audio files if they don't exist (since we can't easily generate .wav)
touch assets/audio/sfx/placeholder.wav
touch assets/audio/music/placeholder.wav

echo "Asset generation complete."
