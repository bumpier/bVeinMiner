package net.bumpier.bveinminer.managers;

import net.bumpier.bveinminer.BVeinMiner;
import net.bumpier.bveinminer.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ConfigManager {

    private final BVeinMiner plugin;
    private FileConfiguration messagesConfig;
    private File messagesFile;
    private Set<Material> allowedOresCache;

    public ConfigManager(BVeinMiner plugin) {
        this.plugin = plugin;
    }

    public void loadConfigs() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        loadAllowedOres();

        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            try (InputStream in = plugin.getResource("messages.yml")) {
                Files.copy(Objects.requireNonNull(in), messagesFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    private void loadAllowedOres() {
        this.allowedOresCache = new HashSet<>();
        for (String materialName : plugin.getConfig().getStringList("vein-miner.ores")) {
            try {
                Material material = Material.valueOf(materialName.toUpperCase());
                this.allowedOresCache.add(material);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid material name in config.yml: " + materialName);
            }
        }
    }

    public boolean isDebug() {
        return plugin.getConfig().getBoolean("debug", false);
    }

    public int getDefaultMaxBlocks() {
        return plugin.getConfig().getInt("vein-miner.default-max-blocks", 64);
    }

    public Set<Material> getAllowedOres() {
        return allowedOresCache;
    }

    public String getMessage(String path) {
        String rawMessage = messagesConfig.getString(path, "&cMessage not found: " + path);
        String prefix = messagesConfig.getString("prefix", "&8[&6bVeinMiner&8] ");
        return ChatUtils.colorize(prefix + rawMessage);
    }

    public String getMessage(String path, String placeholder, String value) {
        String rawMessage = messagesConfig.getString(path, "&cMessage not found: " + path);
        rawMessage = rawMessage.replace(placeholder, value);
        String prefix = messagesConfig.getString("prefix", "&8[&6bVeinMiner&8] ");
        return ChatUtils.colorize(prefix + rawMessage);
    }

    public String getRawMessage(String path) {
        return ChatUtils.colorize(messagesConfig.getString(path, "&cMessage not found: " + path));
    }
}