package me.raindance.chunk.scanners;

import com.podcrash.api.db.pojos.map.*;
import org.bukkit.*;
import org.bukkit.block.Block;

public class RegularScanner extends BaseWorldScanner {
    public RegularScanner() {
        super("regular");
    }
    @Override
    public void scan(Location location, Block block, Chunk chunk, World world, String mode) {
        BaseMap map = get(world.getName());

        processMapName(world, block, map);
        processAuthor(world, block, map);
        processSpectatorSpawn(world, block, map);

        processExtraneous(world, block, map);
    }

    public BaseMap setUp(String worldName) {
        BaseMap map = new BaseMap();

        put(worldName, map);
        return map;
    }
}
