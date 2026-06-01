package com.aetheria.tools;

import com.aetheria.world.*;
import com.aetheria.util.Rect;
import java.io.IOException;

public class MapGenerator {
    public static void main(String[] args) throws IOException {
        generateMap("assets/maps/wastes_start.amap", 20, 20, "wastes_ruins.amap", 288, 16);
        generateMap("assets/maps/wastes_ruins.amap", 20, 20, "wastes_cave.amap", 16, 288);
        generateMap("assets/maps/wastes_cave.amap", 20, 20, "wastes_start.amap", 16, 16);
    }

    private static void generateMap(String path, int w, int h, String target, float tx, float ty) throws IOException {
        WorldMap map = new WorldMap(w, h);
        TileLayer layer = new TileLayer(w, h);
        for (int i = 0; i < w * h; i++) layer.setTile(i % w, i / w, 1);
        // Add walls
        for (int x = 0; x < w; x++) { layer.setTile(x, 0, 2); layer.setTile(x, h - 1, 2); }
        for (int y = 0; y < h; y++) { layer.setTile(0, y, 2); layer.setTile(w - 1, y, 2); }

        map.addLayer(layer);

        // Add transition trigger at bottom right
        map.getObjectLayer().addTrigger(new ObjectLayer.MapTrigger(
            new Rect(w * 16 - 32, h * 16 - 32, 32, 32),
            new WorldTransition(target, tx, ty)
        ));

        MapLoader.save(map, path);
        System.out.println("Generated " + path);
    }
}
