package cz.xrosecky.terraingen.data.types;

public enum StreetType {
    // See the data description at http://download.geofabrik.de/osm-data-in-gis-formats-free.pdf

    /**
     * <h2>Major Roads</h2>
     * Motorway/freeway <br/>
     * <br/>
     * Code: 5111
     **/
    MOTORWAY(5111),

    /**
     * <h2>Major Roads</h2>
     * Important roads, typically divided <br/>
     * <br/>
     * Code: 5112
     **/
    TRUNK(5112),

    /**
     * <h2>Major Roads</h2>
     * Primary roads, typically national. <br/>
     * <br/>
     * Code: 5113
     **/
    PRIMARY(5113),

    /**
     * <h2>Major Roads</h2>
     * Secondary roads, typically regional. <br/>
     * <br/>
     * Code: 5114
     **/
    SECONDARY(5114),

    /**
     * <h2>Major Road</h2>
     * Tertiary roads, typically local. <br/>
     * <br/>
     * Code: 5115
     **/
    TERTIARY(5115),

    /**
     * <h2>Minor Roads</h2>
     * Smaller local roads <br/>
     * <br/>
     * Code: 5121
     **/
    UNCLASSIFIED(5121),

    /**
     * <h2>Minor Roads</h2>
     * Roads in residential areas <br/>
     * <br/>
     * Code: 5122
     **/
    RESIDENTIAL(5122),

    /**
     * <h2>Minor Roads</h2>
     * Streets where pedestrians have priority <br/>
     * <br/>
     * Code: 5123
     **/
    LIVING_STREET(5123),

    /**
     * <h2>Minor Roads</h2>
     * Pedestrian only streets <br/>
     * <br/>
     * Code: 5124
     **/
    PEDESTRIAN(5124),

    /**
     * <h2>Highway Links (Sliproads / Ramps)</h2>
     * Road that connect from one road to another of the same of lower category. <br/>
     * <br/>
     * Code: 5131
     **/
    MOTORWAY_LINK(5131),

    /**
     * <h2>Highway Links (Sliproads / Ramps)</h2>
     * Road that connect from one road to another of the same of lower category. <br/>
     * <br/>
     * Code: 5132
     **/
    TRUNK_LINK(5132),

    /**
     * <h2>Highway Links (Sliproads / Ramps)</h2>
     * Road that connect from one road to another of the same of lower category. <br/>
     * <br/>
     * Code: 5133
     **/
    PRIMARY_LINK(5133),

    /**
     * <h2>Highway Links (Sliproads / Ramps)</h2>
     * Road that connect from one road to another of the same of lower category. <br/>
     * <br/>
     * Code: 5134
     **/
    SECONDARY_LINK(5134),

    /**
     * <h2>Highway Links (Sliproads / Ramps)</h2>
     * Road that connect from one road to another of the same of lower category. <br/>
     * <br/>
     * Code: 5135
     **/
    TERTIARY_LINK(5135),

    /**
     * <h2>Very Small Roads</h2>
     * Service roads for access to buildings, parking lots, etc. <br/>
     * <br/>
     * Code: 5141
     **/
    SERVICE(5141),

    /**
     * <h2>Very Small Roads</h2>
     * For agricultural use, in forests, etc. Often gravel roads. <br/>
     * <br/>
     * Code: 5142
     **/
    TRACK(5142),

    /**
     * <h2>Very Small Roads</h2>
     * For agricultural use, in forests, etc. Often gravel roads. <br/>
     * Solid. Usually a paved or sealed surface. <br/>
     * See <a href="https://wiki.openstreetmap.org/wiki/Key:tracktype">https://wiki.openstreetmap.org/wiki/Key:tracktype</a> <br/>
     * <br/>
     * Code: 5143
     **/
    TRACK_GRADE1(5143),

    /**
     * <h2>Very Small Roads</h2>
     * For agricultural use, in forests, etc. Often gravel roads. <br/>
     * Solid but unpaved. Usually an unpaved track with surface of gravel. <br/>
     * See <a href="https://wiki.openstreetmap.org/wiki/Key:tracktype">https://wiki.openstreetmap.org/wiki/Key:tracktype</a> <br/>
     * <br/>
     * Code: 5144
     **/
    TRACK_GRADE2(5144),

    /**
     * <h2>Very Small Roads</h2>
     * For agricultural use, in forests, etc. Often gravel roads. <br/>
     * Mostly solid. Even mixture of hard and soft materials. Almost always an unpaved track. <br/>
     * See <a href="https://wiki.openstreetmap.org/wiki/Key:tracktype">https://wiki.openstreetmap.org/wiki/Key:tracktype</a> <br/>
     * <br/>
     * Code: 5145
     **/
    TRACK_GRADE3(5145),

    /**
     * <h2>Very Small Roads</h2>
     * For agricultural use, in forests, etc. Often gravel roads. <br/>
     * Mostly soft. Almost always an unpaved track prominently with soil/sand/grass, but with some hard or compacted materials mixed in. <br/>
     * See <a href="https://wiki.openstreetmap.org/wiki/Key:tracktype">https://wiki.openstreetmap.org/wiki/Key:tracktype</a> <br/>
     * <br/>
     * Code: 5146
     **/
    TRACK_GRADE4(5146),

    /**
     * <h2>Very Small Roads</h2>
     * For agricultural use, in forests, etc. Often gravel roads. <br/>
     * Soft. Almost always an unimproved track lacking hard materials, same as surrounding soil. <br/>
     * See <a href="https://wiki.openstreetmap.org/wiki/Key:tracktype">https://wiki.openstreetmap.org/wiki/Key:tracktype</a> <br/>
     * <br/>
     * Code: 5147
     **/
    TRACK_GRADE5(5147),

    /**
     * <h2>Paths Unsuitable for Cars</h2>
     * Paths for horse riding <br/>
     * <br/>
     * Code: 5151
     **/
    BRIDLEWAY(5151),

    /**
     * <h2>Paths Unsuitable for Cars</h2>
     * Paths for cycling <br/>
     * <br/>
     * Code: 5152
     **/
    CYCLEWAY(5152),

    /**
     * <h2>Paths Unsuitable for Cars</h2>
     * Footpaths <br/>
     * <br/>
     * Code: 5153
     **/
    FOOTWAY(5153),

    /**
     * <h2>Paths Unsuitable for Cars</h2>
     * Unspecified paths <br/>
     * <br/>
     * Code: 5154
     **/
    PATH(5154),

    /**
     * <h2>Paths Unsuitable for Cars</h2>
     * Flights of steps on footpaths <br/>
     * <br/>
     * Code: 5155
     **/
    STEPS(5155),

    /**
     * <h2>Unknown Type Of Road Or Path</h2>
     * <br/>
     * Code: 5199
     **/
    UNKNOWN(5199);


    private final int internalCode;

    StreetType(int code) {
        this.internalCode = code;
    }

    public int code() { return internalCode; }

    public static StreetType fromCode(int code) {
        for (StreetType t : values()) {
            if (t.code() == code) {
                return t;
            }
        }
        return UNKNOWN;
    }
}
