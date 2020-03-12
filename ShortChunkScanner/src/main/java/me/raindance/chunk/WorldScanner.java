package me.raindance.chunk;

import com.podcrash.api.db.pojos.map.BaseMap;
import me.raindance.chunk.events.BlockScanEvent;
import me.raindance.chunk.events.ScanFinishEvent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

public final class WorldScanner {
    //world = key
    //metadata = value
    private static final Map<String, BaseMap> dataMap = new HashMap<>();

    public static void scanWorldAsync(String gamemode, World world) {
        Bukkit.getScheduler().runTaskAsynchronously(ShortChunkScanner.getInstance(), () -> WorldScanner.scanWorld(gamemode, world, true));
    }
    public static void scanWorldSync(String gamemode, World world) {
        scanWorld(gamemode, world, false);
    }
    private static void scanWorld(String gamemode, World world, boolean async) {
        Chunk[] loadedChunks = world.getLoadedChunks();
        dataMap.remove(world.getName());
        /*
        if(loadedChunks.length > 200) {
            world.getPlayers().forEach(player -> player.sendMessage("This world tried to be scanned, but there are too many chunks loaded! (temporary)"));
            return;
        }

         */
        ShortChunkScanner.getInstance().getLogger().info("Scanning " + world.getName());
        for(Chunk chunk : loadedChunks) {
            int chunkX = chunk.getX() << 4;
            int chunkZ = chunk.getZ() << 4;
            //scan the blocks
            for(int y = 0; y < world.getMaxHeight(); y++) {
                for (int x = chunkX; x < chunkX + 16; x++) {
                    for (int z = chunkZ; z < chunkZ + 16; z++) {
                        Block block = world.getBlockAt(x, y, z);
                        Bukkit.getPluginManager().callEvent(new BlockScanEvent(async, gamemode, world, chunk, block));
                    }
                }
            }
        }
        Bukkit.getPluginManager().callEvent(new ScanFinishEvent(gamemode, world));
        ShortChunkScanner.getInstance().getLogger().info("Finished scanning " + world.getName());
    }

    public static BaseMap get(String worldName) {
        return dataMap.get(worldName);
    }

    public static void put(String worldName, BaseMap map) {
        dataMap.put(worldName, map);
    }
}
