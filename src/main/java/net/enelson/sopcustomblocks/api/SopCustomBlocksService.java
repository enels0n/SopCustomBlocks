package net.enelson.sopcustomblocks.api;

import net.enelson.sopcustomblocks.managers.blocks.CustomBlock;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface SopCustomBlocksService {

    boolean isAvailable();

    void placeBlock(String id, Location location);

    void placeBlock(String id, Location location, float yaw, float pitch);

    void placeBlock(String id, Location location, Player player);

    boolean removeBlock(Location location);

    boolean isCustomBlock(Location location);

    String getBlockId(Location location);

    CustomBlock getBlock(Location location);

    ItemStack getBlockItem(String id);

    String getItemBlockId(ItemStack item);
}
