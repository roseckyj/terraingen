package cz.xrosecky.terraingen.generator.populators.materials;

import org.bukkit.Material;

import java.util.HashMap;

public class MaterialSchemes {
    private static HashMap<String, HashMap<MaterialType, Material>> materialSchemes
            = new HashMap<String, HashMap<MaterialType, Material>>() {
        {
            put("quartz",
                new HashMap<MaterialType, Material>() {
                    {
                        put(MaterialType.Pillar, Material.QUARTZ_PILLAR);
                        put(MaterialType.WallPrimary, Material.QUARTZ_PILLAR);
                        put(MaterialType.WallSecondary, Material.WHITE_CONCRETE);
                        put(MaterialType.Window, Material.WHITE_STAINED_GLASS);
                        put(MaterialType.Under, Material.POLISHED_BLACKSTONE_BRICKS);
                    }
                });
            put("light gray",
                new HashMap<MaterialType, Material>() {
                    {
                        put(MaterialType.Pillar, Material.LIGHT_GRAY_CONCRETE);
                        put(MaterialType.WallPrimary, Material.LIGHT_GRAY_CONCRETE);
                        put(MaterialType.WallSecondary, Material.LIGHT_GRAY_CONCRETE);
                        put(MaterialType.Window, Material.GLASS);
                        put(MaterialType.Under, Material.BLACK_CONCRETE);
                    }
                });
            put("2",
                new HashMap<MaterialType, Material>() {
                    {
                        put(MaterialType.Pillar, Material.DEEPSLATE_BRICKS);
                        put(MaterialType.WallPrimary, Material.DEEPSLATE_BRICKS);
                        put(MaterialType.WallSecondary, Material.DEEPSLATE_BRICKS);
                        put(MaterialType.Window, Material.GLASS);
                        put(MaterialType.Under, Material.COBBLED_DEEPSLATE);
                    }
                });
            put("3",
                new HashMap<MaterialType, Material>() {
                    {
                        put(MaterialType.Pillar, Material.BONE_BLOCK);
                        put(MaterialType.WallPrimary, Material.BONE_BLOCK);
                        put(MaterialType.WallSecondary, Material.WHITE_TERRACOTTA);
                        put(MaterialType.Window, Material.GLASS);
                        put(MaterialType.Under, Material.COBBLED_DEEPSLATE);
                    }
                });
            put("4",
                new HashMap<MaterialType, Material>() {
                    {
                        put(MaterialType.Pillar, Material.BLUE_TERRACOTTA);
                        put(MaterialType.WallPrimary, Material.BLUE_TERRACOTTA);
                        put(MaterialType.WallSecondary, Material.CYAN_TERRACOTTA);
                        put(MaterialType.Window, Material.WHITE_STAINED_GLASS);
                        put(MaterialType.Under, Material.COBBLED_DEEPSLATE);
                    }
                });
            put("5",
                    new HashMap<MaterialType, Material>() {
                        {
                            put(MaterialType.Pillar, Material.ACACIA_LOG);
                            put(MaterialType.WallPrimary, Material.ACACIA_LOG);
                            put(MaterialType.WallSecondary, Material.QUARTZ_BRICKS);
                            put(MaterialType.Window, Material.WHITE_STAINED_GLASS);
                            put(MaterialType.Under, Material.COBBLED_DEEPSLATE);
                        }
                    });
            put("6",
                    new HashMap<MaterialType, Material>() {
                        {
                            put(MaterialType.Pillar, Material.WHITE_CONCRETE);
                            put(MaterialType.WallPrimary, Material.WHITE_CONCRETE);
                            put(MaterialType.WallSecondary, Material.WHITE_TERRACOTTA);
                            put(MaterialType.Window, Material.GLASS);
                            put(MaterialType.Under, Material.COBBLED_DEEPSLATE);
                        }
                    });

            // Chisseled quartz block
            // Mushroom stem
        }
    };

    /**
     *
     * @param type
     * @param scheme
     * @return Golden if no material or scheme is found
     */
    public static Material getMaterial(String scheme, MaterialType type) {
        HashMap<MaterialType, Material> s = materialSchemes.get(scheme);
        if (s != null) {
            Material m = s.get(type);
            if (m != null) return m;
        }
        return Material.GOLD_BLOCK;
    }

    public static String[] getSchemes(){
        return materialSchemes.keySet().toArray(new String[0]);
    }
}
