package me.raindance.chunk.listeners;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.pojos.map.*;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.MapTable;
import com.podcrash.api.plugin.PodcrashSpigot;
import me.raindance.chunk.WorldScanner;
import me.raindance.chunk.events.ScanFinishEvent;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.*;

public class FinishScanListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void finish(ScanFinishEvent e) {
        BaseMap map = WorldScanner.get(e.getWorld().getName());
        //Bukkit.broadcastMessage(map.toString());
        if(map.getName() == null || map.getName().equalsIgnoreCase("null")) return;
        map.setGamemode(e.getGamemode());
        e.getWorld().getPlayers().forEach(player -> {
            player.sendMessage("Scanning of world " + e.getWorld().getName() + " is finished!\n"
                               +  "Map " + map.getName() + " is yielded!");
        });
        MapTable table = TableOrganizer.getTable(DataTableType.MAPS);
        switch (e.getGamemode().toLowerCase()) {
            case "conquest":
                table.saveMetadataAsync((ConquestMap) map, ConquestMap.class);
                break;
            case "regular":
                table.saveMetadataAsync(map, BaseMap.class);
                break;
            case "islands":
                IslandsMap islandsMap = (IslandsMap) map;
                if (!checkIslands(islandsMap)) {
                    e.getWorld().getPlayers().forEach(player -> {
                        player.sendMessage("Not all bridges were defined correctly! Dump:");
                        player.sendMessage(islandsMap.getBridges().toString());
                    });
                    return;
                }
                uploadBridges(e.getWorld(), islandsMap, islandsMap.getBridges());
                PodcrashSpigot.debugLog(islandsMap.toString());
                table.saveMetadataAsync(islandsMap, IslandsMap.class);
                break;
            default:
                throw new IllegalStateException("uhhh");
        }
    }

    private boolean checkIslands(IslandsMap map) {
        for (IDPoint2Point point : map.getBridges()) {
            if (point.getPoint1() != null && point.getPoint2() != null)
                continue;
            return false;
        }
        return true;
    }

    private void uploadBridges(World world, IslandsMap map, List<IDPoint2Point> point2Points) {
        int size = point2Points.size();
        List<BridgePoint> bridgePoints = new ArrayList<>();
        alert(world, "number of sections: " + size);
        for (int i = 0; i < size; i++) {
            IDPoint2Point point2Point = point2Points.get(i);
            Point point1 = point2Point.getPoint1();
            Point point2 = point2Point.getPoint2();

            int minX = (int) min(point1.getX(), point2.getX());
            int minY = (int) min(point1.getY(), point2.getY());
            int minZ = (int) min(point1.getZ(), point2.getZ());

            int maxX = (int) max(point1.getX(), point2.getX());
            int maxY = (int) max(point1.getY(), point2.getY());
            int maxZ = (int) max(point1.getZ(), point2.getZ());

            BridgePoint bridgePoint = new BridgePoint();
            bridgePoint.setBridgeID(point2Point.getId());
            List<BridgeSection> sections = new ArrayList<>();
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BridgeSection section = new BridgeSection();
                    section.setX(x);
                    section.setZ(z);
                    Map<String, Object> yMap = new HashMap<>();
                    for (int y = minY; y <= maxY; y++) {
                        Block block = world.getBlockAt(x, y, z);
                        if (block.getType() == Material.AIR || block.getType() == Material.SPONGE)
                            continue;
                        int blockID = block.getTypeId() + (block.getData() << 12);
                        yMap.put(String.valueOf(y), String.valueOf(blockID));
                    }
                    section.setBlockMap(yMap);
                    sections.add(section);
                }
            }
            bridgePoint.setSections(sections);
            bridgePoints.add(bridgePoint);
        }

        map.setBridgeData(bridgePoints);
    }


    private void alert(World world, String msg) {
        world.getPlayers().forEach(player -> player.sendMessage(msg));
    }
}
