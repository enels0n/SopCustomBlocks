package net.enelson.sopcustomblocks.commands.subcommands;

import org.bukkit.command.CommandSender;

public class AboutCommand {
    public AboutCommand(CommandSender sender) {
        sender.sendMessage("SopCustomBlocks is running.");
    }
}


