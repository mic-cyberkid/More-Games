package com.aetheria.assets;

import com.aetheria.util.Logger;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public final class AssetManager {
    private static final AssetManager INSTANCE = new AssetManager();
    public static AssetManager get() { return INSTANCE; }

    private final Map<String, BufferedImage> images = new HashMap<>();

    private AssetManager() {}

    public BufferedImage getImage(String path) {
        if (images.containsKey(path)) {
            return images.get(path);
        }

        try {
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream(path));
            if (img == null) throw new IOException("Resource not found: " + path);
            images.put(path, img);
            return img;
        } catch (Exception e) {
            Logger.warn(AssetManager.class, "Failed to load image: " + path + " - Using placeholder.");
            return createPlaceholder();
        }
    }

    private BufferedImage createPlaceholder() {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.MAGENTA);
        g.fillRect(0, 0, 16, 16);
        g.dispose();
        return img;
    }
}
