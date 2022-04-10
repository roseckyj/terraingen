package cz.xrosecky.terraingen.generator.populators;

import cz.xrosecky.terraingen.data.DataChunk;
import cz.xrosecky.terraingen.data.DataStorage;
import cz.xrosecky.terraingen.data.types.Building;
import cz.xrosecky.terraingen.data.types.RoofType;
import cz.xrosecky.terraingen.generator.populators.materials.MaterialSchemes;
import cz.xrosecky.terraingen.generator.populators.materials.MaterialType;
import cz.xrosecky.terraingen.utils.Coords;
import cz.xrosecky.terraingen.utils.Point2D;
import cz.xrosecky.terraingen.utils.Pointf2D;
import cz.xrosecky.terraingen.utils.Pointf3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.postgis.LinearRing;
import org.postgis.Polygon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class BuildingPopulator extends BlockPopulator {
    public BuildingPopulator(JavaPlugin javaPlugin, DataStorage storage) {
        super();
        this.javaPlugin = javaPlugin;
        this.storage = storage;
    }

    private final JavaPlugin javaPlugin;
    private final DataStorage storage;

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion region) {
        DataChunk dataChunk = storage.getChunk(chunkX, chunkZ);
        for (Building b : dataChunk.buildings) {

            int minAlt = (int)Math.floor(Coords.normalizeY(b.alt));

            ArrayList<ArrayList<Pointf2D>> lines = new ArrayList<>();

            for (int polyNum = 0; polyNum < b.polygon.numPolygons(); polyNum++) {
                Polygon poly = b.polygon.getPolygon(polyNum);

                ArrayList<Pointf2D> current = new ArrayList<>();
                lines.add(current);

                for (int ringNum = 0; ringNum < poly.numRings(); ringNum++) {
                    LinearRing r = poly.getRing(ringNum);

                    for (int pointNum = 0; pointNum < r.numPoints(); pointNum++) {
                        current.add(Coords.latLonToXZ(new Pointf2D(r.getPoint(pointNum))));
                    }
                }
            }

            // the minimal floor height => actual is in the interval <floorHeight, 2 * floorHeight)
            int floorHeight = 4;
            int floorCount = (int) Math.floor(b.height / floorHeight);
            int floorRemainder = (int) b.height % floorHeight;
            // the first floor is taller
            int firstFloorHeight = floorHeight + floorRemainder;
            makeFloor(b, chunkX, chunkZ, region, firstFloorHeight, firstFloorHeight, Material.STONE_BRICKS);
            // other floors are the same height (do not draw ceiling, it is drawn with the roof)
            for (int floorIndex = 2; floorIndex < floorCount - 1; floorIndex++){
                int floorY = minAlt + floorIndex * floorHeight;
                makeFloor(b, chunkX, chunkZ, region, floorY, floorY, Material.STONE_BRICKS);
            }

            for (int j = 0; j < lines.size(); j++) {
                ArrayList<Pointf2D> points = lines.get(j);
                for (int i = 0; i < points.size(); i++) {
                    long x1 = Math.round(points.get(i).x);
                    long z1 = Math.round(points.get(i).z);
                    long x2 = Math.round(points.get((i + 1) % points.size()).x);
                    long z2 = Math.round(points.get((i + 1) % points.size()).z);

                    long dx = x2 - x1;
                    long dz = z2 - z1;

                    long steps = Math.max(Math.abs(dx), Math.abs(dz));

                    double x = x1;
                    double z = z1;

                    double stepX = (double) dx / steps;
                    double stepZ = (double) dz / steps;

                    for (long k = 0; k <= steps; k++) {
                        long currX = Math.round(x);
                        long currZ = Math.round(z);

                        if (dataChunk.isInChunk(currX, currZ)) {
                            for (int y = 0; y <= b.height; y++) {
                                Material m = determineMaterial(k, y, steps, (int) b.height, random);
                                region.setType(new Location(region.getWorld(), currX, y + minAlt, currZ), m);
                            }
                            if (b.roofType == RoofType.FLAT) {
                                region.setType(new Location(region.getWorld(), currX, minAlt + b.height + 1, currZ), Material.STONE_BRICKS);
                                region.setType(new Location(region.getWorld(), currX, minAlt + b.height + 2, currZ), Material.STONE_BRICKS);
                            }
                        }

                        x += stepX;
                        z += stepZ;
                    }
                }
            }

            if (b.roofType == RoofType.FLAT) {
                makeFloor(b, chunkX, chunkZ, region, (int) (minAlt + b.height) + 1, (int) (minAlt + b.height) + 1, Material.STONE_BRICKS);
            } else {
                makeRoof(b, region, (int) (minAlt + b.height) + 1, Material.OAK_PLANKS, lines, b.height > 40 ? 4 : b.roofHeight > 8 ? 2 : 1);
            }
        }
    }

    private Material determineMaterial(long x, int y, long width, int height, @NotNull Random random) {
        MaterialType type = MaterialType.None;

        int windowSpacing = 1;

        int floorHeight = 4;
        int floorCount = height / floorHeight;
        int floorRemainder = height % floorHeight;
        long currentFloor = y / floorHeight;
        long indexInFloor = y % floorHeight;

        int regionWidth = 3;
        int regionBorder = 1;
        long regionCount = width / regionWidth;
        long regionRemainder = width % regionWidth;
        long currentRegion = x / regionWidth;
        long indexInRegion = x % regionWidth;

        boolean oddRegions = regionCount % 2 == 1;
        boolean largerRegion = regionRemainder != 0;
        // in case of the odd number of regions extend the one in the middle, otherwise the first one
        boolean currentLargerRegion = largerRegion && ((oddRegions && currentRegion == Math.ceil(regionCount / 2.0))
                || currentRegion == 0);

        // corner
        if (x == 0 || x == width - 1) {
            type = MaterialType.Pillar;
        }
        // edge regions (and larger region)
        else if (currentRegion == 0 || currentRegion == regionCount || currentLargerRegion) {
            type = MaterialType.WallPrimary;
        }
        // window
        else if (currentRegion % (1 + windowSpacing) == windowSpacing &&
                indexInRegion >= regionBorder && indexInRegion < regionWidth - regionBorder
                && indexInFloor >= regionBorder && indexInFloor < floorHeight - regionBorder) {
            type = MaterialType.Window;
        }
        else {
            type = MaterialType.WallSecondary;
        }

        String[] allSchemes = MaterialSchemes.getSchemes();
        int rnd = random.nextInt(allSchemes.length);
        return MaterialSchemes.getMaterial(allSchemes[rnd], type);
    }

    private void makeFloor(Building b, int chunkX, int chunkZ, LimitedRegion region, int yFrom, int yTo, Material material) {
        float minAlt = b.alt;
        for (int polyNum = 0; polyNum < b.originalPolygon.numPolygons(); polyNum++) {
            Polygon poly = b.originalPolygon.getPolygon(polyNum);

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

                            for (int y = yFrom; y <= yTo; y++) {
                                region.setType(new Location(region.getWorld(), x, y, z), material);
                            }
                        }
                    }
                }
            }
        }
    }

    private void makeRoof(Building b, LimitedRegion region, int y, Material material, ArrayList<ArrayList<Pointf2D>> lines, int every) {
        int OVERHANG = 4;
        int LOG_SPACING = 5;

        // Limits measurement

        float minAlt = b.alt;
        double minX = Integer.MAX_VALUE;
        double maxX = Integer.MIN_VALUE;
        double minZ = Integer.MAX_VALUE;
        double maxZ = Integer.MIN_VALUE;

        for (int polyNum = 0; polyNum < b.originalPolygon.numPolygons(); polyNum++) {
            Polygon poly = b.originalPolygon.getPolygon(polyNum);

            for (int ringNum = 0; ringNum < poly.numRings(); ringNum++) {
                LinearRing r = poly.getRing(ringNum);

                for (int i = 0; i < 3; i++) {
                    Pointf3D p = Coords.latLonToXZ(new Pointf3D(r.getPoint(i)));
                    minX = Math.min(minX, p.x);
                    maxX = Math.max(maxX, p.x);
                    minZ = Math.min(minZ, p.z);
                    maxZ = Math.max(maxZ, p.z);
                }
            }
        }

        // Initial fill

        HashSet<Point2D> placed = new HashSet<Point2D>();

        for (int polyNum = 0; polyNum < b.originalPolygon.numPolygons(); polyNum++) {
            Polygon poly = b.originalPolygon.getPolygon(polyNum);

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

                for (int x = (int) Math.floor(minX); x < (int) Math.ceil(maxX); x++) {
                    for (int z = (int) Math.floor(minZ); z < (int) Math.ceil(maxZ); z++) {
                        double Eab = E(p[0].x, p[0].z, p[1].x, p[1].z, x, z);
                        double Ebc = E(p[1].x, p[1].z, p[2].x, p[2].z, x, z);
                        double Eca = E(p[2].x, p[2].z, p[0].x, p[0].z, x, z);
                        if ((Eab <= 0 && Ebc <= 0 && Eca <= 0) || (Eab >= 0 && Ebc >= 0 && Eca >= 0)) {
                            // Inside triangle

                            if (region.isInRegion(x, y, z)) {
                                region.setType(new Location(region.getWorld(), x, y, z), material);
                            }
                            placed.add(new Point2D(x, z));
                        }
                    }
                }
            }
        }

        // Overhang

        HashSet<Point2D> temp = new HashSet<Point2D>();

        for (int i = 0; i < OVERHANG; i++) {
            for (Point2D pt : placed) {
                temp.add(new Point2D(pt.x, pt.z));

                if (region.isInRegion(pt.x + 1, y, pt.z)) {
                    region.setType(new Location(region.getWorld(), pt.x + 1, y, pt.z), material);
                }
                temp.add(new Point2D(pt.x + 1, pt.z));

                if (region.isInRegion(pt.x - 1, y, pt.z)) {
                    region.setType(new Location(region.getWorld(), pt.x - 1, y, pt.z), material);
                }
                temp.add(new Point2D(pt.x - 1, pt.z));

                if (region.isInRegion(pt.x, y, pt.z + 1)) {
                    region.setType(new Location(region.getWorld(), pt.x, y, pt.z + 1), material);
                }
                temp.add(new Point2D(pt.x, pt.z + 1));

                if (region.isInRegion(pt.x, y, pt.z - 1)) {
                    region.setType(new Location(region.getWorld(), pt.x, y, pt.z - 1), material);
                }
                temp.add(new Point2D(pt.x, pt.z - 1));

            }
        }

        // Other layers

        placed = temp;

        int currentY = y;
        int layerNum = 0;

        while (placed.size() > 0) {

            temp = new HashSet<Point2D>();
            currentY++;

            for (Point2D pt : placed) {
                if (
                    placed.contains(new Point2D(pt.x, pt.z))
                ) {
                    if (
                        (layerNum % every > 0) || (
                        placed.contains(new Point2D(pt.x + 1, pt.z + 1)) &&
                        placed.contains(new Point2D(pt.x - 1, pt.z + 1)) &&
                        placed.contains(new Point2D(pt.x + 1, pt.z - 1)) &&
                        placed.contains(new Point2D(pt.x - 1, pt.z - 1)))
                    ) {
                        temp.add(new Point2D(pt.x, pt.z));
                        if (region.isInRegion(pt.x, currentY, pt.z)) {
                            region.setType(new Location(region.getWorld(), pt.x, currentY, pt.z), material);
                        }
                    }
                }
            }

            layerNum++;

            placed = temp;
        }
    }

    private double E(double Ax, double Ay, double Bx, double By, double Px, double Py) {
        return (By - Ay) * (Px - Ax) - (Bx - Ax) * (Py - Ay);
    }
}
