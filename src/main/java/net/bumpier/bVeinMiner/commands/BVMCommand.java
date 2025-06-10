package net.bumpier.bveinminer.commands;

import net.bumpier.bveinminer.managers.ConfigManager;
import net.bumpier.bveinminer.utils.DebugLogger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class BVMCommand implements CommandExecutor, TabCompleter {

    private final ConfigManager configManager;
    private final DebugLogger debugLogger;

    public BVMCommand(ConfigManager configManager, DebugLogger debugLogger) {
        this.configManager = configManager;
        this.debugLogger = debugLogger;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("bveinminer.reload")) {
                sender.sendMessage(configManager.getMessage("no-permission-reload"));
                return true;
            }

            configManager.loadConfigs();
            sender.sendMessage(configManager.getMessage("reload-success"));
            debugLogger.log("Plugin configurations reloaded by " + sender.getName() + ".");
            return true;
        }

        sender.sendMessage(configManager.getMessage("invalid-admin-usage"));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            if (sender.hasPermission("bveinminer.reload")) {
                return List.of("reload").stream()
                        .filter(s -> s.startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }
        return List.of();
    }
}