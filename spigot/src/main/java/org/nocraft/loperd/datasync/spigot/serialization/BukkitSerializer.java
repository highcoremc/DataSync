package org.nocraft.loperd.datasync.spigot.serialization;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.nocraft.loperd.datasync.spigot.player.PlayerData;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;

public class BukkitSerializer {

    public static final int VERSION = 3;

    public static String toByteArray(Object data) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new BukkitObjectOutputStream(bos)) {
            out.writeInt(VERSION);
            out.writeObject(data);
            out.flush();
            return Base64Coder.encodeLines(bos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static PlayerData fromByteArray(String encodedData) throws VersionMismatchException, IOException, ClassNotFoundException {
        byte[] data = Base64Coder.decodeLines(encodedData);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInput in = new BukkitObjectInputStream(bis)) {
            int version = in.readInt();
            if (version != VERSION) {
                throw new VersionMismatchException(version, VERSION);
            }

            return (PlayerData) in.readObject();
        }
    }
}
