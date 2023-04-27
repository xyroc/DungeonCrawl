package xiroc.dungeoncrawl.dungeon.blueprint.feature.type.instance;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.datapack.delegate.Delegate;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerTypes;

public class SpawnerInstance extends BaseFeatureInstance {
    private static final String NBT_KEY_SPAWNER_TYPE = "SpawnerType";

    public final Delegate<SpawnerType> spawnerType;

    public SpawnerInstance(BlockPos position, Delegate<SpawnerType> spawnerType) {
        super(position);
        this.spawnerType = spawnerType;
    }

    public SpawnerInstance(CompoundTag nbt) {
        super(nbt);
        ResourceLocation spawnerTypeKey = new ResourceLocation(nbt.getString(NBT_KEY_SPAWNER_TYPE));
        SpawnerType spawnerType = SpawnerTypes.get(spawnerTypeKey);
        if (spawnerType == null) {
            DungeonCrawl.LOGGER.warn("Spawner type '" + spawnerTypeKey + "' does not exist. Did the data pack change?");
        }
        this.spawnerType = Delegate.of(spawnerType, spawnerTypeKey);
    }

    @Override
    public void write(CompoundTag nbt) {
        super.write(nbt);
        nbt.putString(NBT_KEY_SPAWNER_TYPE, spawnerType.key().toString());
    }
}
