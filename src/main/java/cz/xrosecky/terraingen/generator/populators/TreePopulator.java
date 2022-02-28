package cz.xrosecky.terraingen.generator.populators;

import cz.xrosecky.terraingen.data.DataChunk;
import cz.xrosecky.terraingen.data.DataStorage;
import cz.xrosecky.terraingen.data.types.Tree;
import cz.xrosecky.terraingen.utils.Coords;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundGroup;
import org.bukkit.TreeType;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class TreePopulator extends BlockPopulator {
    public TreePopulator(JavaPlugin javaPlugin, DataStorage storage) {
        super();
        this.javaPlugin = javaPlugin;
        this.storage = storage;
    }

    private final JavaPlugin javaPlugin;
    private final DataStorage storage;

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion region) {
        DataChunk dataChunk = storage.getChunk(chunkX, chunkZ);
        for (Tree tree : dataChunk.trees) {
            long x = tree.x;
            long z = tree.z;

            int y = dataChunk.getAlt((int)(x), (int)(z)) + 1;

            Location loc = new Location(region.getWorld(), x, y, z);

            if (!region.isInRegion(loc)) {
                continue;
            }

            TreeType type = TreeType.TREE;

            switch (tree.type) {
                case OAK_TREE:
                    type = TreeType.TREE;
                    break;
                case SPRUCE_TREE:
                    type = TreeType.REDWOOD;
                    break;
                case OAK_BUSH:
                case SPRUCE_BUSH:
                    type = TreeType.JUNGLE_BUSH;
                    break;
            }

            if (!region.generateTree(new Location(region.getWorld(), x, y, z), random, type)) {
                this.javaPlugin.getLogger().info("Could not generate tree at " + x + " " + y + " " + z);
            }
        }
    }
}
