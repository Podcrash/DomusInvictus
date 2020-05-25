package me.raindance.chunk.listeners;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.pojos.map.*;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.MapTable;
import me.raindance.chunk.ShortChunkScanner;
import me.raindance.chunk.WorldScanner;
import me.raindance.chunk.events.ScanFinishEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;

public class FinishScanListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void finish(ScanFinishEvent e) {
        BaseMap map = WorldScanner.get(e.getWorld().getName());
        if(map.getName() == null || map.getName().equalsIgnoreCase("null")) return;
        map.setGamemode(e.getGamemode());
        e.getWorld().getPlayers().forEach(player -> {
            player.sendMessage("Scanning of world " + e.getWorld().getName() + " is finished!\n"
                               +  "Map " + map.getName() + " is yielded!");
        });

        ShortChunkScanner.getInstance().getLogger().info(map.toString());
        MapTable table = TableOrganizer.getTable(DataTableType.MAPS);
        switch (e.getGamemode().toLowerCase()) {
            case "conquest":
                table.saveMetadataAsync((ConquestMap) map, ConquestMap.class);
                break;
            case "regular":
                table.saveMetadataAsync(map, BaseMap.class);
                break;
            case "islands":
                if (!checkIslands((IslandsMap) map)) {
                    e.getWorld().getPlayers().forEach(player -> {
                        player.sendMessage("Not all bridges were defined correctly! Dump:");
                        player.sendMessage(((IslandsMap) map).getBridges().toString());
                    });
                    return;
                }
                table.saveMetadataAsync((IslandsMap) map, IslandsMap.class);
                break;
            default:
                throw new IllegalStateException("uhhh");
        }
    }

    private boolean checkIslands(IslandsMap map) {
        for (Point2Point point : map.getBridges()) {
            if (point.getPoint1() != null && point.getPoint2() != null)
                continue;
            return false;
        }
        return true;
    }
}
