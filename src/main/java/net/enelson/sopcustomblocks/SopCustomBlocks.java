package net.enelson.sopcustomblocks;

import net.enelson.sopcustomblocks.commands.CommandManager;
import net.enelson.sopcustomblocks.listeners.ArmorStandHandler;
import net.enelson.sopcustomblocks.listeners.BlockBreakHandler;
import net.enelson.sopcustomblocks.listeners.BlockDamageHandler;
import net.enelson.sopcustomblocks.listeners.BlockPlaceHandler;
import net.enelson.sopcustomblocks.listeners.ChunkLoadHandler;
import net.enelson.sopcustomblocks.listeners.DragonEggAndWaterHandler;
import net.enelson.sopcustomblocks.listeners.ExplodeHandler;
import net.enelson.sopcustomblocks.listeners.PistonHandler;
import net.enelson.sopcustomblocks.managers.blocks.BlockManager;
import net.enelson.sopcustomblocks.managers.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SopCustomBlocks
extends JavaPlugin {
    private static SopCustomBlocks plugin;
    private BlockManager blockManager;
    private ConfigManager configManager;

    public void onEnable() {
        plugin = this;
        this.configManager = new ConfigManager(this);
        this.reloadConfig();
        this.blockManager = new BlockManager(this);
        PluginManager pluginManager = Bukkit.getPluginManager();
        this.registerListener(pluginManager, new ArmorStandHandler());
        this.registerListener(pluginManager, new BlockBreakHandler());
        this.registerListener(pluginManager, new BlockDamageHandler());
        this.registerListener(pluginManager, new BlockPlaceHandler());
        this.registerListener(pluginManager, new ChunkLoadHandler());
        this.registerListener(pluginManager, new DragonEggAndWaterHandler());
        this.registerListener(pluginManager, new ExplodeHandler());
        this.registerListener(pluginManager, new PistonHandler());
        this.getCommand("sopcustomblocks").setExecutor((CommandExecutor)new CommandManager());
        this.getCommand("sopcustomblocks").setTabCompleter((TabCompleter)new CommandManager());
    }

    public static SopCustomBlocks getInstance() {
        return plugin;
    }

    public BlockManager getBlockManager() {
        return this.blockManager;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public void reloadConfig() {
        this.configManager.reloadConfig();
    }

    public void reloadPlugin() {
        if (this.blockManager != null) {
            this.blockManager.deInit(false);
        }
        this.configManager.reloadConfig();
        this.blockManager = new BlockManager(this);
        for (World world : Bukkit.getWorlds()) {
            this.blockManager.reconcileWorld(world);
        }
    }

    public void onDisable() {
        if (this.blockManager != null) {
            this.blockManager.deInit(false);
        }
    }

    private void registerListener(PluginManager pluginManager, Listener listener) {
        pluginManager.registerEvents(listener, (Plugin)this);
    }
}


