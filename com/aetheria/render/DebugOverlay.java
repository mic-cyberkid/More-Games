package com.aetheria.render;

import com.aetheria.core.loop.FrameTimer;
import java.awt.*;

public final class DebugOverlay {
    private boolean visible = false;

    public void toggle() { visible = !visible; }

    public void render(Graphics2D g, FrameTimer timer) {
        if (!visible) return;

        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(5, 5, 200, 80);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));

        g.drawString(String.format("FPS: %.1f", timer.getAverageFps()), 10, 20);
        g.drawString(String.format("Frame: %.2f ms", timer.getAverageFrameMs()), 10, 35);

        long totalMemory = Runtime.getRuntime().totalMemory() / 1024 / 1024;
        long freeMemory = Runtime.getRuntime().freeMemory() / 1024 / 1024;
        g.drawString(String.format("Heap: %dMB / %dMB", (totalMemory - freeMemory), totalMemory), 10, 50);

        g.drawString("F3: Toggle Debug", 10, 75);
    }
}
