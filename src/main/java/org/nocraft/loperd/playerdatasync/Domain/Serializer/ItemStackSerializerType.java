package org.nocraft.loperd.playerdatasync.Domain.Serializer;

public enum ItemStackSerializerType {

    BS_64("bs64"),
    JSON("json");

    private final String serializer;

    ItemStackSerializerType(String serializer) {
        this.serializer = serializer;
    }

    public String getValue() {
        return this.serializer;
    }
}
