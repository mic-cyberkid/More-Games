package com.aetheria.world;

import java.util.ArrayList;
import java.util.List;

public final class WorldMap {
    private final int width, height;
    private final List<TileLayer> layers = new ArrayList<>();
    private final ObjectLayer objectLayer = new ObjectLayer();

    public WorldMap(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void addLayer(TileLayer layer) {
        layers.add(layer);
    }

    public List<TileLayer> getLayers() {
        return layers;
    }

    public ObjectLayer getObjectLayer() {
        return objectLayer;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public boolean isSolid(int x, int y) {
        for (TileLayer layer : layers) {
            if (TileRegistry.get(layer.getTile(x, y)).solid()) {
                return true;
            }
        }
        return false;
    }
}
