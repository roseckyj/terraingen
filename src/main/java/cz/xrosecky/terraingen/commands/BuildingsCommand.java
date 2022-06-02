package cz.xrosecky.terraingen.commands;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import cz.xrosecky.terraingen.data.DataChunk;
import cz.xrosecky.terraingen.data.DataStorage;
import cz.xrosecky.terraingen.settings.Configuration;
import cz.xrosecky.terraingen.utils.Coords;
import cz.xrosecky.terraingen.utils.Pointf2D;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.stringtemplate.v4.ST;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BuildingsCommand implements CommandExecutor {
    private JavaPlugin plugin;
    private DataStorage storage;
    private Configuration config;

    public BuildingsCommand(JavaPlugin plugin, DataStorage storage, Configuration config) {
        this.plugin = plugin;
        this.storage = storage;
        this.config = config;
    }

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        World world = config.getWorld(plugin);

        File buildingsFile = new File(plugin.getDataFolder(), "buildings.csv");

        List<String> buildings = new ArrayList<String>();

        try {
            Scanner scanner = new Scanner(buildingsFile);
            while (scanner.hasNextLine()) {
                buildings.add(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (String row : buildings) {
            String[] parts = row.split(";");
            if (parts.length != 4) {
                plugin.getLogger().info("Invalid number of fields in row in buildings.csv ('" + row + "')");
                continue;
            }
            String path = parts[0];

            int X = 0;
            int Y = 0;
            int Z = 0;

            try {
                X = Integer.parseInt(parts[1]);
                Y = Integer.parseInt(parts[2]);
                Z = Integer.parseInt(parts[3]);
            } catch (NumberFormatException e) {
                plugin.getLogger().info("Invalid number in one of the fields in row in buildings.csv ('" + row + "')");
                continue;
            }

            File schematicFile = new File(plugin.getDataFolder(), path);

            Clipboard clipboard = null;

            ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
            try (ClipboardReader reader = format.getReader(new FileInputStream(schematicFile))) {
                clipboard = reader.read();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(X, Y, Z))
                        .maskSource()
                        .build();
                Operations.complete(operation);
            } catch (WorldEditException e) {
                e.printStackTrace();
            }

        }

        return true;
    }
}