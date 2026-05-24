package net.enelson.sopcustomblocks.listeners;

import net.enelson.sopcustomblocks.SopCustomBlocks;
import net.enelson.sopcustomblocks.api.event.SopCustomBlockInteractEvent;
import net.enelson.sopcustomblocks.managers.blocks.CustomBlock;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockInteractHandler implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.isCancelled() || e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getClickedBlock() == null) {
            return;
        }
        CustomBlock block = SopCustomBlocks.getInstance().getBlockManager().getBlock(e.getClickedBlock().getLocation());
        if (block == null) {
            return;
        }
        SopCustomBlockInteractEvent interactEvent = new SopCustomBlockInteractEvent(block, e.getPlayer(), e.getClickedBlock(), null);
        Bukkit.getPluginManager().callEvent(interactEvent);
        if (interactEvent.isCancelled()) {
            e.setCancelled(true);
        }
    }
}
