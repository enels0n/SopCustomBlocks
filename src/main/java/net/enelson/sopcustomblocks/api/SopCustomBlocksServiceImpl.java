package net.enelson.sopcustomblocks.api;

import net.enelson.sopcustomblocks.SopCustomBlocks;
import net.enelson.sopcustomblocks.api.event.SopCustomBlockBreakCause;
import net.enelson.sopcustomblocks.api.event.SopCustomBlockBreakEvent;
import net.enelson.sopcustomblocks.api.event.SopCustomBlockPlaceEvent;
import net.enelson.sopcustomblocks.managers.blocks.BlockManager;
import net.enelson.sopcustomblocks.managers.blocks.CustomBlock;
import net.enelson.sopcustomblocks.managers.config.ConfigType;
import net.enelson.sopcustomblocks.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class SopCustomBlocksServiceImpl implements SopCustomBlocksService {

    private final SopCustomBlocks plugin;

    public SopCustomBlocksServiceImpl(SopCustomBlocks plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isAvailable() {
        return plugin != null && plugin.isEnabled() && plugin.getBlockManager() != null;
    }

    @Override
    public void placeBlock(String id, Location location) {
        if (!isAvailable() || Utils.normalizeId(id) == null) {
            return;
        }
        SopCustomBlockPlaceEvent event = new SopCustomBlockPlaceEvent(id, location, null, 0.0F, 0.0F);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        CustomBlock block = plugin.getBlockManager().addBlock(id, location, 0.0F, 0.0F);
        applyPhysicalBlock(id, location, block);
    }

    @Override
    public void placeBlock(String id, Location location, float yaw, float pitch) {
        if (!isAvailable() || Utils.normalizeId(id) == null) {
            return;
        }
        SopCustomBlockPlaceEvent event = new SopCustomBlockPlaceEvent(id, location, null, yaw, pitch);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        CustomBlock block = plugin.getBlockManager().addBlock(id, location, yaw, pitch);
        applyPhysicalBlock(id, location, block);
    }

    @Override
    public void placeBlock(String id, Location location, Player player) {
        if (!isAvailable() || Utils.normalizeId(id) == null) {
            return;
        }
        float yaw = player != null ? player.getLocation().getYaw() : 0.0F;
        float pitch = player != null ? player.getLocation().getPitch() : 0.0F;
        SopCustomBlockPlaceEvent event = new SopCustomBlockPlaceEvent(id, location, player, yaw, pitch);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        CustomBlock block = plugin.getBlockManager().addBlock(id, location, player);
        applyPhysicalBlock(id, location, block);
    }

    @Override
    public boolean removeBlock(Location location) {
        if (!isAvailable() || location == null) {
            return false;
        }
        BlockManager blockManager = plugin.getBlockManager();
        CustomBlock block = blockManager.getBlock(location);
        if (block == null) {
            return false;
        }
        SopCustomBlockBreakEvent breakEvent = new SopCustomBlockBreakEvent(block, null, SopCustomBlockBreakCause.API);
        Bukkit.getPluginManager().callEvent(breakEvent);
        if (breakEvent.isCancelled()) {
            return false;
        }
        blockManager.breakBlock(block, null);
        return true;
    }

    @Override
    public boolean isCustomBlock(Location location) {
        return getBlock(location) != null;
    }

    @Override
    public String getBlockId(Location location) {
        CustomBlock block = getBlock(location);
        return block == null ? null : block.getId();
    }

    @Override
    public CustomBlock getBlock(Location location) {
        if (!isAvailable() || location == null) {
            return null;
        }
        return plugin.getBlockManager().getBlock(location);
    }

    @Override
    public ItemStack getBlockItem(String id) {
        if (!isAvailable() || Utils.normalizeId(id) == null) {
            return null;
        }
        return Utils.generateItem(id);
    }

    @Override
    public String getItemBlockId(ItemStack item) {
        if (!isAvailable() || item == null) {
            return null;
        }
        return Utils.getId(item);
    }

    private void applyPhysicalBlock(String id, Location location, CustomBlock block) {
        if (block == null || location == null || location.getWorld() == null) {
            return;
        }

        String replacement = plugin.getConfigManager().getString(ConfigType.BLOCKS, id + ".replacement-block");
        Material material = null;

        if (replacement != null && !replacement.trim().isEmpty()) {
            try {
                material = Material.valueOf(replacement.trim().toUpperCase());
            } catch (IllegalArgumentException ignored) {
                material = null;
            }
        }

        if (material == null) {
            ItemStack item = Utils.generateItem(id);
            if (item == null || item.getType() == Material.AIR) {
                return;
            }
            material = item.getType();
        }

        location.getBlock().setType(material);
    }
}
