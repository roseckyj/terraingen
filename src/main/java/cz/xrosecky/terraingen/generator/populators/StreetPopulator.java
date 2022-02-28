package cz.xrosecky.terraingen.generator.populators;

import cz.xrosecky.terraingen.data.DataChunk;
import cz.xrosecky.terraingen.data.DataStorage;
import cz.xrosecky.terraingen.data.types.LineSegment;
import cz.xrosecky.terraingen.data.types.Tree;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class StreetPopulator extends BlockPopulator {
    private int STREET_JITTER = 3;
    private int STREET_RAND_DECREASE = 5;

    public StreetPopulator(JavaPlugin javaPlugin, DataStorage storage) {
        super();
        this.javaPlugin = javaPlugin;
        this.storage = storage;
    }

    private final JavaPlugin javaPlugin;
    private final DataStorage storage;

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion region) {
        DataChunk dataChunk = storage.getChunk(chunkX, chunkZ);
        for (LineSegment street : dataChunk.streets) {
            long x1 = street.x1;
            long z1 = street.z1;
            long x2 = street.x2;
            long z2 = street.z2;

            long dx = x2 - x1;
            long dz = z2 - z1;

            long steps = Math.max(Math.abs(dx), Math.abs(dz));

            double x = x1;
            double z = z1;

            double stepX = (double)dx / steps;
            double stepZ = (double)dz / steps;

            for (long i = 0; i <= steps; i++) {
                long currX = Math.round(x);
                long currZ = Math.round(z);

                if (dataChunk.isInChunk(currX, currZ)) {
                    for (int jx = -STREET_JITTER; jx <= STREET_JITTER; jx++) {
                        for (int jz = -STREET_JITTER; jz <= STREET_JITTER; jz++) {
                            double dist = Math.sqrt(jx*jx + jz*jz);
                            if (Math.ceil(dist) == 0 || random.nextInt((int)Math.ceil(dist) * STREET_RAND_DECREASE) == 0) {
                                long blockX = currX + jx;
                                long blockZ = currZ + jz;

                                int y = storage.getChunk((int)Math.floor(blockX / 16.0), (int)Math.floor(blockZ / 16.0)).getAlt((int) (blockX), (int) (blockZ));
                                Location loc = new Location(region.getWorld(), blockX, y, blockZ);

                                if (region.isInRegion(loc)) {
                                    Material m = Material.DIRT_PATH;
                                    switch(random.nextInt(12)) {
                                        case 0:
                                            m = Material.GRAVEL;
                                            break;
                                        case 1:
                                            m = Material.STONE;
                                            break;
                                        case 2:
                                            m = Material.COBBLESTONE;
                                            break;
                                        case 3:
                                            m = Material.COARSE_DIRT;
                                            break;
                                    }
                                    region.setType(new Location(region.getWorld(), blockX, y, blockZ), m);
                                }
                            }
                        }
                    }
                }

                x += stepX;
                z += stepZ;
            }
        }
    }
}
