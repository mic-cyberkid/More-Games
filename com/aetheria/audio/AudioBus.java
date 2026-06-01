package com.aetheria.audio;

public final class AudioBus {
    private float masterVolume = 1.0f;
    private float musicVolume = 1.0f;
    private float sfxVolume = 1.0f;

    public void setMasterVolume(float vol) { masterVolume = vol; }
    public void setMusicVolume(float vol) { musicVolume = vol; }
    public void setSfxVolume(float vol) { sfxVolume = vol; }

    public float getMasterVolume() { return masterVolume; }
    public float getMusicVolume() { return musicVolume; }
    public float getSfxVolume() { return sfxVolume; }

    public float getEffectiveMusicVolume() { return masterVolume * musicVolume; }
    public float getEffectiveSfxVolume() { return masterVolume * sfxVolume; }
}
