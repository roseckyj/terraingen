package cz.xrosecky.terraingen.generator.populators;

import cz.xrosecky.terraingen.data.DataChunk;
import cz.xrosecky.terraingen.data.DataStorage;
import cz.xrosecky.terraingen.data.types.SpatialData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.Bisected;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

public class SpatialStatsPopulator extends BlockPopulator {

    public SpatialStatsPopulator(JavaPlugin javaPlugin, DataStorage storage) {
        super();
        this.javaPlugin = javaPlugin;
        this.storage = storage;
    }

    private final JavaPlugin javaPlugin;
    private final DataStorage storage;

    private final Material[] COLORS = new Material[]{
        Material.RED_STAINED_GLASS,
        Material.ORANGE_STAINED_GLASS,
        Material.YELLOW_STAINED_GLASS,
        Material.LIME_STAINED_GLASS,
        Material.GREEN_STAINED_GLASS,
        Material.BLUE_STAINED_GLASS
    };

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion region) {
        DataChunk dataChunk = storage.getChunk(chunkX, chunkZ);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                ArrayList<SpatialData> workspaces = dataChunk.spatialStats.get("workspaces");
                for (SpatialData d : workspaces) {
                    if (d.isIn(chunkX * 16 + x, chunkZ * 16 + z)) {
                        //for (int y = worldInfo.getMinHeight(); y < worldInfo.getMaxHeight(); y++) {
                            int y = worldInfo.getMaxHeight() - 1;
                            Location l = new Location(region.getWorld(), chunkX * 16 + x, y, chunkZ * 16 + z);
                            //if (region.getType(l).isSolid()) {
                                region.setType(l, COLORS[(int)(d.data.get("workspaces") * 6 / 4096)]);
                            //}
                        //}
                    }
                }
            }
        }
    }
}
