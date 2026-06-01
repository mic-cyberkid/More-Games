package com.aetheria.render;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class ParticleRenderer {
    private static final int MAX_PARTICLES = 1024;

    private static class Particle {
        float x, y, vx, vy;
        float life, maxLife;
        Color color;
        boolean active;
    }

    private final Particle[] pool = new Particle[MAX_PARTICLES];
    private final Random random = new Random();

    public ParticleRenderer() {
        for (int i = 0; i < MAX_PARTICLES; i++) pool[i] = new Particle();
    }

    public void spawn(float x, float y, float vx, float vy, float life, Color color) {
        for (Particle p : pool) {
            if (!p.active) {
                p.x = x; p.y = y; p.vx = vx; p.vy = vy;
                p.life = p.maxLife = life;
                p.color = color;
                p.active = true;
                return;
            }
        }
    }

    public void update(double dt) {
        for (Particle p : pool) {
            if (p.active) {
                p.x += p.vx * dt;
                p.y += p.vy * dt;
                p.life -= dt;
                if (p.life <= 0) p.active = false;
            }
        }
    }

    public void render(Graphics2D g) {
        for (Particle p : pool) {
            if (p.active) {
                float alpha = p.life / p.maxLife;
                g.setColor(new Color(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), (int)(alpha * 255)));
                g.fillRect((int)p.x, (int)p.y, 2, 2);
            }
        }
    }
}
