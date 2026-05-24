package net.enelson.sopcustomblocks.api.event;

import net.enelson.sopcustomblocks.managers.blocks.CustomBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SopCustomBlockBreakEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final CustomBlock customBlock;
    private final Player player;
    private final SopCustomBlockBreakCause cause;
    private boolean cancelled;

    public SopCustomBlockBreakEvent(CustomBlock customBlock, Player player, SopCustomBlockBreakCause cause) {
        this.customBlock = customBlock;
        this.player = player;
        this.cause = cause;
    }

    public CustomBlock getCustomBlock() {
        return customBlock;
    }

    public Player getPlayer() {
        return player;
    }

    public SopCustomBlockBreakCause getCause() {
        return cause;
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
