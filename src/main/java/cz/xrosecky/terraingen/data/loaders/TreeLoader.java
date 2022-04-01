package cz.xrosecky.terraingen.data.loaders;

import cz.xrosecky.terraingen.data.DataChunk;
import cz.xrosecky.terraingen.data.DataStorage;
import cz.xrosecky.terraingen.data.types.Tree;
import cz.xrosecky.terraingen.data.types.TreeType;
import cz.xrosecky.terraingen.utils.Coords;
import cz.xrosecky.terraingen.utils.Point2D;
import cz.xrosecky.terraingen.utils.Pointf2D;
import cz.xrosecky.terraingen.utils.Pointf3D;
import org.bukkit.plugin.java.JavaPlugin;
import org.postgis.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TreeLoader extends AbstractLoader {

    public TreeLoader(JavaPlugin plugin, DataStorage storage, Connection conn) {
        super(plugin, storage, conn);
    }

    @Override
    public void LoadRegion(Point2D from, Point2D to) {
        try {
            Statement s = conn.createStatement();
            ResultSet r = s.executeQuery(String.format("SELECT geom, druh_bio_k FROM brno_trees WHERE ST_Intersects(geom, %s)", DatabaseUtils.CornersToGeom(from, to)));

            while (r.next()) {
                MultiPoint geom = (MultiPoint) ((PGgeometry)r.getObject(1)).getGeometry();

                String treeType = r.getString(2);
                boolean isBush = treeType.contains("keře");
                boolean isOak = treeType.contains("listnaté");
                TreeType type = TreeType.OAK_TREE;
                if (isBush && isOak) type = TreeType.OAK_BUSH;
                if (!isBush && isOak) type = TreeType.OAK_TREE;
                if (isBush && !isOak) type = TreeType.SPRUCE_BUSH;
                if (!isBush && !isOak) type = TreeType.SPRUCE_TREE;

                for (int i = 0; i < geom.numPoints(); i++) {
                    Pointf3D point = Coords.latLonToXZ(new Pointf3D(geom.getPoint(i)));

                    int setX = (int) Math.floor(point.x / 16.0);
                    int setZ = (int) Math.floor(point.z / 16.0);

                    Point2D fetchPoint = new Point2D(setX, setZ);
                    if (new Point2D((int)Math.round(point.x), (int)Math.round(point.z)).within(from, to)) {
                        DataChunk chunk = storage.chunks.get(fetchPoint);
                        chunk.trees.add(new Tree(Math.round(point.x), Math.round(point.z), type));
                    }
                }
            }
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
