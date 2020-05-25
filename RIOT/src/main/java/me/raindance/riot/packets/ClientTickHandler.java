package me.raindance.riot.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.raindance.riot.events.ClientTickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ClientTickTracker extends BasePacketHandler {
    public ClientTickTracker() {
        super(PacketType.Play.Client.FLYING, PacketType.Play.Client.LOOK, PacketType.Play.Client.POSITION, PacketType.Play.Client.POSITION_LOOK);
    }

    @Override
    public void recieve(Player sender, PacketContainer packet) {
        ClientTickEvent tick = new ClientTickEvent(packet, sender);
        Bukkit.getPluginManager().callEvent(tick);
    }

    @Override
    public void send(Player reciever, PacketContainer packet) {

    }
}
