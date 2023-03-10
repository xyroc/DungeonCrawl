package xiroc.dungeoncrawl.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import xiroc.dungeoncrawl.DungeonCrawl;

public class ModTags {

    public static final TagKey<Biome> HAS_DUNGEON = create("has_structure/dungeon");

    private static TagKey<Biome> create(String path) {
        return TagKey.create(Registries.BIOME, DungeonCrawl.locate(path));
    }

}
