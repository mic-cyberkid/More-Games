package com.aetheria.render;

import com.aetheria.util.MathUtils;
import java.util.Random;

public final class Camera {
    private float x, y;
    private float targetX, targetY;
    private float lerpSpeed = 8.0f;
    private float deadzoneW = 48f, deadzoneH = 32f;
    private int worldW, worldH;
    private int viewportW, viewportH;

    private float shakeIntensity = 0f;
    private float shakeDuration = 0f;
    private final Random random = new Random();

    public Camera(int viewportW, int viewportH) {
        this.viewportW = viewportW;
        this.viewportH = viewportH;
    }

    public void setWorldBounds(int worldW, int worldH) {
        this.worldW = worldW;
        this.worldH = worldH;
    }

    public void update(double dt) {
        // Smooth follow
        float dx = Math.abs(targetX - x);
        float dy = Math.abs(targetY - y);

        if (dx > deadzoneW / 2) {
            float moveX = (targetX > x ? dx - deadzoneW / 2 : -(dx - deadzoneW / 2));
            x += moveX * lerpSpeed * dt;
        }
        if (dy > deadzoneH / 2) {
            float moveY = (targetY > y ? dy - deadzoneH / 2 : -(dy - deadzoneH / 2));
            y += moveY * lerpSpeed * dt;
        }

        // Clamp to world bounds
        x = MathUtils.clamp(x, viewportW / 2f, worldW - viewportW / 2f);
        y = MathUtils.clamp(y, viewportH / 2f, worldH - viewportH / 2f);

        // Shake
        if (shakeDuration > 0) {
            shakeDuration -= dt;
            if (shakeDuration <= 0) shakeIntensity = 0;
        }
    }

    public void follow(float tx, float ty) {
        this.targetX = tx;
        this.targetY = ty;
    }

    public void shake(float intensity, float duration) {
        this.shakeIntensity = intensity;
        this.shakeDuration = duration;
    }

    public float getX() {
        float sx = (shakeDuration > 0) ? (random.nextFloat() * 2 - 1) * shakeIntensity : 0;
        return x - viewportW / 2f + sx;
    }

    public float getY() {
        float sy = (shakeDuration > 0) ? (random.nextFloat() * 2 - 1) * shakeIntensity : 0;
        return y - viewportH / 2f + sy;
    }
}
