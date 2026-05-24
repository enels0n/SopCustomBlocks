package net.enelson.sopcustomblocks.api.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SopCustomBlockPlaceEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final String blockId;
    private final Location location;
    private final Player player;
    private final float yaw;
    private final float pitch;
    private boolean cancelled;

    public SopCustomBlockPlaceEvent(String blockId, Location location, Player player, float yaw, float pitch) {
        this.blockId = blockId;
        this.location = location == null ? null : location.clone();
        this.player = player;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public String getBlockId() {
        return blockId;
    }

    public Location getLocation() {
        return location == null ? null : location.clone();
    }

    public Player getPlayer() {
        return player;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
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
