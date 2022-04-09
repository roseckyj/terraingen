package cz.xrosecky.terraingen.generator;

import cz.xrosecky.terraingen.data.DataStorage;
import cz.xrosecky.terraingen.generator.annotations.ChunkGenInfo;
import cz.xrosecky.terraingen.generator.populators.*;
import cz.xrosecky.terraingen.generator.utils.SingleBiomeProvider;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ChunkGenInfo(versions = {"1.17.1", "1.18", "1.18.1"})
public class ChunkGen extends ChunkGenerator {

    public ChunkGen(JavaPlugin javaPlugin, DataStorage storage) {
        this.javaPlugin = javaPlugin;
        this.storage = storage;
        this.populators.add(new TreePopulator(javaPlugin, storage));
        this.populators.add(new StreetPopulator(javaPlugin, storage));
        this.populators.add(new LightPopulator(javaPlugin, storage));
        this.populators.add(new BuildingPopulator(javaPlugin, storage));
        this.populators.add(new DecorationsPopulator(javaPlugin, storage));
        this.populators.add(new SpatialStatsPopulator(javaPlugin, storage));
    }

    private final JavaPlugin javaPlugin;
    private final DataStorage storage;
    private final ArrayList<BlockPopulator> populators = new ArrayList<BlockPopulator>();

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        int y = storage.getChunk(0, 0).getAlt(0, 0);
        return new Location(world, 0d, y, 0d);
    }

    @Override
    public boolean shouldGenerateCaves() {
        return false;
    }

    @Override
    public boolean shouldGenerateDecorations() {
        return false;
    }

    @Override
    public boolean shouldGenerateMobs() {
        return false;
    }

    @Override
    public boolean shouldGenerateStructures() {
        return false;
    }

    @Override
    public boolean shouldGenerateNoise() {
        return false;
    }

    @Override
    public boolean shouldGenerateSurface() {
        return true;
    }

    @Override
    public boolean shouldGenerateBedrock() {
        return true;
    }

    @Override
    public BiomeProvider getDefaultBiomeProvider(WorldInfo worldInfo) {
        return new SingleBiomeProvider(Biome.PLAINS);
    }

    @Override
    public void generateBedrock(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (chunkData.getType(x, worldInfo.getMinHeight(), z) != Material.AIR) {
                    chunkData.setBlock(x, worldInfo.getMinHeight(), z, Material.BEDROCK);
                }
            }
        }
    }

    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int normalized = storage.getChunk(chunkX, chunkZ).getAlt(x + chunkX * 16, z + chunkZ * 16);
                if (normalized != 0) {
                    for (int y = worldInfo.getMinHeight(); y <= normalized; y++) {
                        int relative = normalized - y;

                        if (relative == 0) chunkData.setBlock(x, y, z, Material.GRASS_BLOCK);
                        else if (relative <= 2) chunkData.setBlock(x, y, z, Material.DIRT);
                        else chunkData.setBlock(x, y, z, Material.STONE);
                    }
                }
            }
        }
    }

    @Override
    public @NotNull List<BlockPopulator> getDefaultPopulators(@NotNull World world) {
        return this.populators;
    }
}