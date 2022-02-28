package cz.xrosecky.terraingen.data.loader;

import cz.xrosecky.terraingen.data.DataStorage;
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

public class TreeLoader {
    private final DataStorage storage;
    private final JavaPlugin plugin;
    private JSONParser jsonParser = new JSONParser();

    public TreeLoader(DataStorage storage, JavaPlugin plugin) {
        this.storage = storage;
        this.plugin = plugin;
    }

    public int load(String path) {
        try (FileReader reader = new FileReader(path)) {
            JSONObject object = (JSONObject) jsonParser.parse(reader);
            JSONArray features = (JSONArray) object.get("features");

            for (Object treeObject : features) {
                JSONObject tree = (JSONObject) treeObject;
                JSONObject properties = (JSONObject) tree.get("properties");
                String treeType = StringUtils.unaccent((String) properties.get("druh_bio_kod")).toLowerCase(Locale.ROOT);
                JSONObject geometry = (JSONObject) tree.get("geometry");
                JSONArray coords = (JSONArray) (((JSONArray) geometry.get("coordinates")).get(0));
                Pointf2D xz = Coords.latLonToXZ((double) coords.get(1), (double) coords.get(0));
                long x = Math.round(xz.x);
                long z = Math.round(xz.z);
                // The diacritics are removed in a weird way...
                boolean isBush = treeType.contains("kele");
                boolean isOak = treeType.contains("listnata");
                TreeType type = TreeType.OAK_TREE;
                if (isBush && isOak) type = TreeType.OAK_BUSH;
                if (!isBush && isOak) type = TreeType.OAK_TREE;
                if (isBush && !isOak) type = TreeType.SPRUCE_BUSH;
                if (!isBush && !isOak) type = TreeType.SPRUCE_TREE;
                Tree treeObj = new Tree(x, z, type);
                //this.getLogger().info("> TREE AT " + xz.x + " " + xz.z);
                storage.getChunk((int)Math.floor(x / 16.0), (int)Math.floor(z / 16.0)).trees.add(treeObj);
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
