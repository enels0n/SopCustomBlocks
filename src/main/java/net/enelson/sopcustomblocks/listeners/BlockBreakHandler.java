package net.enelson.sopcustomblocks.listeners;

import net.enelson.sopcustomblocks.SopCustomBlocks;
import net.enelson.sopcustomblocks.api.event.SopCustomBlockBreakCause;
import net.enelson.sopcustomblocks.api.event.SopCustomBlockBreakEvent;
import net.enelson.sopcustomblocks.managers.blocks.CustomBlock;
import net.enelson.sopcustomblocks.managers.config.ConfigType;
import net.enelson.sopcustomblocks.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakHandler
implements Listener {
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (e.isCancelled()) {
            return;
        }
        CustomBlock block = SopCustomBlocks.getInstance().getBlockManager().getBlock(e.getBlock().getLocation());
        if (block == null) {
            return;
        }
        e.setCancelled(true);
        if (SopCustomBlocks.getInstance().getConfigManager().getBoolean(ConfigType.BLOCKS, block.getId() + ".break-only-admin") && !Utils.hasAdminPermission(e.getPlayer())) {
            return;
        }
        SopCustomBlockBreakEvent breakEvent = new SopCustomBlockBreakEvent(block, e.getPlayer(), SopCustomBlockBreakCause.PLAYER_BREAK);
        Bukkit.getPluginManager().callEvent(breakEvent);
        if (breakEvent.isCancelled()) {
            return;
        }
        SopCustomBlocks.getInstance().getBlockManager().breakBlock(block, e.getPlayer());
    }
}

