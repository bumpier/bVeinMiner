package net.bumpier.bveinminer;

import net.bumpier.bveinminer.commands.BVMCommand;
import net.bumpier.bveinminer.commands.VeinMinerCommand;
import net.bumpier.bveinminer.listeners.BlockBreakListener;
import net.bumpier.bveinminer.managers.ConfigManager;
import net.bumpier.bveinminer.managers.VeinMinerManager;
import net.bumpier.bveinminer.utils.DebugLogger;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class BVeinMiner extends JavaPlugin {

    private ConfigManager configManager;
    private DebugLogger debugLogger;
    private VeinMinerManager veinMinerManager;

    @Override
    public void onEnable() {
        // Initialize managers and utilities
        this.configManager = new ConfigManager(this);
        this.debugLogger = new DebugLogger(this, configManager);
        this.veinMinerManager = new VeinMinerManager();

        // Load configurations
        configManager.loadConfigs();

        // Register commands
        Objects.requireNonNull(getCommand("veinminer")).setExecutor(new VeinMinerCommand(this, veinMinerManager, configManager, debugLogger));
        Objects.requireNonNull(getCommand("bvm")).setExecutor(new BVMCommand(configManager, debugLogger));

        // Register listener
        getServer().getPluginManager().registerEvents(new BlockBreakListener(veinMinerManager, debugLogger, configManager), this);

        debugLogger.log("bVeinMiner enabled successfully.");
    }

    @Override
    public void onDisable() {
        debugLogger.log("bVeinMiner disabled.");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}