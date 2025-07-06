package xiroc.dungeoncrawl.data.spawner;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.data.JsonDataProvider;
import xiroc.dungeoncrawl.datapack.DatapackDirectories;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.util.JSONUtils;

import java.util.function.BiConsumer;

public class SpawnerTypes extends JsonDataProvider<SpawnerType> {
    public SpawnerTypes(PackOutput packOutput) {
        super(packOutput, "Spawner Types", DatapackDirectories.SPAWNER_TYPES.path(), JSONUtils.GSON::toJsonTree);
    }

    @Override
    public void collect(BiConsumer<ResourceLocation, SpawnerType> collector) {
        // TODO: add spawner types
    }
}