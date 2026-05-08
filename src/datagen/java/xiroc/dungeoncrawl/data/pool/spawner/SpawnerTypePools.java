package xiroc.dungeoncrawl.data.pool.spawner;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import xiroc.dungeoncrawl.data.spawner.SpawnerTypeKeys;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface SpawnerTypePools {
    static void generate(BootstrapContext<IRandom<Holder<SpawnerType>>> context) {
        var spawnerTypes = context.lookup(DatapackRegistries.SPAWNER_TYPE);

        context.register(SpawnerTypePoolKeys.LEVEL_O, IRandom.<Holder<SpawnerType>>builder()
                .add(spawnerTypes.getOrThrow(SpawnerTypeKeys.LEVEL_0))
                .build());

        context.register(SpawnerTypePoolKeys.LEVEL_1, IRandom.<Holder<SpawnerType>>builder()
                .add(spawnerTypes.getOrThrow(SpawnerTypeKeys.LEVEL_1))
                .build());

        context.register(SpawnerTypePoolKeys.LEVEL_2, IRandom.<Holder<SpawnerType>>builder()
                .add(spawnerTypes.getOrThrow(SpawnerTypeKeys.LEVEL_2))
                .build());

        context.register(SpawnerTypePoolKeys.LEVEL_3, IRandom.<Holder<SpawnerType>>builder()
                .add(spawnerTypes.getOrThrow(SpawnerTypeKeys.LEVEL_3))
                .build());

        context.register(SpawnerTypePoolKeys.LEVEL_4, IRandom.<Holder<SpawnerType>>builder()
                .add(spawnerTypes.getOrThrow(SpawnerTypeKeys.LEVEL_4))
                .build());
    }
}
