package me.raindance.chunk.events;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BlockScanEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private World world;
    private String gamemode;
    private Chunk chunk;
    private Block block;

    public BlockScanEvent(boolean async, String gamemode, World world, Chunk chunk, Block block) {
        super(async);
        this.world = world;
        this.gamemode = gamemode;
        this.chunk = chunk;
        this.block = block;
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

    public Chunk getChunk() {
        return chunk;
    }

    public Block getBlock() {
        return block;
    }

    public String getGamemode() {
        return gamemode;
    }
}
