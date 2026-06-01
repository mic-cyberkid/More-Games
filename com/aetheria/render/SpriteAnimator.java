package com.aetheria.render;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public final class SpriteAnimator {

    public record Animation(int row, int frames, double frameDuration, boolean loop) {}

    private final SpriteSheet sheet;
    private final int frameW, frameH;
    private final Map<String, Animation> animations = new HashMap<>();

    private Animation currentAnim;
    private double    elapsedTime;
    private int       currentFrame;
    private boolean   finished;

    public SpriteAnimator(SpriteSheet sheet, int frameW, int frameH) {
        this.sheet = sheet;
        this.frameW = frameW;
        this.frameH = frameH;
    }

    public void addAnimation(String name, int row, int frames, double duration, boolean loop) {
        animations.put(name, new Animation(row, frames, duration, loop));
    }

    public void play(String name) {
        Animation anim = animations.get(name);
        if (anim == null || anim == currentAnim) return;

        currentAnim = anim;
        elapsedTime = 0;
        currentFrame = 0;
        finished = false;
    }

    public void update(double dt) {
        if (currentAnim == null || finished) return;

        elapsedTime += dt;
        if (elapsedTime >= currentAnim.frameDuration()) {
            elapsedTime = 0;
            currentFrame++;

            if (currentFrame >= currentAnim.frames()) {
                if (currentAnim.loop()) {
                    currentFrame = 0;
                } else {
                    currentFrame = currentAnim.frames() - 1;
                    finished = true;
                }
            }
        }
    }

    public BufferedImage getCurrentFrame() {
        if (currentAnim == null) return null;
        return sheet.getFrame(currentFrame * frameW, currentAnim.row() * frameH, frameW, frameH);
    }

    public boolean isFinished() { return finished; }
}
