package com.aetheria.render;

import java.awt.image.BufferedImage;

public final class SpriteSheet {
    private final BufferedImage sheet;

    public SpriteSheet(BufferedImage sheet) {
        this.sheet = sheet;
    }

    public BufferedImage getFrame(int x, int y, int w, int h) {
        return sheet.getSubimage(x, y, w, h);
    }

    public int getWidth() { return sheet.getWidth(); }
    public int getHeight() { return sheet.getHeight(); }
}
