package com.aetheria.util;

public final class MathUtils {
    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    public static float lerp(float start, float end, float t) {
        return start + t * (end - start);
    }
}
