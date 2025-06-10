package net.bumpier.bveinminer.utils;

import net.bumpier.bveinminer.BVeinMiner;
import net.bumpier.bveinminer.managers.ConfigManager;

import java.util.logging.Logger;

public class DebugLogger {

    private final Logger logger;
    private final ConfigManager configManager;
    private final String prefix = "[bVeinMiner][DEBUG] ";

    public DebugLogger(BVeinMiner plugin, ConfigManager configManager) {
        this.logger = plugin.getLogger();
        this.configManager = configManager;
    }

    public void log(String message) {
        if (configManager.isDebug()) {
            logger.info(prefix + message);
        }
    }
}