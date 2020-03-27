package me.raindance.chunk;

import me.raindance.chunk.commands.CacheDeleteCommand;
import me.raindance.chunk.commands.ScanCommand;
import me.raindance.chunk.listeners.CustomBlockListener;
import me.raindance.chunk.listeners.FinishScanListener;
import me.raindance.chunk.scanners.ConquestScanner;
import me.raindance.chunk.scanners.RegularScanner;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Short class to figure out where the fuck my warps went lol
 * might be reintegrated to engine
 */
public class ShortChunkScanner extends JavaPlugin implements Listener {
    private static ShortChunkScanner plugin;
    @Override
    public void onEnable() {
        plugin = this;
        Bukkit.getPluginManager().registerEvents(new RegularScanner(), this);
        Bukkit.getPluginManager().registerEvents(new ConquestScanner(), this);
        Bukkit.getPluginManager().registerEvents(new FinishScanListener(), this);
        Bukkit.getPluginManager().registerEvents(new CustomBlockListener(), this);
        getCommand("scan").setExecutor(new ScanCommand());
        getCommand("deletecache").setExecutor(new CacheDeleteCommand());
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onDisable() {

    }
    public static ShortChunkScanner getInstance() {
        return plugin;
    }
}
