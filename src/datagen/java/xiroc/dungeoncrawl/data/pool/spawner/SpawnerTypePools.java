package xiroc.dungeoncrawl.data.pool.spawner;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import xiroc.dungeoncrawl.data.SharedKeys;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface SpawnerTypePools {
    static void generate(BootstrapContext<IRandom<Holder<SpawnerType>>> context) {
        var spawnerTypes = context.lookup(DatapackRegistries.SPAWNER_TYPE);

        context.register(SpawnerTypePoolKeys.LEVEL_O, IRandom.<Holder<SpawnerType>>builder()
                .add(spawnerTypes.getOrThrow(SharedKeys.Spawner.Type.DEFAULT))
                .build());
    }
}
