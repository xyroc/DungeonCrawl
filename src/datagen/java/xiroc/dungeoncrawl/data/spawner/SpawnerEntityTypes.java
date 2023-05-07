package xiroc.dungeoncrawl.data.spawner;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.data.JsonDataProvider;
import xiroc.dungeoncrawl.datapack.DatapackDirectories;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityType;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerSerializers;

import java.util.function.BiConsumer;

public class SpawnerEntityTypes extends JsonDataProvider<SpawnerEntityType> {
    public SpawnerEntityTypes(DataGenerator generator) {
        super(generator, "Spawner Entity Types", DatapackDirectories.SPAWNER_ENTITIES.path(), SpawnerSerializers.ENTITY_TYPES::toJsonTree);
    }

    @Override
    public void collect(BiConsumer<ResourceLocation, SpawnerEntityType> collector) {
        // TODO: add spawner entity types
    }
}
