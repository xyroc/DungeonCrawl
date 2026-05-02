package xiroc.dungeoncrawl.data.pool.spawner;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import xiroc.dungeoncrawl.data.spawner.SpawnerEntityTypeKeys;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityType;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface SpawnerEntityTypePools {
    static void generate(BootstrapContext<IRandom<Holder<SpawnerEntityType>>> context) {
        var spawnerEntities = context.lookup(DatapackRegistries.SPAWNER_ENTITY_TYPE);

        context.register(SpawnerEntityTypePoolKeys.LEVEL_0, IRandom.<Holder<SpawnerEntityType>>builder()
                .add(spawnerEntities.getOrThrow(SpawnerEntityTypeKeys.ZOMBIE))
                .build());

        context.register(SpawnerEntityTypePoolKeys.CAVE_SPIDER_LEVEL_0, IRandom.<Holder<SpawnerEntityType>>builder()
                .add(spawnerEntities.getOrThrow(SpawnerEntityTypeKeys.CAVE_SPIDER_LEVEL_0))
                .build());

        context.register(SpawnerEntityTypePoolKeys.CAVE_SPIDER_LEVEL_1, IRandom.<Holder<SpawnerEntityType>>builder()
                .add(spawnerEntities.getOrThrow(SpawnerEntityTypeKeys.CAVE_SPIDER_LEVEL_1))
                .build());

        context.register(SpawnerEntityTypePoolKeys.CAVE_SPIDER_LEVEL_2, IRandom.<Holder<SpawnerEntityType>>builder()
                .add(spawnerEntities.getOrThrow(SpawnerEntityTypeKeys.CAVE_SPIDER_LEVEL_2))
                .build());

        context.register(SpawnerEntityTypePoolKeys.CAVE_SPIDER_LEVEL_3, IRandom.<Holder<SpawnerEntityType>>builder()
                .add(spawnerEntities.getOrThrow(SpawnerEntityTypeKeys.CAVE_SPIDER_LEVEL_3))
                .build());

        context.register(SpawnerEntityTypePoolKeys.CAVE_SPIDER_LEVEL_4, IRandom.<Holder<SpawnerEntityType>>builder()
                .add(spawnerEntities.getOrThrow(SpawnerEntityTypeKeys.CAVE_SPIDER_LEVEL_4))
                .build());
    }
}
