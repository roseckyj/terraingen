package cz.xrosecky.terraingen.generator.populators;

import cz.xrosecky.terraingen.data.DataChunk;
import cz.xrosecky.terraingen.data.DataStorage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Lantern;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class DecorationsPopulator extends BlockPopulator {

    public DecorationsPopulator(JavaPlugin javaPlugin, DataStorage storage) {
        super();
        this.javaPlugin = javaPlugin;
        this.storage = storage;
    }

    private final JavaPlugin javaPlugin;
    private final DataStorage storage;

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion region) {
        DataChunk dataChunk = storage.getChunk(chunkX, chunkZ);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int alt = dataChunk.getAlt(x, z) + 1;

                Location loc = new Location(region.getWorld(), x + chunkX * 16, alt, z + chunkZ * 16);
                Location locBellow = new Location(region.getWorld(), x + chunkX * 16, alt - 1, z + chunkZ * 16);
                Location locAbove = new Location(region.getWorld(), x + chunkX * 16, alt + 1, z + chunkZ * 16);

                if (region.getType(locBellow) != Material.GRASS_BLOCK || region.getType(locAbove) != Material.AIR) {
                    continue;
                }

                int rnd = random.nextInt(500);

                if (rnd < 20) {
                    region.setType(loc, Material.GRASS);
                } else if (rnd < 25) {
                    Bisected dataBottom = (Bisected)Material.TALL_GRASS.createBlockData();
                    dataBottom.setHalf(Bisected.Half.BOTTOM);
                    region.setBlockData(loc, dataBottom);

                    Bisected dataTop = (Bisected)Material.TALL_GRASS.createBlockData();
                    dataTop.setHalf(Bisected.Half.TOP);
                    region.setBlockData(locAbove, dataTop);
                } else if (rnd < 28) {
                    region.setType(loc, Material.DANDELION);
                } else if (rnd < 30) {
                    region.setType(loc, Material.POPPY);
                } else if (rnd < 31) {
                    region.setType(loc, Material.CORNFLOWER);
                }
            }
        }
    }
}
