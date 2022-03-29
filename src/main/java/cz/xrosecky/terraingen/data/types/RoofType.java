package cz.xrosecky.terraingen.data.types;

public enum RoofType {
    OTHER,

    /** No slope **/
    FLAT,

    /** Slope to only one side **/
    SHED,

    /** Round on all sides **/
    DOME,

    /** Hip and then flat **/
    MANSARD,

    /** Slope on some sides **/
    GABLE,

    /** Round on some sides **/
    VAULT,

    /** Part of a sphere (even on square building) **/
    SPHERICAL,

    /** Slope on all sides **/
    HIP
}
