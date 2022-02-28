package cz.xrosecky.terraingen.generator.populators;

import cz.xrosecky.terraingen.data.DataChunk;
import cz.xrosecky.terraingen.data.DataStorage;
import cz.xrosecky.terraingen.data.types.Point;
import cz.xrosecky.terraingen.data.types.Tree;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Lantern;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class LightPopulator extends BlockPopulator {
    public LightPopulator(JavaPlugin javaPlugin, DataStorage storage) {
        super();
        this.javaPlugin = javaPlugin;
        this.storage = storage;
    }

    private final JavaPlugin javaPlugin;
    private final DataStorage storage;

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion region) {
        DataChunk dataChunk = storage.getChunk(chunkX, chunkZ);
        for (Point light : dataChunk.lights) {
            long x = light.x;
            long z = light.z;

            int y = dataChunk.getAlt((int)(x), (int)(z)) + 1;

            Location loc = new Location(region.getWorld(), x, y, z);

            if (!region.isInRegion(loc)) {
                continue;
            }

            // Post
            region.setType(new Location(region.getWorld(), x + 0, y + 0, z + 0), Material.CHISELED_STONE_BRICKS);
            region.setType(new Location(region.getWorld(), x + 0, y + 1, z + 0), Material.STONE_BRICK_WALL);
            region.setType(new Location(region.getWorld(), x + 0, y + 2, z + 0), Material.OAK_FENCE);
            region.setType(new Location(region.getWorld(), x + 0, y + 3, z + 0), Material.OAK_FENCE);
            region.setType(new Location(region.getWorld(), x + 0, y + 4, z + 0), Material.OAK_FENCE);
            region.setType(new Location(region.getWorld(), x + 0, y + 5, z + 0), Material.STONE_BRICK_WALL);
            region.setType(new Location(region.getWorld(), x + 0, y + 6, z + 0), Material.OAK_PLANKS);

            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (Math.abs(dx) + Math.abs(dz) == 1) {
                        region.setType(new Location(region.getWorld(), x + dx, y + 6, z + dz), Material.OAK_SLAB);
                        region.setType(new Location(region.getWorld(), x + dx, y + 5, z + dz), Material.HOPPER);

                        Lantern data = (Lantern)Material.LANTERN.createBlockData();
                        data.setHanging(true);

                        region.setBlockData(new Location(region.getWorld(), x + dx, y + 4, z + dz), data);
                    }
                }
            }
        }
    }
}
