package com.aetheria.tests;

import com.aetheria.render.Camera;
import com.aetheria.util.Assert;

public class CameraTest {
    public static void main(String[] args) {
        Camera cam = new Camera(320, 180);
        cam.setWorldBounds(1000, 1000);

        cam.follow(500, 500);
        cam.update(1.0); // Should move towards target

        Assert.that(cam.getX() > 0, "Camera should have moved. Current X: " + cam.getX());

        // Bounds check
        cam.follow(-100, -100);
        cam.update(1.0);
        Assert.that(cam.getX() >= 0, "Camera should not go out of bounds. Current X: " + cam.getX());

        java.lang.System.out.println("CameraTest PASSED");
    }
}
