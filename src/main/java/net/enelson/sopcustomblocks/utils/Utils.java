package net.enelson.sopcustomblocks.utils;

import java.util.List;
import net.enelson.sopcustomblocks.SopCustomBlocks;
import net.enelson.sopcustomblocks.managers.config.ConfigType;
import net.enelson.sopli.lib.SopLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Utils {
    public static ItemStack generateItem(String id, int amount) {
        String material = SopCustomBlocks.getInstance().getConfigManager().getString(ConfigType.BLOCKS, id + ".material");
        if (material == null) {
            return null;
        }
        int model = SopCustomBlocks.getInstance().getConfigManager().getInt(ConfigType.BLOCKS, id + ".model");
        String name = SopCustomBlocks.getInstance().getConfigManager().getString(ConfigType.BLOCKS, id + ".name");
        List<String> enchantments = SopCustomBlocks.getInstance().getConfigManager().getStringList(ConfigType.BLOCKS, id + ".enchantments");
        List<String> lore = SopCustomBlocks.getInstance().getConfigManager().getStringList(ConfigType.BLOCKS, id + ".lore");
        List<String> nbts = SopCustomBlocks.getInstance().getConfigManager().getStringList(ConfigType.BLOCKS, id + ".nbts");
        nbts.add("ACustomBlocks::" + id);
        nbts.add("SopCustomBlocks::" + id);
        String customItemKey = SopCustomBlocks.getInstance().getConfigManager().getString(ConfigType.BLOCKS, id + ".custom-item-key");
        String customItemKeyFallback = SopCustomBlocks.getInstance().getConfigManager().getString(ConfigType.BLOCKS, id + ".custom-item-key-fallback");
        ItemStack item = SopLib.getInstance().getItemUtils().createItem(material, amount, (Object)model, name, enchantments, lore, nbts);
        if (customItemKey != null) {
            SopLib.getInstance().getItemUtils().setCustomItemKey(item, customItemKey, customItemKeyFallback);
        }
        return item;
    }

    public static ItemStack generateItem(String id) {
        return Utils.generateItem(id, 1);
    }

    public static ItemStack generateModeledItemForItemDisplay(String material, int model) {
        return SopLib.getInstance().getItemUtils().createItem(material, (Object)model, null, null, null, null);
    }

    public static String getId(ItemStack item) {
        String id = (String)SopLib.getInstance().getItemUtils().getNBT(item, "SopCustomBlocks", String.class);
        if (id != null && !id.isEmpty()) {
            return id;
        }
        return (String)SopLib.getInstance().getItemUtils().getNBT(item, "ACustomBlocks", String.class);
    }

    public static Location getDeserializedLocation(String s) {
        String[] split = s.split(",");
        return new Location(Bukkit.getWorld((String)split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
    }

    public static String getSerializedLocation(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    public static boolean canBuild(Player player, Location location) {
        return SopLib.getInstance().getProtectionService().canBuild(player, location);
    }

    public static boolean hasAdminPermission(CommandSender sender) {
        return sender.hasPermission("sopcustomblocks.admin") || sender.hasPermission("acustomblocks.admin");
    }
}


