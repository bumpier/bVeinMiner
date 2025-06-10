package net.bumpier.bveinminer.managers;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VeinMinerManager {

    private final Set<UUID> veinMinerEnabled = new HashSet<>();
    private final Set<UUID> autoPickupEnabled = new HashSet<>();

    public boolean isVeinMinerEnabled(Player player) {
        return veinMinerEnabled.contains(player.getUniqueId());
    }

    public void toggleVeinMiner(Player player) {
        UUID uuid = player.getUniqueId();
        if (veinMinerEnabled.contains(uuid)) {
            veinMinerEnabled.remove(uuid);
        } else {
            veinMinerEnabled.add(uuid);
        }
    }

    public void setVeinMiner(Player player, boolean enabled) {
        UUID uuid = player.getUniqueId();
        if (enabled) {
            veinMinerEnabled.add(uuid);
        } else {
            veinMinerEnabled.remove(uuid);
        }
    }

    public boolean isAutoPickupEnabled(Player player) {
        return autoPickupEnabled.contains(player.getUniqueId());
    }

    public void toggleAutoPickup(Player player) {
        UUID uuid = player.getUniqueId();
        if (autoPickupEnabled.contains(uuid)) {
            autoPickupEnabled.remove(uuid);
        } else {
            autoPickupEnabled.add(uuid);
        }
    }
}