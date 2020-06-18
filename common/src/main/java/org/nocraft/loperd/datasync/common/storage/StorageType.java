package org.nocraft.loperd.datasync.common.storage;

import com.google.common.collect.ImmutableList;

import java.util.List;

public enum StorageType {

    // SQL storage
    MARIADB("MariaDB", "mariadb"),
    MYSQL("MySQL", "mysql"),
    POSTGRESQL("PostgreSQL", "postgresql"),

    // NoSQL Storage
    REDIS("Redis", "redis");

    private final String name;

    private final List<String> identifiers;

    StorageType(String name, String... identifiers) {
        this.name = name;
        this.identifiers = ImmutableList.copyOf(identifiers);
    }

    public static StorageType parse(String name, StorageType def) {
        for (StorageType t : values()) {
            for (String id : t.getIdentifiers()) {
                if (id.equalsIgnoreCase(name)) {
                    return t;
                }
            }
        }
        return def;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getIdentifiers() {
        return this.identifiers;
    }
}
