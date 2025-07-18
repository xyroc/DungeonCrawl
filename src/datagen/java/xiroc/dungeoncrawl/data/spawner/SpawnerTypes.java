package xiroc.dungeoncrawl.data.spawner;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import xiroc.dungeoncrawl.data.SharedKeys;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityType;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.value.Range;

public interface SpawnerTypes {
    static void generate(BootstrapContext<SpawnerType> context) {
        var spawnerEntityTypes = context.lookup(DatapackRegistries.SPAWNER_ENTITY_TYPE);

        context.register(SharedKeys.Spawner.Type.DEFAULT, new SpawnerType.Builder()
                        .entities(IRandom.<Holder<SpawnerEntityType>>builder()
                                .add(spawnerEntityTypes.getOrThrow(SharedKeys.Spawner.EntityType.ZOMBIE)))
                        .spawnAmount(new Range(1, 2))
                        .spawnDelay(new Range(100, 200))
                .build());
    }
}