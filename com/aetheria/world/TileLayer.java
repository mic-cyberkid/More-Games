package com.aetheria.world;

public final class TileLayer {
    private final int width, height;
    private final int[] tiles;

    public TileLayer(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new int[width * height];
    }

    public void setTile(int x, int y, int id) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            tiles[y * width + x] = id;
        }
    }

    public int getTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return tiles[y * width + x];
        }
        return 0;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
