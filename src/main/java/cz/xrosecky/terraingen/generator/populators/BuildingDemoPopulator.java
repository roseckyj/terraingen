package cz.xrosecky.terraingen.generator.populators;

import cz.xrosecky.terraingen.data.DataChunk;
import cz.xrosecky.terraingen.data.DataStorage;
import cz.xrosecky.terraingen.data.types.Building;
import cz.xrosecky.terraingen.data.types.Tree;
import cz.xrosecky.terraingen.utils.Coords;
import cz.xrosecky.terraingen.utils.Point2D;
import cz.xrosecky.terraingen.utils.Pointf3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.postgis.LinearRing;
import org.postgis.Point;
import org.postgis.Polygon;

import java.util.Random;

public class BuildingDemoPopulator extends BlockPopulator {
    public BuildingDemoPopulator(JavaPlugin javaPlugin, DataStorage storage) {
        super();
        this.javaPlugin = javaPlugin;
        this.storage = storage;
    }

    private final JavaPlugin javaPlugin;
    private final DataStorage storage;

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion region) {
        DataChunk dataChunk = storage.getChunk(chunkX, chunkZ);
//        javaPlugin.getLogger().info(dataChunk.buildings.size() + " buildings in chunk " + chunkX + " " + chunkZ);
        for (Building b : dataChunk.buildings) {
//            int minAlt = Integer.MAX_VALUE;
//            int maxAlt = Integer.MIN_VALUE;
//
//            int minX = Integer.MAX_VALUE;
//            int maxX = Integer.MIN_VALUE;
//            int minZ = Integer.MAX_VALUE;
//            int maxZ = Integer.MIN_VALUE;
//
//            for (int pointNum = 0; pointNum < b.polygon.numPoints(); pointNum++) {
//                Pointf3D pt = Coords.latLonToXZ(new Pointf3D(b.polygon.getPoint(pointNum)));
//                minX = Math.min(minX, (int)Math.floor(pt.x));
//                maxX = Math.max(maxX, (int)Math.ceil(pt.x));
//                minZ = Math.min(minZ, (int)Math.floor(pt.z));
//                maxZ = Math.max(maxZ, (int)Math.ceil(pt.z));
//            }
//
//            for (int x = minX; x <= maxX; x++) {
//                for (int z = minZ; z <= maxZ; z++) {
//                    int alt = storage.getChunk((int)Math.floor(x / 16.0), (int)Math.floor(z / 16.0)).getAlt(x, z);
//                    minAlt = Math.min(minAlt, alt);
//                    maxAlt = Math.max(maxAlt, alt);
//                }
//            }

            int minAlt = (int)Math.floor(Coords.normalizeY(b.alt));

            for (int polyNum = 0; polyNum < b.polygon.numPolygons(); polyNum++) {
                Polygon poly = b.polygon.getPolygon(polyNum);

                for (int ringNum = 0; ringNum < poly.numRings(); ringNum++) {
                    LinearRing r = poly.getRing(ringNum);

                    boolean skip = false;
                    for (int i = 0; i < 3; i++) {
                        skip = skip || (Math.abs(r.getPoint(i).z - b.alt) > 0.01);
                    }
                    if (skip) {
                        continue;
                    }

                    Pointf3D[] p = new Pointf3D[3];
                    for (int i = 0; i < 3; i++) {
                        p[i] = Coords.latLonToXZ(new Pointf3D(r.getPoint(i)));
                    }

                    for (int x = chunkX * 16; x < chunkX * 16 + 16; x++) {
                        for (int z = chunkZ * 16; z < chunkZ * 16 + 16; z++) {
                            double Eab = E(p[0].x, p[0].z, p[1].x, p[1].z, x, z);
                            double Ebc = E(p[1].x, p[1].z, p[2].x, p[2].z, x, z);
                            double Eca = E(p[2].x, p[2].z, p[0].x, p[0].z, x, z);
                            if ((Eab <= 0 && Ebc <= 0 && Eca <= 0) || (Eab >= 0 && Ebc >= 0 && Eca >= 0)) {
                                // Inside triangle

                                for (int y = minAlt; y < minAlt + b.height; y++) {
                                    region.setType(new Location(region.getWorld(), x, y, z), Material.STONE_BRICKS);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private double E(double Ax, double Ay, double Bx, double By, double Px, double Py) {
        return (By - Ay) * (Px - Ax) - (Bx - Ax) * (Py - Ay);
    }
}
