package xiroc.dungeoncrawl.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import xiroc.dungeoncrawl.DungeonCrawl;

public interface BuiltinModStructures {

    ResourceKey<Structure> DUNGEON = createKey(DungeonCrawl.locate("dungeon"));

    private static ResourceKey<Structure> createKey(ResourceLocation key) {
        return ResourceKey.create(Registry.STRUCTURE_REGISTRY, key);
    }

}
