package net.enelson.sopcustomblocks.api.event;

import net.enelson.sopcustomblocks.managers.blocks.CustomBlock;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SopCustomBlockInteractEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final CustomBlock customBlock;
    private final Player player;
    private final Block clickedBlock;
    private final Entity clickedEntity;
    private boolean cancelled;

    public SopCustomBlockInteractEvent(CustomBlock customBlock, Player player, Block clickedBlock, Entity clickedEntity) {
        this.customBlock = customBlock;
        this.player = player;
        this.clickedBlock = clickedBlock;
        this.clickedEntity = clickedEntity;
    }

    public CustomBlock getCustomBlock() {
        return customBlock;
    }

    public Player getPlayer() {
        return player;
    }

    public Block getClickedBlock() {
        return clickedBlock;
    }

    public Entity getClickedEntity() {
        return clickedEntity;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
