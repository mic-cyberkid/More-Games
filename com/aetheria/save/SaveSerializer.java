package com.aetheria.save;

import java.io.*;
import java.util.*;

public final class SaveSerializer {
    private static final String HEADER = "ASAV";

    public static void serialize(SaveData data, OutputStream out) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeBytes(HEADER);
        dos.writeInt(data.slot());
        dos.writeInt(data.chapter());
        dos.writeUTF(data.mapId());
        dos.writeFloat(data.playerX());
        dos.writeFloat(data.playerY());

        // Flags
        dos.writeInt(data.flags().size());
        for (Map.Entry<String, Object> entry : data.flags().entrySet()) {
            dos.writeUTF(entry.getKey());
            Object val = entry.getValue();
            if (val instanceof Boolean) {
                dos.writeByte(0);
                dos.writeBoolean((Boolean) val);
            } else if (val instanceof Integer) {
                dos.writeByte(1);
                dos.writeInt((Integer) val);
            } else if (val instanceof String) {
                dos.writeByte(2);
                dos.writeUTF((String) val);
            } else {
                dos.writeByte(255); // Unknown
            }
        }

        // Inventory
        dos.writeInt(data.inventory().size());
        for (String item : data.inventory()) {
            dos.writeUTF(item);
        }
    }

    public static SaveData deserialize(InputStream in) throws IOException {
        DataInputStream dis = new DataInputStream(in);
        byte[] header = new byte[4];
        dis.readFully(header);
        if (!new String(header).equals(HEADER)) throw new IOException("Invalid save header");

        int slot = dis.readInt();
        int chapter = dis.readInt();
        String mapId = dis.readUTF();
        float px = dis.readFloat();
        float py = dis.readFloat();

        // Flags
        int flagCount = dis.readInt();
        Map<String, Object> flags = new HashMap<>();
        for (int i = 0; i < flagCount; i++) {
            String key = dis.readUTF();
            byte type = dis.readByte();
            Object val = switch (type) {
                case 0 -> dis.readBoolean();
                case 1 -> dis.readInt();
                case 2 -> dis.readUTF();
                default -> null;
            };
            flags.put(key, val);
        }

        // Inventory
        int invCount = dis.readInt();
        List<String> inventory = new ArrayList<>();
        for (int i = 0; i < invCount; i++) {
            inventory.add(dis.readUTF());
        }

        return new SaveData(slot, chapter, mapId, px, py, flags, inventory);
    }
}
