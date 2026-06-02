package com.aetheria.world;

import com.aetheria.util.Logger;

public final class MapLoader {
    public static WorldMap loadStub(int w, int h) {
        Logger.info(MapLoader.class, "Loading stub map " + w + "x" + h);
        WorldMap map = new WorldMap(w, h);
        TileLayer ground = new TileLayer(w, h);
        for (int i = 0; i < w * h; i++) {
            ground.setTile(i % w, i / w, 1);
        }
        map.addLayer(ground);
        return map;
    }
}
