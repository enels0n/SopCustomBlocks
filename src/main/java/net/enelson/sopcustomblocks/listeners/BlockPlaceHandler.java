package net.enelson.sopcustomblocks.listeners;

import net.enelson.sopcustomblocks.SopCustomBlocks;
import net.enelson.sopcustomblocks.managers.blocks.CustomBlock;
import net.enelson.sopcustomblocks.managers.config.ConfigType;
import net.enelson.sopcustomblocks.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;

public class BlockPlaceHandler
implements Listener {
    @EventHandler
    public void onPlace(final BlockPlaceEvent e) {
        if (e.isCancelled()) {
            return;
        }
        String id = Utils.getId(e.getItemInHand());
        if (id == null) {
            return;
        }
        e.setCancelled(true);
        final Block block = e.getBlock();
        if (SopCustomBlocks.getInstance().getBlockManager().getBlock(block.getLocation()) != null) {
            return;
        }
        if (id.startsWith("debug")) {
            if (!Utils.hasAdminPermission(e.getPlayer())) {
                return;
            }
            int radius = Integer.parseInt(id.split("-")[1]);
            e.getPlayer().sendMessage("\u0423\u0434\u0430\u043b\u0435\u043d\u043e " + SopCustomBlocks.getInstance().getBlockManager().debug(block.getLocation(), radius) + " \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439 \u0432 \u0440\u0430\u0434\u0438\u0443\u0441\u0435 " + radius);
            return;
        }
        final String newBlock = SopCustomBlocks.getInstance().getConfigManager().getString(ConfigType.BLOCKS, id + ".replacement-block");
        final Material material = newBlock != null ? Material.valueOf((String)newBlock.toUpperCase()) : e.getItemInHand().getType();
        CustomBlock placed = SopCustomBlocks.getInstance().getBlockManager().addBlock(id, block.getLocation(), e.getPlayer());
        if (placed == null) {
            e.getPlayer().sendMessage("Failed to place custom block visual. Check console.");
            return;
        }
        Bukkit.getScheduler().runTaskLater((Plugin)SopCustomBlocks.getInstance(), new Runnable(){
            @Override
            public void run() {
                if (newBlock != null) {
                    block.setType(material);
                } else {
                    block.setType(e.getItemInHand().getType());
                }
            }
        }, 1L);
        if (!e.getPlayer().getGameMode().equals((Object)GameMode.CREATIVE)) {
            e.getItemInHand().setAmount(e.getItemInHand().getAmount() - 1);
        }
    }
}


