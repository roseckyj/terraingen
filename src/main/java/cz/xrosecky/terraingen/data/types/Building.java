package cz.xrosecky.terraingen.data.types;

import cz.xrosecky.terraingen.utils.Coords;
import cz.xrosecky.terraingen.utils.Pointf3D;
import org.postgis.MultiPolygon;

public class Building extends AbstractObject {
    public float height;
    public float alt;
    public float roofHeight;
    public MultiPolygon polygon;
    public MultiPolygon originalPolygon;
    public RoofType roofType;

    public Building(float height, float alt, float roofHeight, RoofType roofType, MultiPolygon polygon, MultiPolygon originalPolygon) {
        super(Coords.latLonToXZ(new Pointf3D(polygon.getPoint(0))).x, Coords.latLonToXZ(new Pointf3D(polygon.getPoint(0))).z);
        this.height = height;
        this.alt = alt;
        this.roofHeight = roofHeight;
        this.roofType = roofType;
        this.polygon = polygon;
        this.originalPolygon = originalPolygon;
    }
}