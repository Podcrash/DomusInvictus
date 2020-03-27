package me.raindance.chunk.scanners;

import com.podcrash.api.db.pojos.PojoHelper;
import com.podcrash.api.db.pojos.map.*;
import com.podcrash.api.mc.world.BlockUtil;
import me.raindance.chunk.WorldScanner;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConquestScanner extends BaseWorldScanner {
    public ConquestScanner() {
        super("conquest");
    }

    @Override
    public void scan(Location location, Block block, Chunk chunk, World world, String mode) {
        ConquestMap map = (ConquestMap) get(world.getName());

        processMapName(world, block, map);
        processSpawn(world, block, map);
        processAuthor(world, block, map);
        processSpectatorSpawn(world, block, map);

        processItemObjectives(world, block, map);
        processWinObjectives(world, block, map);
        processExtraneous(world, block, map);
    }

    public BaseMap setUp(String worldName) {
        ConquestMap map = new ConquestMap();

        put(worldName, map);
        return map;
    }

    private void processItemObjectives(World world, Block block, ConquestMap map) {
        Block plate = block.getRelative(BlockFace.UP);
        if(plate.getType() != Material.STONE_PLATE) return;
        List<Point> points;
        ChatColor color;
        switch (block.getType()) {
            case BEDROCK:
                points = map.getStars();
                color = ChatColor.WHITE;
                break;
            case GOLD_BLOCK:
                points = map.getRestocks();
                color = ChatColor.YELLOW;
                break;
            case REDSTONE_BLOCK:
                points = map.getMines();
                color = ChatColor.RED;
                break;
            case DIAMOND_BLOCK:
                points = map.getDiamonds();
                color = ChatColor.AQUA;
                break;
            default:
                return;
        }
        Point point = PojoHelper.convertVector2Point(block.getLocation().toVector().add(new Vector(0.5, 0, 0.5)));
        points.add(point);
        worldBroadcast(world, ChatColor.GRAY + "Scanner> Found a " + color + block.getType().name() + ChatColor.GRAY + " @" + point);
        put(world.getName(), map);

        WorldScanner.addToDeleteCache(plate);
    }
    private void processWinObjectives(World world, Block block, ConquestMap map) {
        if(block.getType() != Material.BEACON) return;
        Block glass = block.getRelative(BlockFace.UP);
        if(glass.getType() != Material.GLASS && glass.getType() != Material.STAINED_GLASS) return;
        Block sign = glass.getRelative(BlockFace.UP);
        if(sign.getType() != Material.SIGN_POST && sign.getType() != Material.WALL_SIGN) return;
        String name = ((Sign) sign.getState()).getLine(0);
        Point point = PojoHelper.convertVector2Point(block.getLocation().toVector());
        CapturePointPojo capturePointPojo = new CapturePointPojo();
        capturePointPojo.setName(name);
        capturePointPojo.setPoint(point);

        worldBroadcast(world, ChatColor.GRAY + "Scanner> Found a capture point " + ChatColor.GOLD + name + ChatColor.GRAY + " @" + point);
        map.getCapturePointPojos().add(capturePointPojo);

        put(world.getName(), map);
        WorldScanner.addToDeleteCache(sign);
    }
}
