package xiroc.dungeoncrawl.dungeon.theme;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import xiroc.dungeoncrawl.datapack.DatapackDirectories;
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.util.random.RandomMapping;
import xiroc.dungeoncrawl.util.random.WeightedRandom;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.function.Function;

public class Themes {
    private static ImmutableMap<ResourceLocation, PrimaryTheme> PRIMARY_THEMES;
    private static ImmutableMap<ResourceLocation, SecondaryTheme> SECONDARY_THEMES;

    private static RandomMapping<String, PrimaryTheme> PRIMARY_THEME_MAPPING;
    private static RandomMapping<String, SecondaryTheme> SECONDARY_THEME_MAPPING;

    public static PrimaryTheme getPrimary(ResourceLocation key) {
        return PRIMARY_THEMES.getOrDefault(key, BuiltinThemes.DEFAULT_PRIMARY);
    }

    public static SecondaryTheme getSecondary(ResourceLocation key) {
        return SECONDARY_THEMES.getOrDefault(key, BuiltinThemes.DEFAULT_SECONDARY);
    }

    public static PrimaryTheme rollPrimary(String biome, Random random) {
        return PRIMARY_THEME_MAPPING.roll(biome, random);
    }

    public static SecondaryTheme rollSecondary(String biome, Random random) {
        return SECONDARY_THEME_MAPPING.roll(biome, random);
    }

    public static void load(ResourceManager resourceManager) {
        loadThemes(resourceManager);
        loadMappings(resourceManager);
    }

    private static void loadThemes(ResourceManager resourceManager) {
        ImmutableMap.Builder<ResourceLocation, PrimaryTheme> primaryThemes = ImmutableMap.builder();
        ImmutableMap.Builder<ResourceLocation, SecondaryTheme> secondaryThemes = ImmutableMap.builder();
        BuiltinThemes.register(primaryThemes::put, secondaryThemes::put);

        resourceManager.listResources(DatapackDirectories.PRIMARY_THEMES.path(), (path) -> path.endsWith(".json"))
                .forEach((file) -> {
                    ResourceLocation key = DatapackDirectories.PRIMARY_THEMES.key(file, ".json");
                    try {
                        JsonElement json = JsonParser.parseReader(new InputStreamReader(resourceManager.getResource(file).getInputStream()));
                        primaryThemes.put(key, PrimaryTheme.deserialize(key, json));
                    } catch (IOException e) {
                        throw new DatapackLoadException("Failed to load " + file + ": " + e.getMessage());
                    }
                });

        resourceManager.listResources(DatapackDirectories.SECONDARY_THEMES.path(), (path) -> path.endsWith(".json"))
                .forEach((file) -> {
                    ResourceLocation key = DatapackDirectories.SECONDARY_THEMES.key(file, ".json");
                    try {
                        JsonElement json = JsonParser.parseReader(new InputStreamReader(resourceManager.getResource(file).getInputStream()));
                        secondaryThemes.put(key, SecondaryTheme.deserialize(key, json));
                    } catch (IOException e) {
                        throw new DatapackLoadException("Failed to load " + file + ": " + e.getMessage());
                    }
                });

        PRIMARY_THEMES = primaryThemes.build();
        SECONDARY_THEMES = secondaryThemes.build();
    }

    private static void loadMappings(ResourceManager resourceManager) {
        RandomMapping.Builder<String, PrimaryTheme> primaryThemes = new RandomMapping.Builder<>();
        RandomMapping.Builder<String, SecondaryTheme> secondaryThemes = new RandomMapping.Builder<>();

        resourceManager.listResources(DatapackDirectories.PRIMARY_THEME_MAPPINGS.path(), (path) -> path.endsWith(".json"))
                .forEach((file) -> {
                    try {
                        JsonElement json = JsonParser.parseReader(new InputStreamReader(resourceManager.getResource(file).getInputStream()));
                        primaryThemes.deserialize(json, WeightedRandom.PRIMARY_THEME, Function.identity());
                    } catch (IOException e) {
                        throw new DatapackLoadException("Failed to load " + file + ": " + e.getMessage());
                    }
                });

        resourceManager.listResources(DatapackDirectories.SECONDARY_THEME_MAPPINGS.path(), (path) -> path.endsWith(".json"))
                .forEach((file) -> {
                    try {
                        JsonElement json = JsonParser.parseReader(new InputStreamReader(resourceManager.getResource(file).getInputStream()));
                        secondaryThemes.deserialize(json, WeightedRandom.SECONDARY_THEME, Function.identity());
                    } catch (IOException e) {
                        throw new DatapackLoadException("Failed to load " + file + ": " + e.getMessage());
                    }
                });

        PRIMARY_THEME_MAPPING = primaryThemes.build();
        SECONDARY_THEME_MAPPING = secondaryThemes.build();

        PRIMARY_THEME_MAPPING.validate((message) -> {
            throw new DatapackLoadException("Invalid primary theme mapping: " + message);
        });

        SECONDARY_THEME_MAPPING.validate((message) -> {
            throw new DatapackLoadException("Invalid secondary theme mapping: " + message);
        });
    }
}