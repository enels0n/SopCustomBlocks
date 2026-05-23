package net.enelson.sopcustomblocks.api;

import net.enelson.sopcustomblocks.SopCustomBlocks;
import net.enelson.sopcustomblocks.managers.blocks.BlockManager;
import net.enelson.sopcustomblocks.managers.blocks.CustomBlock;
import net.enelson.sopcustomblocks.utils.Utils;
import org.bukkit.Location;
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
        if (!isAvailable()) {
            return;
        }
        plugin.getBlockManager().addBlock(id, location, 0.0F, 0.0F);
    }

    @Override
    public void placeBlock(String id, Location location, float yaw, float pitch) {
        if (!isAvailable()) {
            return;
        }
        plugin.getBlockManager().addBlock(id, location, yaw, pitch);
    }

    @Override
    public void placeBlock(String id, Location location, Player player) {
        if (!isAvailable()) {
            return;
        }
        plugin.getBlockManager().addBlock(id, location, player);
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
        if (!isAvailable()) {
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
}
