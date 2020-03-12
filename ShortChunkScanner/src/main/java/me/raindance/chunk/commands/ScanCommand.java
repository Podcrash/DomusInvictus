package me.raindance.chunk.commands;

import me.raindance.chunk.WorldScanner;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ScanCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) return false;
        if(args.length == 0) {
            commandSender.sendMessage("please put 1 argument");
            return true;
        }
        World world = ((Player) commandSender).getWorld();
        String mode = args[0];
        if(args.length == 1) {
            WorldScanner.scanWorldSync(mode, world);
            return true;
        }
        return true;
    }
}
