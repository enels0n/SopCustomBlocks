package net.enelson.sopcustomblocks.listeners;

import java.util.List;
import net.enelson.sopcustomblocks.SopCustomBlocks;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

public class PistonHandler
implements Listener {
    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent e) {
        if (this.checkCustomBlocks(e.getBlocks())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent e) {
        if (this.checkCustomBlocks(e.getBlocks())) {
            e.setCancelled(true);
        }
    }

    private boolean checkCustomBlocks(List<Block> blocks) {
        for (Block block : blocks) {
            if (SopCustomBlocks.getInstance().getBlockManager().getBlock(block.getLocation()) == null) continue;
            return true;
        }
        return false;
    }
}


