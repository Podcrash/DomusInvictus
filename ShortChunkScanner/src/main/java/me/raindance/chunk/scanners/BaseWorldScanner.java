package me.raindance.chunk.scanners;

import com.podcrash.api.db.pojos.PojoHelper;
import com.podcrash.api.db.pojos.map.*;
import com.podcrash.api.world.BlockUtil;
import me.raindance.chunk.WorldScanner;
import me.raindance.chunk.events.BlockScanEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.material.Wool;
import org.bukkit.util.Vector;

import java.util.*;

public abstract class BaseWorldScanner implements Listener {
    private String mode;
    public BaseWorldScanner(String mode) {
        this.mode = mode;
    }

    @EventHandler
    public void scanner(BlockScanEvent event) {
        String mode = event.getGamemode();
        if(!mode.equalsIgnoreCase(this.mode)) return;
        Block block = event.getBlock();
        Chunk chunk = event.getChunk();
        Location location = event.getBlock().getLocation();
        World world = event.getWorld();

        scan(location, block, chunk, world, mode);
    }

    public abstract BaseMap setUp(String worldName);
    public abstract void scan(Location location, Block block, Chunk chunk, World world, String mode);

    protected BaseMap get(String worldName) {
        BaseMap map = WorldScanner.get(worldName);
        if(map == null) map = setUp(worldName);
        return map;
    }

    protected void put(String worldName, BaseMap map) {
        WorldScanner.put(worldName, map);
    }

    protected void worldBroadcast(World world, String msg) {
        for(Player player : world.getPlayers()) player.sendMessage(msg);
    }

    protected void processMapName(World world, Block block, BaseMap map) {
        if(!BlockUtil.isSign(block)) return;
        Sign signState = (Sign) block.getState();
        if(!signState.getLine(0).contains("DATA")) return;
        String name = signState.getLine(1);
        map.setName(name);
        worldBroadcast(world, "Found the name of the map: " + name);

        put(world.getName(), map);
    }

    protected void processSpawn(World world, Block block, GameMap map) {
        if(block.getType() != Material.WOOL) return;
        Block plate = block.getRelative(BlockFace.UP);
        if(plate.getType() != Material.STONE_PLATE) return;

        Location loc = block.getLocation();
        //we add 0.5 because block coords return whole numbers
        //whole number coords usually make people suffocate into a wall.
        //(0.5, 0, 0.5) will make players stand in the middle of that block.
        Point point = PojoHelper.convertVector2Point(loc.toVector().add(new Vector(0.5, 0, 0.5)));
        Wool woolData = (Wool) block.getState().getData();

        ChatColor color = ChatColor.valueOf(woolData.getColor().name());
        List<Point> points = map.getSpawns().getOrDefault(woolData.getColor().name(), new ArrayList<>());
        points.add(point);

        map.getSpawns().put(woolData.getColor().name(), points);
        worldBroadcast(world, ChatColor.GRAY + "Scanner> Loaded a " + color + woolData.getColor().name() + " spawn at " + point);
        put(world.getName(), map);
        WorldScanner.addToDeleteCache(plate, block);
    }

    protected void processAuthor(World world, Block block, BaseMap map) {
        if(block.getType() != Material.SIGN_POST && block.getType() != Material.WALL_SIGN) return;
        Sign sign = (Sign) block.getState();
        //if it doesn't contain, it's a normal sign
        if(!sign.getLine(0).contains("[Authors]")) return;
        for(int i = 1; i < 4; i++) {
            String line = sign.getLine(i);
            if(line.isEmpty()) continue;
            map.getAuthors().add(line);
            worldBroadcast(world, ChatColor.GRAY + "Scanner> Found contributor: " + ChatColor.DARK_BLUE + line);
        }
        put(world.getName(), map);
    }

    protected void processSpectatorSpawn(World world, Block block, BaseMap map) {
        if(block.getType() != Material.LAPIS_BLOCK) return;
        Block plate = block.getRelative(BlockFace.UP);
        if(plate.getType() != Material.STONE_PLATE) return;
        Point point = PojoHelper.convertVector2Point(block.getLocation().toVector());
        map.setDefaultSpawn(point);
        worldBroadcast(world, ChatColor.GRAY + "Scanner> Found spectator spawn @" + ChatColor.DARK_BLUE + point);
        put(world.getName(), map);

        WorldScanner.addToDeleteCache(plate, block);
    }


    private final Set<Material> validExtra = new HashSet<>(Arrays.asList(Material.SLIME_BLOCK, Material.COMMAND));
    protected void processExtraneous(World world, Block block, BaseMap map) {
        Block signBlock = block.getRelative(BlockFace.UP);
        if(!BlockUtil.isSign(signBlock)) return;
        if(!validExtra.contains(block.getType())) return;
        Sign sign = (Sign) signBlock.getState();

        List<Point2Point> pointMap;
        ChatColor color;

        Point dataPoint = new Point();
        double dataX, dataY, dataZ;
        try {
            dataX = Double.parseDouble(sign.getLine(0));
            dataY = Double.parseDouble(sign.getLine(1));
            dataZ = Double.parseDouble(sign.getLine(2));
        }catch (NumberFormatException e) {
            return;
        }

        Point point = PojoHelper.convertVector2Point(block.getLocation().toVector().add(new Vector(0.5, 0, 0.5)));

        Point2Point point2Point = new Point2Point();
        point2Point.setPoint1(point);
        switch (block.getType()) {
            case SLIME_BLOCK:
                pointMap = map.getLaunchPads();
                color = ChatColor.DARK_GREEN;
                break;
            case COMMAND:
                pointMap = map.getTeleportPads();
                color = ChatColor.LIGHT_PURPLE;
                dataX += 0.5D;
                dataZ += 0.5D;
                Block block1 = world.getBlockAt(new Location(world, dataX, dataY, dataZ));
                if(block1.getType() != Material.COMMAND) {
                    worldBroadcast(world, ChatColor.GRAY + "The corresponding teleport pad at " + point + "does not correlate to the point at: " + dataPoint);
                    return;
                }else if(compare(pointMap, point2Point)) return;
                break;
            default:
                return;
        }

        dataPoint.setX(dataX);
        dataPoint.setY(dataY);
        dataPoint.setZ(dataZ);

        point2Point.setPoint2(dataPoint);

        worldBroadcast(world, ChatColor.GRAY + "Scanner> Found a " + color + block.getType().name() + ChatColor.GRAY + " @" + point + " that points to " + dataPoint);
        pointMap.add(point2Point);

        put(world.getName(), map);
        WorldScanner.addToDeleteCache(signBlock);
    }

    /**
     * This is to avoid storing duplicate data.
     * @param list
     * @param data
     * @return
     */
    private boolean compare(List<Point2Point> list, Point2Point data) {
        for(Point2Point po : list) {
            if(po.getPoint1().equals(data.getPoint2()) && po.getPoint2().equals(data.getPoint1()))
                return true;
        }
        return false;
    }
}
