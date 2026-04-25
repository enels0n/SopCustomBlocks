package net.enelson.sopcustomblocks.listeners;

import net.enelson.sopcustomblocks.SopCustomBlocks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class ChunkLoadHandler
implements Listener {
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (SopCustomBlocks.getInstance() == null || SopCustomBlocks.getInstance().getBlockManager() == null) {
            return;
        }
        SopCustomBlocks.getInstance().getBlockManager().reconcileChunk(event.getChunk());
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if (SopCustomBlocks.getInstance() == null || SopCustomBlocks.getInstance().getBlockManager() == null) {
            return;
        }
        SopCustomBlocks.getInstance().getBlockManager().reconcileWorld(event.getWorld());
    }
}


