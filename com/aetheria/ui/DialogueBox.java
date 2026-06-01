package com.aetheria.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import com.aetheria.assets.AssetManager;

public final class DialogueBox {
    private String fullText = "";
    private String visibleText = "";
    private double charTimer = 0;
    private double charDelay = 0.025; // 40 chars/sec
    private int charIndex = 0;

    public void setText(String text) {
        this.fullText = text;
        this.visibleText = "";
        this.charIndex = 0;
        this.charTimer = 0;
    }

    public void update(double dt) {
        if (charIndex < fullText.length()) {
            charTimer += dt;
            if (charTimer >= charDelay) {
                charTimer = 0;
                charIndex++;
                visibleText = fullText.substring(0, charIndex);
            }
        }
    }

    public void skip() {
        charIndex = fullText.length();
        visibleText = fullText;
    }

    public boolean isFinished() {
        return charIndex >= fullText.length();
    }

    public void render(Graphics2D g, int x, int y, int w, int h) {
        // Draw background
        g.setColor(new Color(0, 0, 0, 220));
        g.fillRect(x, y, w, h);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, w, h);

        // Draw text
        g.setFont(new Font("Monospaced", Font.PLAIN, 10));
        drawWrappedText(g, visibleText, x + 10, y + 20, w - 20);
    }

    private void drawWrappedText(Graphics2D g, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g.getFontMetrics();
        int lineHeight = fm.getHeight();
        int curX = x;
        int curY = y;

        String[] words = text.split(" ");
        for (String word : words) {
            int wordWidth = fm.stringWidth(word + " ");
            if (curX + wordWidth > x + maxWidth) {
                curX = x;
                curY += lineHeight;
            }
            g.drawString(word + " ", curX, curY);
            curX += wordWidth;
        }
    }
}
