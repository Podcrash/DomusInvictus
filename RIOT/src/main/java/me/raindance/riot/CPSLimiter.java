package me.raindance.riot;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;


public class CPSLimiter extends PacketAdapter {
    private final Map<String, Integer> currentCPS = new HashMap<>();

    private final long maxCPS;
    private final long maxTicks;

    public CPSLimiter(long maxCPS, long maxTicks) {
        super(Riot.getInstance(), ListenerPriority.LOWEST, PacketType.Play.Client.USE_ENTITY);
        this.maxCPS = maxCPS;
        this.maxTicks = maxTicks;

        startTask();
    }

    private void startTask() {
        final long ticksInMilles = maxTicks * 50;
        Bukkit.getServer().getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            long curr = System.currentTimeMillis();
            while (getPlugin().isEnabled()) {
                long now = System.currentTimeMillis();
                long diff = now - curr;
                if (diff < ticksInMilles)
                    continue;
                curr = now;
                currentCPS.clear();
            }
        });
    }

    private int increment(String name) {
        Integer current = currentCPS.get(name);
        if (current == null) current = 0;
        final int finalCPS = current + 1;
        currentCPS.put(name, finalCPS);

        return finalCPS;
    }

    @Override
    public void onPacketSending(PacketEvent packetEvent) {

    }

    @Override
    public void onPacketReceiving(PacketEvent packetEvent) {
        if (packetEvent.getPacketType() != PacketType.Play.Client.USE_ENTITY)
            return;
        Player player = packetEvent.getPlayer();
        String name = player.getName();
        int cps = increment(name);
        if (cps > maxCPS) {
            packetEvent.setCancelled(true);
        }

    }
}
