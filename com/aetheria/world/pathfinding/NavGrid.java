package com.aetheria.world.pathfinding;

import com.aetheria.world.WorldMap;

public final class NavGrid {
    private final int width, height;
    private final boolean[] walkable;

    public NavGrid(WorldMap map) {
        this.width = map.getWidth();
        this.height = map.getHeight();
        this.walkable = new boolean[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                walkable[y * width + x] = !map.isSolid(x, y);
            }
        }
    }

    public boolean isWalkable(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return false;
        return walkable[y * width + x];
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
