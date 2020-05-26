package org.nocraft.loperd.playerdatasync.spigot.serializer;

import com.google.gson.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PotionEffectsSerializer implements Serializable {

    static JsonArray toJsonObject(Collection<PotionEffect> effects) {
        JsonArray result = new JsonArray();

        for (PotionEffect effect : effects) {
            result.add(serialize(effect));
        }

        return result;
    }

    static List<PotionEffect> toEffectList(String serialized) {
        JsonArray effectList = (JsonArray) new JsonParser().parse(serialized);
        List<PotionEffect> resultEffects = new ArrayList<>(effectList.size());

        for (JsonElement effectObject : effectList) {
            try {
                PotionEffect effect = deserialize((JsonObject) effectObject);

                if (effect == null) {
                    continue;
                }

                resultEffects.add(effect);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {
            }
        }

        return resultEffects;
    }

    private static JsonObject serialize(PotionEffect effect) {
        JsonObject result = new JsonObject();

        result.add("name", new JsonPrimitive(effect.getType().getName()));
        result.add("amplifier", new JsonPrimitive(effect.getAmplifier()));
        result.add("duration", new JsonPrimitive(effect.getDuration()));

        return result;
    }

    private static PotionEffect deserialize(JsonObject source) {
        String name = source.get("name").getAsString();
        int amplifier = source.get("amplifier").getAsInt();
        int duration = source.get("duration").getAsInt();

        PotionEffectType effect = PotionEffectType.getByName(name);

        if (null == effect) {
            return null;
        }

        return new PotionEffect(effect, duration, amplifier);
    }
}
