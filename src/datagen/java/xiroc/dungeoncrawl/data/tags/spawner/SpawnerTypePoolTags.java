package xiroc.dungeoncrawl.data.tags.spawner;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.data.pool.spawner.SpawnerTypePoolKeys;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.util.random.IRandom;

import java.util.concurrent.CompletableFuture;

public class SpawnerTypePoolTags extends TagsProvider<IRandom<Holder<SpawnerType>>> {
    public SpawnerTypePoolTags(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, DatapackRegistries.SPAWNER_TYPE_POOLS, pLookupProvider, DungeonCrawl.MOD_ID, existingFileHelper);
    }

    public static final TagKey<IRandom<Holder<SpawnerType>>> LEVEL_O = key(SpawnerTypePoolKeys.LEVEL_O);
    public static final TagKey<IRandom<Holder<SpawnerType>>> LEVEL_1 = key(SpawnerTypePoolKeys.LEVEL_1);
    public static final TagKey<IRandom<Holder<SpawnerType>>> LEVEL_2 = key(SpawnerTypePoolKeys.LEVEL_2);
    public static final TagKey<IRandom<Holder<SpawnerType>>> LEVEL_3 = key(SpawnerTypePoolKeys.LEVEL_3);
    public static final TagKey<IRandom<Holder<SpawnerType>>> LEVEL_4 = key(SpawnerTypePoolKeys.LEVEL_4);

    private static TagKey<IRandom<Holder<SpawnerType>>> key(ResourceKey<?> key) {
        return key(key.location());
    }

    private static TagKey<IRandom<Holder<SpawnerType>>> key(ResourceLocation identifier) {
        return TagKey.create(DatapackRegistries.SPAWNER_TYPE_POOLS, identifier);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(LEVEL_O)
                .add(SpawnerTypePoolKeys.LEVEL_O);
        tag(LEVEL_1)
                .add(SpawnerTypePoolKeys.LEVEL_1);
        tag(LEVEL_2)
                .add(SpawnerTypePoolKeys.LEVEL_2);
        tag(LEVEL_3)
                .add(SpawnerTypePoolKeys.LEVEL_3);
        tag(LEVEL_4)
                .add(SpawnerTypePoolKeys.LEVEL_4);
    }
}
