package net.enelson.sopcustomblocks.listeners;

import net.enelson.sopcustomblocks.SopCustomBlocks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

public class DragonEggAndWaterHandler
implements Listener {
    @EventHandler
    public void onBlockFromTo(BlockFromToEvent e) {
        if (SopCustomBlocks.getInstance().getBlockManager().getBlock(e.getBlock().getLocation()) != null || SopCustomBlocks.getInstance().getBlockManager().getBlock(e.getToBlock().getLocation()) != null) {
            e.setCancelled(true);
        }
    }
}


