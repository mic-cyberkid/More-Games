package com.aetheria.world;

import com.aetheria.util.Logger;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class MapLoader {

    public static WorldMap load(String path) throws IOException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(
                MapLoader.class.getResourceAsStream(path)))) {

            byte[] header = new byte[4];
            in.readFully(header);
            if (!new String(header).equals("AMAP")) throw new IOException("Invalid map header: " + path);

            int version = in.readInt();
            int width = in.readInt();
            int height = in.readInt();
            int layerCount = in.readInt();

            WorldMap map = new WorldMap(width, height);
            for (int l = 0; l < layerCount; l++) {
                TileLayer layer = new TileLayer(width, height);
                for (int i = 0; i < width * height; i++) {
                    layer.setTile(i % width, i / width, in.readInt());
                }
                map.addLayer(layer);
            }

            return map;
        } catch (NullPointerException e) {
            throw new IOException("Map file not found: " + path);
        }
    }

    public static void save(WorldMap map, String path) throws IOException {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(path))) {
            out.writeBytes("AMAP");
            out.writeInt(1); // version
            out.writeInt(map.getWidth());
            out.writeInt(map.getHeight());
            out.writeInt(map.getLayers().size());

            for (TileLayer layer : map.getLayers()) {
                for (int y = 0; y < map.getHeight(); y++) {
                    for (int x = 0; x < map.getWidth(); x++) {
                        out.writeInt(layer.getTile(x, y));
                    }
                }
            }
        }
    }

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
