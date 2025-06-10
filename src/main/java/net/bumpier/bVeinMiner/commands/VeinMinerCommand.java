package net.bumpier.bveinminer.commands;

import net.bumpier.bveinminer.BVeinMiner;
import net.bumpier.bveinminer.managers.ConfigManager;
import net.bumpier.bveinminer.managers.VeinMinerManager;
import net.bumpier.bveinminer.utils.DebugLogger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VeinMinerCommand implements CommandExecutor, TabCompleter {

    private final VeinMinerManager veinMinerManager;
    private final ConfigManager configManager;
    private final DebugLogger debugLogger;
    private final BVeinMiner plugin;

    public VeinMinerCommand(BVeinMiner plugin, VeinMinerManager veinMinerManager, ConfigManager configManager, DebugLogger debugLogger) {
        this.plugin = plugin;
        this.veinMinerManager = veinMinerManager;
        this.configManager = configManager;
        this.debugLogger = debugLogger;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (!player.hasPermission("bveinminer.use")) {
            player.sendMessage(configManager.getMessage("no-permission"));
            return true;
        }

        if (args.length == 0) {
            veinMinerManager.toggleVeinMiner(player);
            if (veinMinerManager.isVeinMinerEnabled(player)) {
                player.sendMessage(configManager.getMessage("veinminer-enabled"));
            } else {
                player.sendMessage(configManager.getMessage("veinminer-disabled"));
            }
            debugLogger.log("Player " + player.getName() + " toggled Vein Miner to " + (veinMinerManager.isVeinMinerEnabled(player) ? "ON" : "OFF"));
            return true;
        }

        if (args.length == 1) {
            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "on" -> {
                    veinMinerManager.setVeinMiner(player, true);
                    player.sendMessage(configManager.getMessage("veinminer-enabled"));
                    debugLogger.log("Player " + player.getName() + " set Vein Miner to ON");
                }
                case "off" -> {
                    veinMinerManager.setVeinMiner(player, false);
                    player.sendMessage(configManager.getMessage("veinminer-disabled"));
                    debugLogger.log("Player " + player.getName() + " set Vein Miner to OFF");
                }
                case "status" -> {
                    if (veinMinerManager.isVeinMinerEnabled(player)) {
                        player.sendMessage(configManager.getMessage("veinminer-status-on"));
                    } else {
                        player.sendMessage(configManager.getMessage("veinminer-status-off"));
                    }
                }
                case "autopickup" -> {
                    if (!player.hasPermission("bveinminer.autopickup")) {
                        player.sendMessage(configManager.getMessage("no-permission-autopickup"));
                        return true;
                    }
                    veinMinerManager.toggleAutoPickup(player);
                    if (veinMinerManager.isAutoPickupEnabled(player)) {
                        player.sendMessage(configManager.getMessage("autopickup-enabled"));
                    } else {
                        player.sendMessage(configManager.getMessage("autopickup-disabled"));
                    }
                    debugLogger.log("Player " + player.getName() + " toggled auto-pickup to " + (veinMinerManager.isAutoPickupEnabled(player) ? "ON" : "OFF"));
                }
                default -> player.sendMessage(configManager.getMessage("invalid-usage"));
            }
            return true;
        }

        player.sendMessage(configManager.getMessage("invalid-usage"));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>(List.of("on", "off", "status"));
            if (sender.hasPermission("bveinminer.autopickup")) {
                completions.add("autopickup");
            }
            return completions.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}