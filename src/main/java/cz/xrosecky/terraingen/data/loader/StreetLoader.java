package cz.xrosecky.terraingen.data.loader;

import cz.xrosecky.terraingen.data.DataStorage;
import cz.xrosecky.terraingen.data.types.LineSegment;
import cz.xrosecky.terraingen.data.types.Tree;
import cz.xrosecky.terraingen.data.types.TreeType;
import cz.xrosecky.terraingen.utils.Coords;
import cz.xrosecky.terraingen.utils.Pointf2D;
import cz.xrosecky.terraingen.utils.StringUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

public class StreetLoader {
    private final DataStorage storage;
    private final JavaPlugin plugin;
    private JSONParser jsonParser = new JSONParser();

    public StreetLoader(DataStorage storage, JavaPlugin plugin) {
        this.storage = storage;
        this.plugin = plugin;
    }

    public int load(String path) {
        try (FileReader reader = new FileReader(path)) {
            JSONObject object = (JSONObject) jsonParser.parse(reader);
            JSONArray features = (JSONArray) object.get("features");

            for (Object streetObject : features) {
                JSONObject street = (JSONObject) streetObject;
                // JSONObject properties = (JSONObject) street.get("properties");
                // String streetName = (String) properties.get("nazev");
                JSONObject geometry = (JSONObject) street.get("geometry");
                JSONArray coords = ((JSONArray) geometry.get("coordinates"));
                for (int i = 0; i < coords.size(); i++) {
                    JSONArray points = ((JSONArray) coords.get(i));
                    for (int seg = 0; seg < points.size() - 1; seg++) {
                        JSONArray point1 = ((JSONArray) points.get(seg));
                        JSONArray point2 = ((JSONArray) points.get(seg + 1));
                        Pointf2D xz1 = Coords.latLonToXZ((double) point1.get(1), (double) point1.get(0));
                        Pointf2D xz2 = Coords.latLonToXZ((double) point2.get(1), (double) point2.get(0));
                        long x1 = Math.round(xz1.x);
                        long z1 = Math.round(xz1.z);
                        long x2 = Math.round(xz2.x);
                        long z2 = Math.round(xz2.z);

                        long minX = Math.min(x1, x2);
                        long maxX = Math.max(x1, x2);
                        long minZ = Math.min(z1, z2);
                        long maxZ = Math.max(z1, z2);

                        LineSegment segment = new LineSegment(x1, z1, x2, z2);

                        for (int x = (int) Math.floor(minX / 16.0); x <= (int) Math.ceil(maxX / 16.0); x++) {
                            for (int z = (int) Math.floor(minZ / 16.0); z <= (int) Math.ceil(maxZ / 16.0); z++) {
                                storage.getChunk(x, z).streets.add(segment);
                            }
                        }
                    }
                }
            }

            return features.size();

        } catch (FileNotFoundException e) {
            this.plugin.getLogger().severe("! File not found");
            return 0;
        } catch (IOException e) {
            this.plugin.getLogger().severe("! File could not be accessed");
            return 0;
        } catch (ParseException e) {
            this.plugin.getLogger().severe("! Could not parse the json file");
            return 0;
        }
    }
}
