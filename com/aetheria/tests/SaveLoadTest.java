package com.aetheria.tests;

import com.aetheria.save.*;
import com.aetheria.util.Assert;
import java.io.*;
import java.util.*;

public class SaveLoadTest {
    public static void main(String[] args) throws IOException {
        Map<String, Object> flags = new HashMap<>();
        flags.put("CH1_SILAS_SAVED", true);
        flags.put("GOLD", 100);

        List<String> inv = List.of("IRON_SWORD", "AETHER_BOTTLE");

        SaveData data = new SaveData(1, 1, "wastes_start.amap", 150.5f, 200.0f, flags, inv);

        // Test Serialization
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SaveSerializer.serialize(data, out);

        byte[] bytes = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        SaveData loaded = SaveSerializer.deserialize(in);

        Assert.that(loaded.slot() == 1, "Slot mismatch");
        Assert.that(loaded.chapter() == 1, "Chapter mismatch");
        Assert.that("wastes_start.amap".equals(loaded.mapId()), "MapID mismatch");
        Assert.that(loaded.playerX() == 150.5f, "PlayerX mismatch");
        Assert.that(loaded.flags().get("GOLD").equals(100), "Flag mismatch");
        Assert.that(loaded.inventory().size() == 2, "Inventory size mismatch");

        // Test Manager Atomic Save
        SaveManager manager = new SaveManager();
        manager.save(data);
        SaveData managerLoaded = manager.load(1);
        Assert.notNull(managerLoaded, "Manager failed to load");
        Assert.that(managerLoaded.playerY() == 200.0f, "Manager load value mismatch");

        java.lang.System.out.println("SaveLoadTest PASSED");
    }
}
