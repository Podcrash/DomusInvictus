package me.raindance.chunk.commands;

import me.raindance.chunk.WorldScanner;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CacheDeleteCommand  implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        World world = ((Player) commandSender).getWorld();
        try {
            WorldScanner.deleteCache(world);
        }catch (RuntimeException e) {
            commandSender.sendMessage(e.getMessage());
        }
        world.getPlayers().forEach(player -> player.sendMessage("Cleared all the datapoints in world " + world.getName() + "\n" +
                "Note: this does not include 'DATA' or 'AUTHOR' signs"));
        return true;
    }
}
