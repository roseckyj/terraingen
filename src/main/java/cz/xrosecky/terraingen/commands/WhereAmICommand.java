package cz.xrosecky.terraingen.commands;

import cz.xrosecky.terraingen.data.DataStorage;
import cz.xrosecky.terraingen.utils.Coords;
import cz.xrosecky.terraingen.utils.Pointf2D;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;

public class WhereAmICommand implements CommandExecutor {
    private JavaPlugin plugin;
    private DataStorage storage;

    public WhereAmICommand(JavaPlugin plugin, DataStorage storage) {
        this.plugin = plugin;
        this.storage = storage;
    }

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            Location loc = player.getLocation();
            Pointf2D latlon = Coords.XZToLatLon(loc.getX(), -loc.getZ());

            String lat = String.format(Locale.ROOT, "%.8f", latlon.lat());
            String lon = String.format(Locale.ROOT, "%.8f", latlon.lon());

            player.sendMessage("You are at N" + lat + " E" + lon);
            player.sendMessage("https://mapy.cz/zakladni?source=coor&id=" + lon + "%2C" + lat);

            return true;
        }

        plugin.getLogger().info("Invalid command sender");
        return false;
    }
}