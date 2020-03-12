package me.raindance.chunk.commands;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.MapTable;
import me.raindance.chunk.WorldScanner;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UploadCommand implements CommandExecutor {@Override
public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
    if (!(commandSender instanceof Player)) return false;
    if(args.length != 0) {
        commandSender.sendMessage("please put no arguments");
        return true;
    }
    World world = ((Player) commandSender).getWorld();
    MapTable table = TableOrganizer.getTable(DataTableType.MAPS);
    table.uploadWorld(world.getName());
    return true;
}
}
