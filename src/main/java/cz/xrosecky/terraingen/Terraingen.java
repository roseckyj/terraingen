package cz.xrosecky.terraingen;

import cz.xrosecky.terraingen.commands.GotoCommand;
import cz.xrosecky.terraingen.commands.WhereAmICommand;
import cz.xrosecky.terraingen.data.DataStorage;
import cz.xrosecky.terraingen.data.loader.LightLoader;
import cz.xrosecky.terraingen.data.loader.StreetLoader;
import cz.xrosecky.terraingen.data.loader.TerrainLoader;
import cz.xrosecky.terraingen.data.loader.TreeLoader;
import cz.xrosecky.terraingen.generator.ChunkGen;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;


public final class Terraingen extends JavaPlugin {
    private final DataStorage storage = new DataStorage();

    @Override
    public ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, String id) {
        return new ChunkGen(this, storage);
    }

    @Override
    public void onEnable() {
        this.getCommand("goto").setExecutor(new GotoCommand(this, storage));
        this.getCommand("whereami").setExecutor(new WhereAmICommand(this, storage));

        this.getLogger().info("Source loading started");

        this.getLogger().info("> Loading terrain...");
        TerrainLoader terrainLoader = new TerrainLoader(storage, this);
        int loadedTerrainPoints = terrainLoader.load("plugins/terraingen/terrain");
        this.getLogger().info("> Loaded " + loadedTerrainPoints + " terrain points");

        this.getLogger().info("> Loading trees...");
        TreeLoader treeLoader = new TreeLoader(storage, this);
        int loadedTrees = treeLoader.load("plugins/terraingen/tree.geojson");
        this.getLogger().info("> Loaded " + loadedTrees + " trees");

        this.getLogger().info("> Loading streets...");
        StreetLoader streetLoader = new StreetLoader(storage, this);
        int loadedStreets = streetLoader.load("plugins/terraingen/street.geojson");
        this.getLogger().info("> Loaded " + loadedStreets + " streets");

        this.getLogger().info("> Loading lights...");
        LightLoader lightLoader = new LightLoader(storage, this);
        int loadedLights = lightLoader.load("plugins/terraingen/light.geojson");
        this.getLogger().info("> Loaded " + loadedLights + " lights");

        this.getLogger().info("Source loading completed");
    }

    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);
    }
}
