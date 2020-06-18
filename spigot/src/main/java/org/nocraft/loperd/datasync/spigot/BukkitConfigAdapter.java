package org.nocraft.loperd.datasync.spigot;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.nocraft.loperd.datasync.common.config.Adapter.ConfigurationAdapter;
import org.nocraft.loperd.datasync.common.plugin.DataSyncPlugin;

import java.io.File;
import java.util.*;

public class BukkitConfigAdapter implements ConfigurationAdapter {

    private YamlConfiguration configuration;
    private final DataSyncPlugin plugin;
    private final File file;

    public BukkitConfigAdapter(DataSyncPlugin plugin, File file) {
        this.plugin = plugin;
        this.file = file;
        reload();
    }

    @Override
    public void reload() {
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }

    @Override
    public String getString(String path, String def) {
        return this.configuration.getString(path, def);
    }

    @Override
    public int getInteger(String path, int def) {
        return this.configuration.getInt(path, def);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return this.configuration.getBoolean(path, def);
    }

    @Override
    public List<String> getStringList(String path, List<String> def) {
        List<String> list = this.configuration.getStringList(path);
        return list == null ? def : list;
    }

    @Override
    public List<String> getKeys(String path, List<String> def) {
        ConfigurationSection section = this.configuration.getConfigurationSection(path);
        if (section == null) {
            return def;
        }

        Set<String> keys = section.getKeys(false);
        return keys == null ? def : new ArrayList<>(keys);
    }

    @Override
    public Map<String, String> getStringMap(String path, Map<String, String> def) {
        Map<String, String> map = new HashMap<>();
        ConfigurationSection section = this.configuration.getConfigurationSection(path);
        if (section == null) {
            return def;
        }

        for (String key : section.getKeys(false)) {
            map.put(key, section.getString(key));
        }

        return map;
    }

    @Override
    public DataSyncPlugin getPlugin() {
        return this.plugin;
    }
}
