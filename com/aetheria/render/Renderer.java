package com.aetheria.render;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public final class Renderer {
    private final Graphics2D g;
    private Camera camera;

    public Renderer(Graphics2D g) {
        this.g = g;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void applyTransform() {
        if (camera != null) {
            g.translate(-camera.getX(), -camera.getY());
        }
    }

    public void resetTransform() {
        g.setTransform(new AffineTransform());
    }

    public Graphics2D g() {
        return g;
    }
}
