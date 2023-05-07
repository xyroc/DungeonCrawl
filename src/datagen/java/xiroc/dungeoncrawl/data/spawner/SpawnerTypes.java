package xiroc.dungeoncrawl.data.spawner;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.data.JsonDataProvider;
import xiroc.dungeoncrawl.datapack.DatapackDirectories;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerSerializers;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;

import java.util.function.BiConsumer;

public class SpawnerTypes extends JsonDataProvider<SpawnerType> {
    public SpawnerTypes(DataGenerator generator) {
        super(generator, "Spawner Types", DatapackDirectories.SPAWNER_TYPES.path(), SpawnerSerializers.SPAWNER_TYPES::toJsonTree);
    }

    @Override
    public void collect(BiConsumer<ResourceLocation, SpawnerType> collector) {
        // TODO: add spawner types
    }
}