package xiroc.dungeoncrawl.dungeon.theme;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.server.packs.resources.ResourceManager;
import xiroc.dungeoncrawl.datapack.DatapackDirectories;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.RandomMapping;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.function.Function;

public class Themes {
    public static final Gson GSON = gsonAdapters(new GsonBuilder()).create();

    public static GsonBuilder gsonAdapters(GsonBuilder builder) {
        return BlockStateProvider.gsonAdapters(builder)
                .registerTypeAdapter(PrimaryTheme.class, new PrimaryTheme.Serializer())
                .registerTypeAdapter(SecondaryTheme.class, new SecondaryTheme.Serializer());
    }

    private static RandomMapping<String, PrimaryTheme> PRIMARY_THEME_MAPPING;
    private static RandomMapping<String, SecondaryTheme> SECONDARY_THEME_MAPPING;

    public static PrimaryTheme rollPrimary(String biome, Random random) {
        return PRIMARY_THEME_MAPPING.roll(biome, random);
    }

    public static SecondaryTheme rollSecondary(String biome, Random random) {
        return SECONDARY_THEME_MAPPING.roll(biome, random);
    }

    public static void load(ResourceManager resourceManager) {
        loadMappings(resourceManager);
    }

    private static void loadMappings(ResourceManager resourceManager) {
        RandomMapping.Builder<String, PrimaryTheme> primaryThemes = new RandomMapping.Builder<>();
        RandomMapping.Builder<String, SecondaryTheme> secondaryThemes = new RandomMapping.Builder<>();

        resourceManager.listResources(DatapackDirectories.PRIMARY_THEME_MAPPINGS.path(), (path) -> path.endsWith(".json"))
                .forEach((file) -> {
                    try {
                        JsonElement json = JsonParser.parseReader(new InputStreamReader(resourceManager.getResource(file).getInputStream()));
                        primaryThemes.deserialize(json, IRandom.PRIMARY_THEME, Function.identity());
                    } catch (IOException e) {
                        throw new DatapackLoadException("Failed to load " + file + ": " + e.getMessage());
                    }
                });

        resourceManager.listResources(DatapackDirectories.SECONDARY_THEME_MAPPINGS.path(), (path) -> path.endsWith(".json"))
                .forEach((file) -> {
                    try {
                        JsonElement json = JsonParser.parseReader(new InputStreamReader(resourceManager.getResource(file).getInputStream()));
                        secondaryThemes.deserialize(json, IRandom.SECONDARY_THEME, Function.identity());
                    } catch (IOException e) {
                        throw new DatapackLoadException("Failed to load " + file + ": " + e.getMessage());
                    }
                });

        primaryThemes.fallback(BuiltinThemes.DEFAULT, 1);
        secondaryThemes.fallback(BuiltinThemes.DEFAULT, 1);

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