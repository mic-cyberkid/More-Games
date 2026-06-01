package com.aetheria.audio;

import com.aetheria.util.Logger;
import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class AudioEngine {
    private final AudioBus bus;
    private final Map<String, Clip> clips = new HashMap<>();
    private Clip currentMusic;

    public AudioEngine(AudioBus bus) {
        this.bus = bus;
    }

    public void playSfx(String path) {
        Clip clip = getClip(path);
        if (clip != null) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float volume = bus.getEffectiveSfxVolume();
            float dB = (float) (Math.log(volume == 0 ? 0.0001 : volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public void playMusic(String path, boolean loop) {
        if (currentMusic != null) {
            currentMusic.stop();
        }

        Clip clip = getClip(path);
        if (clip != null) {
            currentMusic = clip;
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float volume = bus.getEffectiveMusicVolume();
            float dB = (float) (Math.log(volume == 0 ? 0.0001 : volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
            clip.setFramePosition(0);
            if (loop) clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        }
    }

    private Clip getClip(String path) {
        if (clips.containsKey(path)) return clips.get(path);

        try (var is = getClass().getResourceAsStream(path)) {
            if (is == null) throw new IOException("Audio resource not found: " + path);
            AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clips.put(path, clip);
            return clip;
        } catch (Exception e) {
            Logger.warn(AudioEngine.class, "Failed to load audio: " + path + " (" + e.getMessage() + ")");
            return null;
        }
    }
}
