package xiroc.dungeoncrawl.data.pool.spawner;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import xiroc.dungeoncrawl.data.SharedKeys;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityType;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface SpawnerEntityTypePools {
    static void generate(BootstrapContext<IRandom<Holder<SpawnerEntityType>>> context) {
        var spawnerEntities = context.lookup(DatapackRegistries.SPAWNER_ENTITY_TYPE);

        context.register(SpawnerEntityTypePoolKeys.LEVEL_0, IRandom.<Holder<SpawnerEntityType>>builder()
                .add(spawnerEntities.getOrThrow(SharedKeys.Spawner.EntityType.ZOMBIE))
                .build());
    }
}
