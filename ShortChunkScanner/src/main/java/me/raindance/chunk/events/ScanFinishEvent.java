package me.raindance.chunk.events;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ScanFinishEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private World world;
    private String gamemode;


    public ScanFinishEvent(String gamemode, World world) {
        this.world = world;
        this.gamemode = gamemode;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public World getWorld() {
        return world;
    }

    public String getGamemode() {
        return gamemode;
    }
}
