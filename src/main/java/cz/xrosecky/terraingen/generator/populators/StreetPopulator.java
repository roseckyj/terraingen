package cz.xrosecky.terraingen.generator.populators;

import cz.xrosecky.terraingen.data.DataChunk;
import cz.xrosecky.terraingen.data.DataStorage;
import cz.xrosecky.terraingen.data.types.LineSegment;
import cz.xrosecky.terraingen.data.types.StreetSegment;
import cz.xrosecky.terraingen.data.types.StreetType;
import cz.xrosecky.terraingen.data.types.Tree;
import cz.xrosecky.terraingen.generator.utils.RandomMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Random;

public class StreetPopulator extends BlockPopulator {
    private static Random rnd = new Random();

    private static HashMap<StreetType, RandomMaterial> streetMaterial = new HashMap<StreetType, RandomMaterial>() {{
        put(StreetType.UNKNOWN, new RandomMaterial(new Material[]{
                Material.GRAVEL, Material.STONE, Material.COBBLESTONE, Material.COARSE_DIRT, Material.DIRT_PATH, Material.DIRT_PATH,
                Material.DIRT_PATH, Material.DIRT_PATH, Material.DIRT_PATH, Material.DIRT_PATH, Material.DIRT_PATH, Material.DIRT_PATH
        }, rnd, 2, 0.5));

        put(StreetType.MOTORWAY, new RandomMaterial(new Material[]{
                Material.STONE, Material.COBBLESTONE, Material.GRAVEL
        }, rnd, 4, 1));

        put(StreetType.TRUNK, get(StreetType.MOTORWAY));
        put(StreetType.PRIMARY, get(StreetType.MOTORWAY));
        put(StreetType.SECONDARY, get(StreetType.MOTORWAY));
        put(StreetType.TERTIARY, get(StreetType.MOTORWAY));
        put(StreetType.UNCLASSIFIED, get(StreetType.MOTORWAY));
        put(StreetType.RESIDENTIAL, get(StreetType.MOTORWAY));

        put(StreetType.MOTORWAY_LINK, get(StreetType.MOTORWAY));
        put(StreetType.TRUNK_LINK, get(StreetType.MOTORWAY));
        put(StreetType.PRIMARY_LINK, get(StreetType.MOTORWAY));
        put(StreetType.SECONDARY_LINK, get(StreetType.MOTORWAY));
        put(StreetType.TERTIARY_LINK, get(StreetType.MOTORWAY));

        put(StreetType.LIVING_STREET, get(StreetType.MOTORWAY));
        put(StreetType.SERVICE, get(StreetType.MOTORWAY));
        put(StreetType.PEDESTRIAN, get(StreetType.MOTORWAY));

        put(StreetType.TRACK, get(StreetType.UNKNOWN));
        put(StreetType.TRACK_GRADE1, get(StreetType.UNKNOWN));
        put(StreetType.TRACK_GRADE2, get(StreetType.UNKNOWN));
        put(StreetType.TRACK_GRADE3, get(StreetType.UNKNOWN));
        put(StreetType.TRACK_GRADE4, get(StreetType.UNKNOWN));
        put(StreetType.TRACK_GRADE5, get(StreetType.UNKNOWN));

        put(StreetType.FOOTWAY, new RandomMaterial(new Material[]{
                Material.DIRT_PATH
        }, rnd, 1, 1));
        put(StreetType.BRIDLEWAY, get(StreetType.FOOTWAY));
        put(StreetType.STEPS, get(StreetType.FOOTWAY));
        put(StreetType.PATH, get(StreetType.FOOTWAY));
        put(StreetType.CYCLEWAY, get(StreetType.FOOTWAY));
    }};

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
        for (StreetSegment street : dataChunk.streets) {
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

            RandomMaterial randMat = streetMaterial.getOrDefault(street.type, new RandomMaterial(new Material[]{}, rnd, 1, 0));

            for (long i = 0; i <= steps; i++) {
                long currX = Math.round(x);
                long currZ = Math.round(z);

                if (dataChunk.isInChunk(currX, currZ)) {
                    for (int jx = -(int)Math.floor(randMat.radius); jx <= (int)Math.ceil(randMat.radius); jx++) {
                        for (int jz = -(int)Math.floor(randMat.radius); jz <= (int)Math.ceil(randMat.radius); jz++) {
                            double dist = Math.sqrt(jx*jx + jz*jz);
                            long blockX = currX + jx;
                            long blockZ = currZ + jz;

                            int y = storage.getChunk((int)Math.floor(blockX / 16.0), (int)Math.floor(blockZ / 16.0)).getAlt((int) (blockX), (int) (blockZ));
                            Location loc = new Location(region.getWorld(), blockX, y, blockZ);

                            if (region.isInRegion(loc)) {
                                Material m = randMat.getMaterial(dist);
                                if(m != null && region.getType(new Location(region.getWorld(), blockX, y, blockZ)) == Material.GRASS_BLOCK) {
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
