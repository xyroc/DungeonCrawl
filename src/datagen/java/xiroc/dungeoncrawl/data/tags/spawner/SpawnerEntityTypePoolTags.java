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
import xiroc.dungeoncrawl.data.pool.spawner.SpawnerEntityTypePoolKeys;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityType;
import xiroc.dungeoncrawl.util.random.IRandom;

import java.util.concurrent.CompletableFuture;

public class SpawnerEntityTypePoolTags extends TagsProvider<IRandom<Holder<SpawnerEntityType>>> {
    public SpawnerEntityTypePoolTags(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, DatapackRegistries.SPAWNER_ENTITY_TYPE_POOLS, pLookupProvider, DungeonCrawl.MOD_ID, existingFileHelper);
    }

    public static final TagKey<IRandom<Holder<SpawnerEntityType>>> LEVEL_0 = key(SpawnerEntityTypePoolKeys.LEVEL_0);
    public static final TagKey<IRandom<Holder<SpawnerEntityType>>> LEVEL_1 = key(SpawnerEntityTypePoolKeys.LEVEL_1);
    public static final TagKey<IRandom<Holder<SpawnerEntityType>>> LEVEL_2 = key(SpawnerEntityTypePoolKeys.LEVEL_2);
    public static final TagKey<IRandom<Holder<SpawnerEntityType>>> LEVEL_3 = key(SpawnerEntityTypePoolKeys.LEVEL_3);
    public static final TagKey<IRandom<Holder<SpawnerEntityType>>> LEVEL_4 = key(SpawnerEntityTypePoolKeys.LEVEL_4);

    public static final TagKey<IRandom<Holder<SpawnerEntityType>>> CAVE_SPIDER_LEVEL_0 = key(SpawnerEntityTypePoolKeys.CAVE_SPIDER_LEVEL_0);
    public static final TagKey<IRandom<Holder<SpawnerEntityType>>> CAVE_SPIDER_LEVEL_1 = key(SpawnerEntityTypePoolKeys.CAVE_SPIDER_LEVEL_1);
    public static final TagKey<IRandom<Holder<SpawnerEntityType>>> CAVE_SPIDER_LEVEL_2 = key(SpawnerEntityTypePoolKeys.CAVE_SPIDER_LEVEL_2);
    public static final TagKey<IRandom<Holder<SpawnerEntityType>>> CAVE_SPIDER_LEVEL_3 = key(SpawnerEntityTypePoolKeys.CAVE_SPIDER_LEVEL_3);
    public static final TagKey<IRandom<Holder<SpawnerEntityType>>> CAVE_SPIDER_LEVEL_4 = key(SpawnerEntityTypePoolKeys.CAVE_SPIDER_LEVEL_4);

    private static TagKey<IRandom<Holder<SpawnerEntityType>>> key(ResourceKey<?> key) {
        return key(key.location());
    }

    private static TagKey<IRandom<Holder<SpawnerEntityType>>> key(ResourceLocation identifier) {
        return TagKey.create(DatapackRegistries.SPAWNER_ENTITY_TYPE_POOLS, identifier);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(LEVEL_0)
                .add(SpawnerEntityTypePoolKeys.LEVEL_0);
        tag(LEVEL_1)
                .add(SpawnerEntityTypePoolKeys.LEVEL_1);
        tag(LEVEL_2)
                .add(SpawnerEntityTypePoolKeys.LEVEL_2);
        tag(LEVEL_3)
                .add(SpawnerEntityTypePoolKeys.LEVEL_3);
        tag(LEVEL_4)
                .add(SpawnerEntityTypePoolKeys.LEVEL_4);

        tag(CAVE_SPIDER_LEVEL_0)
                .add(SpawnerEntityTypePoolKeys.CAVE_SPIDER_LEVEL_0);
        tag(CAVE_SPIDER_LEVEL_1)
                .add(SpawnerEntityTypePoolKeys.CAVE_SPIDER_LEVEL_1);
        tag(CAVE_SPIDER_LEVEL_2)
                .add(SpawnerEntityTypePoolKeys.CAVE_SPIDER_LEVEL_2);
        tag(CAVE_SPIDER_LEVEL_3)
                .add(SpawnerEntityTypePoolKeys.CAVE_SPIDER_LEVEL_3);
        tag(CAVE_SPIDER_LEVEL_4)
                .add(SpawnerEntityTypePoolKeys.CAVE_SPIDER_LEVEL_4);
    }
}
