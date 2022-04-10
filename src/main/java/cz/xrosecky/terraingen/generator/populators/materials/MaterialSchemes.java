package cz.xrosecky.terraingen.generator.populators.materials;

import org.bukkit.Material;

import java.util.HashMap;

public class MaterialSchemes {
    private static HashMap<String, HashMap<MaterialType, Material>> materialSchemes
            = new HashMap<String, HashMap<MaterialType, Material>>() {
        {
            put("1",
                new HashMap<MaterialType, Material>() {
                    {
                        put(MaterialType.Pillar, Material.STONE_BRICKS);
                        put(MaterialType.WallPrimary, Material.BRICKS);
                        put(MaterialType.WallSecondary, Material.DEEPSLATE_BRICKS);
                        put(MaterialType.Window, Material.CYAN_STAINED_GLASS);
                    }
                });
            put("2",
                new HashMap<MaterialType, Material>() {
                    {
                        put(MaterialType.Pillar, Material.DARK_OAK_WOOD);
                        put(MaterialType.WallPrimary, Material.STRIPPED_ACACIA_WOOD);
                        put(MaterialType.WallSecondary, Material.SPRUCE_PLANKS);
                        put(MaterialType.Window, Material.GLASS_PANE);
                    }
                });
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
