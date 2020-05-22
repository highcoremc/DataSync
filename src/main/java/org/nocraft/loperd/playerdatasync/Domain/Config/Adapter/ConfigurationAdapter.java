package org.nocraft.loperd.playerdatasync.Domain.Config.Adapter;

import org.nocraft.loperd.playerdatasync.NoPlayerDataSync;

import java.util.List;
import java.util.Map;

public interface ConfigurationAdapter {

    NoPlayerDataSync getPlugin();

    void reload();

    String getString(String path, String def);

    int getInteger(String path, int def);

    boolean getBoolean(String path, boolean def);

    List<String> getStringList(String path, List<String> def);

    List<String> getKeys(String path, List<String> def);

    Map<String, String> getStringMap(String path, Map<String, String> def);

}
