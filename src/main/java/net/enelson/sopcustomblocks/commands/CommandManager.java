package net.enelson.sopcustomblocks.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.enelson.sopcustomblocks.SopCustomBlocks;
import net.enelson.sopcustomblocks.commands.subcommands.AboutCommand;
import net.enelson.sopcustomblocks.commands.subcommands.GiveCommand;
import net.enelson.sopcustomblocks.commands.subcommands.ReloadCommand;
import net.enelson.sopcustomblocks.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class CommandManager
implements CommandExecutor,
TabCompleter {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
        if (args[0].equalsIgnoreCase("about")) {
            new AboutCommand(sender);
        } else if (args[0].equalsIgnoreCase("reload")) {
            new ReloadCommand(sender);
        } else if (args[0].equalsIgnoreCase("give")) {
            new GiveCommand(sender, this.removeElement(args, 0));
        } else {
            return false;
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> completions = new ArrayList<String>();
        if (args.length == 1 && args[0].equalsIgnoreCase("about")) {
            completions.add("about");
            return completions;
        }
        if (Utils.hasAdminPermission(sender)) {
            if (args.length == 1) {
                if (!args[0].equals("") && "give".startsWith(args[0])) {
                    completions.add("give");
                } else if (!args[0].equals("") && "reload".startsWith(args[0])) {
                    completions.add("reload");
                } else {
                    completions.addAll(Arrays.asList("reload", "give"));
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("give")) {
                    return null;
                }
            } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
                completions.addAll(SopCustomBlocks.getInstance().getConfigManager().getBlocksID());
            } else if (args.length == 4 && args[0].equalsIgnoreCase("give")) {
                completions.addAll(Arrays.asList("1", "16", "32", "64"));
            }
        }
        return completions.stream().distinct().collect(Collectors.toList());
    }

    private String[] removeElement(String[] arr, int index) {
        String[] copyArray = new String[arr.length - 1];
        System.arraycopy(arr, 0, copyArray, 0, index);
        System.arraycopy(arr, index + 1, copyArray, index, arr.length - index - 1);
        return copyArray;
    }
}


