package net.enelson.sopcustomblocks.commands.subcommands;

import net.enelson.sopcustomblocks.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveCommand {
    public GiveCommand(CommandSender sender, String[] args) {
        ItemStack item;
        if (!Utils.hasAdminPermission(sender)) {
            sender.sendMessage("You do not have permission to use this command.");
            return;
        }
        if (args.length != 2 && args.length != 3) {
            sender.sendMessage("Usage: /sopcustomblocks give <player> <blockId> [amount]");
            return;
        }
        Player player = Bukkit.getPlayerExact((String)args[0]);
        if (player == null) {
            sender.sendMessage("Player not found.");
            return;
        }
        int amount = 0;
        if (args.length == 3) {
            try {
                amount = Integer.parseInt(args[2]);
            }
            catch (NumberFormatException ex) {
                sender.sendMessage("Amount must be a number.");
                return;
            }
        }
        if ((item = Utils.generateItem(args[1], amount = amount > 0 ? amount : 1)) == null) {
            sender.sendMessage("Block ID not found or configured incorrectly.");
            return;
        }
        if (player.getInventory().addItem(new ItemStack[]{item}).size() != 0) {
            player.getWorld().dropItem(player.getLocation(), item);
        }
        sender.sendMessage("Given " + amount + " item(s) of " + args[1] + " to " + player.getName() + ".");
    }
}


