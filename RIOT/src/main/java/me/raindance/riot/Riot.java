package me.raindance.riot;

import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.plugin.java.JavaPlugin;

public class Riot extends JavaPlugin {
    private static Riot INSTANCE;

    public Riot() {
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        ProtocolLibrary.getProtocolManager().addPacketListener(new CPSLimiter(1, 2));
    }

    @Override
    public void onDisable() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(this);
    }

    public static Riot getInstance() {
        return INSTANCE;
    }
}
