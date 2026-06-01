package com.aetheria.tests;

import com.aetheria.world.*;
import com.aetheria.util.Assert;

public class MapLoaderTest {
    public static void main(String[] args) {
        WorldMap map = MapLoader.loadStub(100, 100);
        Assert.that(map.getWidth() == 100, "Width should be 100");
        Assert.that(map.getLayers().size() == 1, "Should have 1 layer");
        Assert.that(!map.isSolid(50, 50), "Grass should not be solid");

        TileLayer wallLayer = new TileLayer(100, 100);
        wallLayer.setTile(10, 10, 2); // WALL
        map.addLayer(wallLayer);
        Assert.that(map.isSolid(10, 10), "Wall should be solid");

        System.out.println("MapLoaderTest PASSED");
    }
}
