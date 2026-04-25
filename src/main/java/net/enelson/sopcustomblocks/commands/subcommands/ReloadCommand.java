package net.enelson.sopcustomblocks.commands.subcommands;

import net.enelson.sopcustomblocks.SopCustomBlocks;
import net.enelson.sopcustomblocks.utils.Utils;
import org.bukkit.command.CommandSender;

public class ReloadCommand {
    public ReloadCommand(CommandSender sender) {
        if (Utils.hasAdminPermission(sender)) {
            SopCustomBlocks.getInstance().reloadPlugin();
            sender.sendMessage("The plugin has been reloaded.");
            return;
        }
        sender.sendMessage("You do not have permission to use this command.");
    }
}


