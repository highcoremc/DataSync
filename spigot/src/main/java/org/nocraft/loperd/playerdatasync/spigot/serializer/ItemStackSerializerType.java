package org.nocraft.loperd.playerdatasync.spigot.serializer;

public enum ItemStackSerializerType {

    BUKKIT("bukkit"),
    JSON("json");

    private final String serializer;

    ItemStackSerializerType(String serializer) {
        this.serializer = serializer;
    }

    public String getValue() {
        return this.serializer;
    }
}
