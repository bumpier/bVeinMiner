package net.bumpier.bveinminer.listeners;

import net.bumpier.bveinminer.managers.ConfigManager;
import net.bumpier.bveinminer.managers.VeinMinerManager;
import net.bumpier.bveinminer.utils.DebugLogger;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class BlockBreakListener implements Listener {

    private final VeinMinerManager veinMinerManager;
    private final DebugLogger debugLogger;
    private final ConfigManager configManager;

    public BlockBreakListener(VeinMinerManager veinMinerManager, DebugLogger debugLogger, ConfigManager configManager) {
        this.veinMinerManager = veinMinerManager;
        this.debugLogger = debugLogger;
        this.configManager = configManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();

        if (player.getGameMode() == GameMode.CREATIVE || !veinMinerManager.isVeinMinerEnabled(player)) {
            return;
        }

        Block startBlock = event.getBlock();
        Material oreType = startBlock.getType();

        if (!configManager.getAllowedOres().contains(oreType)) {
            return;
        }

        // Check if the tool is effective by seeing if it would produce any drops.
        // For ores, an empty drop list implies an incorrect tool (e.g., hand vs stone).
        if (startBlock.getDrops(tool).isEmpty()) {
            debugLogger.log("Player " + player.getName() + " tried to vein mine with an invalid tool for " + oreType);
            return; // Do not cancel the event, let the server handle the ineffective break.
        }

        // Take full control of the breaking process
        event.setCancelled(true);

        debugLogger.log("Player " + player.getName() + " broke an ore (" + oreType + ") with Vein Miner enabled.");

        boolean hasAutoPickup = player.hasPermission("bveinminer.autopickup") && veinMinerManager.isAutoPickupEnabled(player);
        if (hasAutoPickup) {
            debugLogger.log(player.getName() + " has auto-pickup enabled.");
        }

        int maxBlocks = getPlayerMaxBlocks(player);
        int blockLimit = (maxBlocks == -1) ? Integer.MAX_VALUE : maxBlocks;

        Queue<Block> blocksToProcess = new LinkedList<>();
        blocksToProcess.add(startBlock);

        Set<Block> visited = new HashSet<>();
        visited.add(startBlock);

        int blocksBroken = 0;

        while (!blocksToProcess.isEmpty()) {
            if (blocksBroken >= blockLimit) {
                debugLogger.log("Vein miner limit of " + blockLimit + " reached for " + player.getName());
                break;
            }

            Block currentBlock = blocksToProcess.poll();

            if (shouldStopBreaking(player)) {
                break;
            }

            // Core breaking logic for every block
            if (hasAutoPickup) {
                Collection<ItemStack> drops = currentBlock.getDrops(tool);
                HashMap<Integer, ItemStack> remainingItems = player.getInventory().addItem(drops.toArray(new ItemStack[0]));
                if (!remainingItems.isEmpty()) {
                    for (ItemStack item : remainingItems.values()) {
                        player.getWorld().dropItemNaturally(currentBlock.getLocation(), item);
                    }
                    debugLogger.log("Player " + player.getName() + "'s inventory is full, dropping items on ground.");
                }
            } else {
                currentBlock.breakNaturally(tool);
            }

            currentBlock.setType(Material.AIR);
            blocksBroken++;
            damageTool(player, 1);

            // Find adjacent blocks
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;
                        Block relative = currentBlock.getRelative(x, y, z);
                        if (relative.getType() == oreType && !visited.contains(relative)) {
                            visited.add(relative);
                            blocksToProcess.add(relative);
                        }
                    }
                }
            }
        }

        debugLogger.log("Vein Miner broke a total of " + blocksBroken + " blocks for " + player.getName());

        if (blocksBroken > 1) {
            player.sendMessage(configManager.getMessage("veinminer-complete", "{blocks}", String.valueOf(blocksBroken)));
        }
    }

    private int getPlayerMaxBlocks(Player player) {
        if (player.hasPermission("bveinminer.max.unlimited")) {
            debugLogger.log("Player " + player.getName() + " has unlimited vein mining.");
            return -1;
        }

        int maxBlocks = -1;
        String prefix = "bveinminer.max.";

        for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
            String perm = permInfo.getPermission();
            if (perm.startsWith(prefix) && permInfo.getValue()) {
                String numberPart = perm.substring(prefix.length());
                if(numberPart.equals("*")) continue;

                try {
                    int currentLimit = Integer.parseInt(numberPart);
                    if (currentLimit > maxBlocks) {
                        maxBlocks = currentLimit;
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid formats
                }
            }
        }

        if (maxBlocks != -1) {
            debugLogger.log("Player " + player.getName() + " has a custom vein miner limit of " + maxBlocks);
            return maxBlocks;
        }

        int defaultLimit = configManager.getDefaultMaxBlocks();
        debugLogger.log("Player " + player.getName() + " is using the default limit of " + defaultLimit);
        return defaultLimit;
    }

    private boolean shouldStopBreaking(Player player) {
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool.getType().isAir()) return true;

        ItemMeta meta = tool.getItemMeta();
        if (meta instanceof Damageable damageable) {
            if(damageable.getDamage() >= tool.getType().getMaxDurability()){
                debugLogger.log("Tool broke for " + player.getName());
                return true;
            }
        }
        return false;
    }

    private void damageTool(Player player, int amount) {
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool.getType().isAir() || !(tool.getItemMeta() instanceof Damageable)) {
            return;
        }

        Damageable damageable = (Damageable) tool.getItemMeta();
        int newDamage = damageable.getDamage() + amount;

        if (newDamage >= tool.getType().getMaxDurability()) {
            tool.setAmount(0);
            player.updateInventory();
            player.playEffect(org.bukkit.EntityEffect.BREAK_EQUIPMENT_MAIN_HAND);
        } else {
            damageable.setDamage(newDamage);
            tool.setItemMeta(damageable);
        }
    }
}