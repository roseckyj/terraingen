package cz.xrosecky.terraingen.commands;

import cz.xrosecky.terraingen.data.DataChunk;
import cz.xrosecky.terraingen.data.DataStorage;
import cz.xrosecky.terraingen.utils.Coords;
import cz.xrosecky.terraingen.utils.Pointf2D;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GotoCommand implements CommandExecutor {
    private JavaPlugin plugin;
    private DataStorage storage;

    public GotoCommand(JavaPlugin plugin, DataStorage storage) {
        this.plugin = plugin;
        this.storage = storage;
    }

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && args.length == 2) {
            Player player = (Player) sender;

            double lat = Double.parseDouble(args[0]);
            double lon = Double.parseDouble(args[1]);
            Pointf2D loc = Coords.latLonToXZ(lat, lon);

            DataChunk chunk = storage.getChunk((int)Math.floor(loc.x / 16.0), (int)Math.floor(loc.z / 16.0));
            int alt = chunk.getAlt((int)Math.round(loc.x), (int)Math.round(loc.z));

            player.teleport(new Location(player.getWorld(), loc.x, alt + 2, loc.z));

            return true;
        }

        plugin.getLogger().info("Invalid command args or sender (" + args.length + " args)");
        return false;
    }
}