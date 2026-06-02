package com.aetheria.save;

import java.util.List;
import java.util.Map;

public record SaveData(
    int slot,
    int chapter,
    String mapId,
    float playerX,
    float playerY,
    Map<String, Object> flags,
    List<String> inventory
) {
}
