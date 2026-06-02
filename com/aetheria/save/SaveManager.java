package com.aetheria.save;

import com.aetheria.util.Logger;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class SaveManager {
    private static final String SAVE_DIR = "saves/";
    private static final String EXT = ".asave";
    private static final String TMP_EXT = ".tmp";

    public SaveManager() {
        new File(SAVE_DIR).mkdirs();
    }

    public void save(SaveData data) {
        String baseName = SAVE_DIR + "slot" + data.slot();
        File actualFile = new File(baseName + EXT);
        File tempFile = new File(baseName + TMP_EXT);

        try (OutputStream out = new FileOutputStream(tempFile)) {
            SaveSerializer.serialize(data, out);
            out.flush();
        } catch (IOException e) {
            Logger.warn(SaveManager.class, "Failed to write save temp file: " + e.getMessage());
            return;
        }

        try {
            Files.move(tempFile.toPath(), actualFile.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            Logger.info(SaveManager.class, "Saved game to slot " + data.slot());
        } catch (IOException e) {
            Logger.warn(SaveManager.class, "Failed to commit save file: " + e.getMessage());
        }
    }

    public SaveData load(int slot) {
        File file = new File(SAVE_DIR + "slot" + slot + EXT);
        if (!file.exists()) return null;

        try (InputStream in = new FileInputStream(file)) {
            return SaveSerializer.deserialize(in);
        } catch (IOException e) {
            Logger.warn(SaveManager.class, "Failed to load save slot " + slot + ": " + e.getMessage());
            return null;
        }
    }
}
