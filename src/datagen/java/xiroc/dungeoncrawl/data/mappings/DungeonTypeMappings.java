package xiroc.dungeoncrawl.data.mappings;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.Biome;
import xiroc.dungeoncrawl.data.SharedKeys;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.type.DungeonType;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.RandomMapping;

public interface DungeonTypeMappings {
    static void generate(BootstrapContext<RandomMapping<Biome, DungeonType>> context) {
        var dungeonTypes = context.lookup(DatapackRegistries.DUNGEON_TYPE);

        context.register(SharedKeys.DungeonTypeMappings.DEFAULT, new RandomMapping.Builder<Biome, DungeonType>()
                .fallback(new IRandom.Builder<Holder<DungeonType>>()
                        .add(dungeonTypes.getOrThrow(SharedKeys.Dungeon.DEFAULT))
                        .build())
                .build());
    }
}
