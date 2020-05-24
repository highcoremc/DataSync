package org.nocraft.loperd.playerdatasync.Serializer;

import com.google.gson.JsonArray;
import org.bukkit.potion.PotionEffect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class PotionEffectsSerializer implements Serializable {
    public static JsonArray serialize(Collection<PotionEffect> potionEffects) {
        return new JsonArray();
    }

    public static Collection<PotionEffect> deserialize(String value) {
        return new ArrayList<>();
    }
}
