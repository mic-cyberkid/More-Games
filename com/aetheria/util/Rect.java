package com.aetheria.util;

public record Rect(float x, float y, float w, float h) {
    public boolean intersects(Rect other) {
        return x < other.x + other.w && x + w > other.x &&
               y < other.y + other.h && y + h > other.y;
    }
}
