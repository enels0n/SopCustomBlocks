package net.enelson.sopcustomblocks.listeners;

import net.enelson.sopcustomblocks.SopCustomBlocks;
import net.enelson.sopcustomblocks.api.event.SopCustomBlockInteractEvent;
import net.enelson.sopcustomblocks.managers.blocks.CustomBlock;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class ArmorStandHandler
implements Listener {
    @EventHandler
    public void onClick(PlayerInteractAtEntityEvent e) {
        if (!(e.getRightClicked() instanceof ArmorStand)) {
            return;
        }
        if (SopCustomBlocks.getInstance().getBlockManager().isCustomBlockArmorStand(e.getRightClicked())) {
            CustomBlock block = SopCustomBlocks.getInstance().getBlockManager().getBlock(e.getRightClicked());
            SopCustomBlockInteractEvent interactEvent = new SopCustomBlockInteractEvent(block, e.getPlayer(), block != null ? block.getLocation().getBlock() : null, e.getRightClicked());
            Bukkit.getPluginManager().callEvent(interactEvent);
            e.setCancelled(true);
        }
    }
}


