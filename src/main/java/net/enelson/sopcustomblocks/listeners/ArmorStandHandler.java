package net.enelson.sopcustomblocks.listeners;

import net.enelson.sopcustomblocks.SopCustomBlocks;
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
            e.setCancelled(true);
        }
    }
}


