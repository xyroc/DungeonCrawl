package xiroc.dungeoncrawl.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import xiroc.dungeoncrawl.DungeonCrawl;

public interface BuiltinModStructureSets {

    ResourceKey<StructureSet> DUNGEONS = register(DungeonCrawl.locate("dungeon"));

    private static ResourceKey<StructureSet> register(ResourceLocation key) {
        return ResourceKey.create(Registry.STRUCTURE_SET_REGISTRY, key);
    }

}
