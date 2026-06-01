package com.aetheria.core.loop;

public final class FrameTimer {
    private static final int SAMPLE_SIZE = 64;
    private final double[] samples = new double[SAMPLE_SIZE];
    private int index = 0;
    private long lastTime = System.nanoTime();

    public double tick() {
        long now = System.nanoTime();
        double ms = (now - lastTime) / 1_000_000.0;
        lastTime = now;
        samples[index++ % SAMPLE_SIZE] = ms;
        return ms;
    }

    public double getAverageFps() {
        double avgMs = 0;
        for (double s : samples) avgMs += s;
        avgMs /= SAMPLE_SIZE;
        return avgMs > 0 ? 1000.0 / avgMs : 0;
    }

    public double getAverageFrameMs() {
        double sum = 0;
        for (double s : samples) sum += s;
        return sum / SAMPLE_SIZE;
    }
}
