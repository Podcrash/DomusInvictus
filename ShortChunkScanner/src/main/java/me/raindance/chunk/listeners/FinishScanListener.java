package me.raindance.chunk.listeners;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.pojos.map.BaseMap;
import com.podcrash.api.db.pojos.map.ConquestMap;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.MapTable;
import me.raindance.chunk.WorldScanner;
import me.raindance.chunk.events.ScanFinishEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FinishScanListener implements Listener {
    @EventHandler
    public void finish(ScanFinishEvent e) {
        BaseMap map = WorldScanner.get(e.getWorld().getName());
        e.getWorld().getPlayers().forEach(player -> {
            player.sendMessage("map: " + map.toString());
        });
        MapTable table = TableOrganizer.getTable(DataTableType.MAPS);
        switch (e.getGamemode().toLowerCase()) {
            case "conquest":
                table.saveMetadataAsync((ConquestMap) map, ConquestMap.class);
                break;
            default:
                throw new IllegalStateException("uhhh");
        }
    }
}
