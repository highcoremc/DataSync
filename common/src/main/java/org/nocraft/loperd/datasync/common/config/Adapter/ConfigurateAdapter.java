package org.nocraft.loperd.datasync.common.config.Adapter;

import com.google.common.base.Splitter;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.nocraft.loperd.datasync.common.plugin.DataSyncPlugin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class ConfigurateAdapter implements ConfigurationAdapter {
    private final DataSyncPlugin plugin;
    private final Path path;
    private ConfigurationNode root;

    public ConfigurateAdapter(DataSyncPlugin plugin, Path path) {
        this.plugin = plugin;
        this.path = path;
        reload();
    }

    protected abstract ConfigurationLoader<? extends ConfigurationNode> createLoader(Path path);

    @Override
    public void reload() {
        ConfigurationLoader<? extends ConfigurationNode> loader = createLoader(this.path);
        try {
            this.root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ConfigurationNode resolvePath(String path) {
        if (this.root == null) {
            throw new RuntimeException("Config is not loaded.");
        }

        return this.root.getNode(Splitter.on('.').splitToList(path).toArray());
    }

    @Override
    public String getString(String path, String def) {
        return resolvePath(path).getString(def);
    }

    @Override
    public int getInteger(String path, int def) {
        return resolvePath(path).getInt(def);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return resolvePath(path).getBoolean(def);
    }

    @Override
    public List<String> getStringList(String path, List<String> def) {
        ConfigurationNode node = resolvePath(path);
        if (node.isVirtual()) {
            return def;
        }

        return node.getList(Object::toString);
    }

    @Override
    public List<String> getKeys(String path, List<String> def) {
        ConfigurationNode node = resolvePath(path);
        if (node.isVirtual()) {
            return def;
        }

        return node.getChildrenMap().keySet().stream().map(Object::toString).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> getStringMap(String path, Map<String, String> def) {
        ConfigurationNode node = resolvePath(path);
        if (node.isVirtual()) {
            return def;
        }

        Map<String, Object> m = (Map<String, Object>) node.getValue(Collections.emptyMap());
        return m.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().toString()));
    }

    @Override
    public DataSyncPlugin getPlugin() {
        return this.plugin;
    }
}
