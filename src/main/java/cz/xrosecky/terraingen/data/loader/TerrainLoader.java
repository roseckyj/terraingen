package cz.xrosecky.terraingen.data.loader;

import cz.xrosecky.terraingen.data.DataStorage;
import cz.xrosecky.terraingen.utils.Coords;
import cz.xrosecky.terraingen.utils.Pointf2D;
import cz.xrosecky.terraingen.utils.Pointf3D;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class TerrainLoader {
    private final DataStorage storage;
    private final JavaPlugin plugin;
    private JSONParser jsonParser = new JSONParser();

    public TerrainLoader(DataStorage storage, JavaPlugin plugin) {
        this.storage = storage;
        this.plugin = plugin;
    }

    public int load(String path) {
        File dir = new File(path);
        String files[] = dir.list();
        int loaded = 0;
        int loadedFiles = 0;
        for (String filename : files) {
            if (!filename.endsWith(".txt") && !filename.endsWith(".xyz") && !filename.endsWith(".xyz1")) continue;

            try (BufferedReader br = new BufferedReader(new FileReader(path + "/" + filename))) {
                String line;
                int i = 0;
                double[] Xs = new double[3];
                double[] Zs = new double[3];
                double[] Hs = new double[3];

                while ((line = br.readLine()) != null) {
                    if (i < 3) {
                        String[] values = line.split("\\s+");
                        if (values.length != 4) {
                            this.plugin.getLogger().severe("! Found " + values.length + " items on a row");
                            throw new ParseException(-1);
                        }
                        double Y = Double.parseDouble(values[0]);
                        double X = Double.parseDouble(values[1]);
                        double H = Double.parseDouble(values[2]);
                        Pointf3D latlon = Coords.krovakToLatLonAlt(Y, X, H);
                        Pointf2D projected = Coords.latLonToXZ(latlon.lat(), latlon.lon());

                        Xs[i] = projected.x;
                        Zs[i] = projected.z;
                        Hs[i] = latlon.alt();
                        i++;
                    } else {
                        // Pineda algorithm - see https://is.muni.cz/auth/el/fi/jaro2021/PB009/um/slides/Lecture3_Rasterization_and_Filling.pdf
                        int minX = Integer.MAX_VALUE;
                        int maxX = Integer.MIN_VALUE;
                        int minZ = Integer.MAX_VALUE;
                        int maxZ = Integer.MIN_VALUE;

                        for (int j = 0; j < 3; j++) {
                            minX = Math.min(minX, (int)Math.floor(Xs[j]));
                            maxX = Math.max(maxX, (int)Math.ceil(Xs[j]));
                            minZ = Math.min(minZ, (int)Math.floor(Zs[j]));
                            maxZ = Math.max(maxZ, (int)Math.ceil(Zs[j]));
                        }

                        for (int x = minX; x <= maxX; x++) {
                            for (int z = minZ; z <= maxZ; z++) {
                                double Eab = E(Xs[0], Zs[0], Xs[1], Zs[1], x, z);
                                double Ebc = E(Xs[1], Zs[1], Xs[2], Zs[2], x, z);
                                double Eca = E(Xs[2], Zs[2], Xs[0], Zs[0], x, z);
                                double Eabc = E(Xs[0], Zs[0], Xs[1], Zs[1], Xs[2], Zs[2]);
                                if ((Eab <= 0 && Ebc <= 0 && Eca <= 0) || (Eab >= 0 && Ebc >= 0 && Eca >= 0)) {
                                    // Inside triangle
                                    Eab = Math.abs(Eab);
                                    Ebc = Math.abs(Ebc);
                                    Eca = Math.abs(Eca);
                                    Eabc = Math.abs(Eabc);
                                    double la = Ebc / Eabc;
                                    double lb = Eca / Eabc;
                                    double lc = Eab / Eabc;
                                    storage.getChunk((int)Math.floor(x / 16.0), (int)Math.floor(z / 16.0)).setAlt(x, z, la * Hs[0] + lb * Hs[1] + lc * Hs[2]);
                                }
                            }
                        }

                        i = 0;
                        loaded++;
                    }
                }
            } catch (FileNotFoundException e) {
                this.plugin.getLogger().severe("! File not found");
                return 0;
            } catch (IOException e) {
                this.plugin.getLogger().severe("! File could not be accessed");
                return 0;
            } catch (ParseException e) {
                this.plugin.getLogger().severe("! Could not parse the input file");
                return 0;
            }

            loadedFiles++;
            this.plugin.getLogger().info("> Loaded file " + loadedFiles + " of " + files.length);
        }
        return loaded;
    }

    private double E(double Ax, double Ay, double Bx, double By, double Px, double Py) {
        return (By - Ay) * (Px - Ax) - (Bx - Ax) * (Py - Ay);
    }
}
