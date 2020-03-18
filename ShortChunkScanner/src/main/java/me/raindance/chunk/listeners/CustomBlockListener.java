package me.raindance.chunk.listeners;

import me.raindance.chunk.ShortChunkScanner;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class CustomBlockListener implements Listener {
    private static final Map<String, Long> cooldown = new HashMap<>();
    @EventHandler
    public void slimeBlock(PlayerMoveEvent event) {
        long cd = cooldown.getOrDefault(event.getPlayer().getName(), -1L);
        if(cd >= System.currentTimeMillis()) return;
        Location towards = event.getTo().clone();
        if(!(towards.getBlock().getState() instanceof Sign)) return;
        Sign sign = (Sign) towards.getBlock().getState();
        Location possSlime = towards.subtract(0, 1, 0);
        if(possSlime.getBlock().getType() != Material.SLIME_BLOCK) return;
        double x = Double.parseDouble(sign.getLine(0));
        double y = Double.parseDouble(sign.getLine(1));
        double z = Double.parseDouble(sign.getLine(2));

        Vector vector = new Vector(x, y, z);
        cooldown.put(event.getPlayer().getName(), System.currentTimeMillis() + 1500L);
        event.getPlayer().setVelocity(vector);
    }
}
