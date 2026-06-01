package com.aetheria.tests;

import com.aetheria.world.*;
import com.aetheria.world.pathfinding.*;
import com.aetheria.util.Assert;
import java.util.List;

public class PathfinderTest {
    public static void main(String[] args) {
        WorldMap map = new WorldMap(64, 64);
        TileLayer ground = new TileLayer(64, 64);
        for(int i=0; i<64*64; i++) ground.setTile(i%64, i/64, 1);
        map.addLayer(ground);

        TileLayer walls = new TileLayer(64, 64);
        // Create a wall from (10, 0) to (10, 50)
        for(int y=0; y<50; y++) walls.setTile(10, y, 2);
        map.addLayer(walls);

        NavGrid grid = new NavGrid(map);
        Pathfinder pathfinder = new Pathfinder();

        // Find path around the wall
        List<int[]> path = pathfinder.findPath(grid, 0, 0, 20, 0);

        Assert.that(!path.isEmpty(), "Path should not be empty");
        for(int[] p : path) {
            Assert.that(!map.isSolid(p[0], p[1]), "Path should not go through walls");
        }

        System.out.println("PathfinderTest PASSED, path length: " + path.size());
    }
}
