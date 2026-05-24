package net.enelson.sopcustomblocks.listeners;

import net.enelson.sopcustomblocks.SopCustomBlocks;
import net.enelson.sopcustomblocks.api.event.SopCustomBlockBreakCause;
import net.enelson.sopcustomblocks.api.event.SopCustomBlockBreakEvent;
import net.enelson.sopcustomblocks.managers.blocks.CustomBlock;
import net.enelson.sopcustomblocks.managers.config.ConfigType;
import net.enelson.sopcustomblocks.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;

public class BlockDamageHandler
implements Listener {
    @EventHandler(priority=EventPriority.LOWEST)
    public void onHit(BlockDamageEvent e) {
        Block block = e.getBlock();
        CustomBlock customBlock = SopCustomBlocks.getInstance().getBlockManager().getBlock(block.getLocation());
        if (customBlock == null) {
            return;
        }
        if (!SopCustomBlocks.getInstance().getConfigManager().getBoolean(ConfigType.BLOCKS, customBlock.getId() + ".break-by-hit")) {
            return;
        }
        if (SopCustomBlocks.getInstance().getConfigManager().getBoolean(ConfigType.BLOCKS, customBlock.getId() + ".break-only-admin") && !Utils.hasAdminPermission(e.getPlayer())) {
            return;
        }
        if (e.isCancelled() || !Utils.canBuild(e.getPlayer(), block.getLocation())) {
            return;
        }
        SopCustomBlockBreakEvent breakEvent = new SopCustomBlockBreakEvent(customBlock, e.getPlayer(), SopCustomBlockBreakCause.PLAYER_DAMAGE);
        Bukkit.getPluginManager().callEvent(breakEvent);
        if (breakEvent.isCancelled()) {
            return;
        }
        SopCustomBlocks.getInstance().getBlockManager().breakBlock(customBlock, e.getPlayer());
    }
}


