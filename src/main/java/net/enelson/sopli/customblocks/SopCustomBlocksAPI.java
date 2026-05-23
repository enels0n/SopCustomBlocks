package net.enelson.sopli.customblocks;

import net.enelson.sopcustomblocks.SopCustomBlocks;
import net.enelson.sopcustomblocks.api.SopCustomBlocksService;
import net.enelson.sopcustomblocks.managers.blocks.CustomBlock;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class SopCustomBlocksAPI {

    private SopCustomBlocksAPI() {
    }

    public static boolean isAvailable() {
        SopCustomBlocks plugin = SopCustomBlocks.getInstance();
        return plugin != null && plugin.getApi() != null && plugin.getApi().isAvailable();
    }

    public static SopCustomBlocksService getService() {
        SopCustomBlocks plugin = SopCustomBlocks.getInstance();
        return plugin == null ? null : plugin.getApi();
    }

    public static void placeBlock(String id, Location location) {
        SopCustomBlocksService service = getService();
        if (service != null) {
            service.placeBlock(id, location);
        }
    }

    public static void placeBlock(String id, Location location, float yaw, float pitch) {
        SopCustomBlocksService service = getService();
        if (service != null) {
            service.placeBlock(id, location, yaw, pitch);
        }
    }

    public static void placeBlock(String id, Location location, Player player) {
        SopCustomBlocksService service = getService();
        if (service != null) {
            service.placeBlock(id, location, player);
        }
    }

    public static boolean removeBlock(Location location) {
        SopCustomBlocksService service = getService();
        return service != null && service.removeBlock(location);
    }

    public static boolean isCustomBlock(Location location) {
        SopCustomBlocksService service = getService();
        return service != null && service.isCustomBlock(location);
    }

    public static String getBlockId(Location location) {
        SopCustomBlocksService service = getService();
        return service == null ? null : service.getBlockId(location);
    }

    public static CustomBlock getBlock(Location location) {
        SopCustomBlocksService service = getService();
        return service == null ? null : service.getBlock(location);
    }

    public static ItemStack getBlockItem(String id) {
        SopCustomBlocksService service = getService();
        return service == null ? null : service.getBlockItem(id);
    }

    public static String getItemBlockId(ItemStack item) {
        SopCustomBlocksService service = getService();
        return service == null ? null : service.getItemBlockId(item);
    }
}
