package com.aetheria.world;

import java.util.HashMap;
import java.util.Map;

public final class TileRegistry {
    private static final Map<Integer, Tile> tiles = new HashMap<>();

    static {
        register(new Tile(0, "AIR", false));
        register(new Tile(1, "GRASS", false));
        register(new Tile(2, "WALL", true));
    }

    public static void register(Tile tile) {
        tiles.put(tile.id(), tile);
    }

    public static Tile get(int id) {
        return tiles.getOrDefault(id, tiles.get(0));
    }
}
