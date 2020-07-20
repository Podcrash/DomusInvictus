package me.raindance.chunk.scanners;

import com.podcrash.api.db.pojos.PojoHelper;
import com.podcrash.api.db.pojos.map.*;
import com.podcrash.api.world.BlockUtil;
import me.raindance.chunk.WorldScanner;
import me.raindance.chunk.annotations.Scannable;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.util.Vector;
import sun.reflect.generics.tree.Tree;

import java.util.*;

public class IslandsScanner extends BaseWorldScanner<IslandsMap> {
    //this is currently a limitation:
    private final Map<Integer, IDPoint2Point> tempBridgeMap = new TreeMap<>();

    public IslandsScanner() {
        super("islands");
    }

    @Override
    public IslandsMap setUp(String worldName) {
        IslandsMap map = new IslandsMap();
        synchronized (tempBridgeMap) {
            tempBridgeMap.clear();
        }
        put(worldName, map);
        return map;
    }

    @Override
    public void scan(Location location, Block block, Chunk chunk, World world, String mode) {
        IslandsMap map = get(world.getName());

        processMapName(world, block, map);
        processSpawn(world, block, map);
        processAuthor(world, block, map);
        processSpectatorSpawn(world, block, map);

        processMiddle(world, block, map);
        processOres(world, block, map);
        processChests(world, block, map);
        processBridges(world, block, map);
        processBridgeType(world, block, map);
    }


    @Scannable
    private void processOres(World world, Block block, IslandsMap map) {
        Material oreType = block.getType();
        Point point = PojoHelper.convertVector2Point(block.getLocation().toVector());
        switch (oreType) {
            case DIAMOND_ORE:
                map.getBlueOres().add(point);
                break;
            case REDSTONE_ORE:
                map.getRedOres().add(point);
                break;
            case EMERALD_ORE:
                map.getGreenOres().add(point);
                break;
            case GOLD_ORE:
                map.getYellowOres().add(point);
                break;
        }

    }

    @Scannable
    private void processOtherResources(World world, Block block, IslandsMap map) {

    }
    @Scannable
    private void processChests(World world, Block block, IslandsMap map) {
        if (block.getType() != Material.CHEST)
            return;

        Point point = PojoHelper.convertVector2Point(block.getLocation().toVector());
        map.getChests().add(point);
        worldBroadcast(world, ChatColor.GOLD + "Found a CHEST at " + point);
    }

    @Scannable
    private void processMiddle(World world, Block block, IslandsMap map) {
        if(block.getType() != Material.FURNACE)
            return;
        Block plate = block.getRelative(BlockFace.UP);
        if(plate.getType() != Material.STONE_PLATE)
            return;

        Point point = PojoHelper.convertVector2Point(block.getLocation().toVector().add(new Vector(0.5, 0, 0.5)));
        worldBroadcast(world, ChatColor.GRAY + "Scanner> Loaded a middle block (for bridges) at " + point);
        map.setMiddle(point);

        WorldScanner.addToDeleteCache(block.getRelative(BlockFace.UP), block);

    }
    @Scannable
    private void processBridges(World world, Block block, IslandsMap map) {
        if (!BlockUtil.isSign(block))
            return;
        Sign sign = (Sign) block.getState();
        String firstLine = sign.getLine(0);
        if (!firstLine.equals("BRIDGE"))
            return;

        Point point = PojoHelper.convertVector2Point(block.getRelative(((org.bukkit.material.Sign) sign.getData()).getAttachedFace()).getLocation().toVector());
        int bridgeID;
        try {
            String secondLine = sign.getLine(1);
            bridgeID = Integer.parseInt(secondLine);
        }catch (NumberFormatException e) {
            worldBroadcast(world, ChatColor.DARK_GREEN + "Sign at " + point + " has an invalid ID!");
            return;
        }

        synchronized (tempBridgeMap) {
            IDPoint2Point questionPoint = tempBridgeMap.get(bridgeID);
            if (questionPoint == null) {
                questionPoint = new IDPoint2Point();
                questionPoint.setId(bridgeID);
                tempBridgeMap.put(bridgeID, questionPoint);
            }

            if (questionPoint.getPoint1() != null && questionPoint.getPoint2() != null) {
                worldBroadcast(world, ChatColor.DARK_GREEN + "The bridge ID " + bridgeID + " does not correspond to ONLY two specific signs.");
                return;
            }
            if (questionPoint.getPoint1() == null) {
                questionPoint.setPoint1(point);
            } else if (questionPoint.getPoint2() == null) {
                questionPoint.setPoint2(point);
            }
            List<IDPoint2Point> ya = new ArrayList<>(tempBridgeMap.values());


            map.setBridges(ya);
        }
        WorldScanner.addToDeleteCache(block);
    }

    @Scannable
    private void processBridgeType(World world, Block block, IslandsMap map) {
        if(!BlockUtil.isSign(block)) return;
        Sign signState = (Sign) block.getState();
        if(!signState.getLine(0).contains("BRIDGETYPE")) return;
        String type = signState.getLine(1);
        map.setBridgeType(type);
        worldBroadcast(world, "Found the bridge type of the map: " + type);
    }
}
