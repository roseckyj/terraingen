package cz.xrosecky.terraingen.generator.utils;

import org.bukkit.Material;

import java.util.Dictionary;
import java.util.Random;

public class RandomMaterial {
    private Material[] materials;
    private Random random;
    public final double radius;
    private double probability;

    public RandomMaterial(Material[] materials, Random random, double radius, double probability) {
        this.materials = materials;
        this.random = random;
        this.radius = radius;
        this.probability = probability;
    }

    public Material getMaterial(double distance) {
        if (random.nextDouble() < probability / (radius * 2 + 1)) {
            int rnd = random.nextInt(materials.length);
            return materials[rnd];
        }
        return null;
    }
}
