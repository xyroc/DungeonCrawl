package xiroc.dungeoncrawl.data.tags.spawner;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
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

    public static final TagKey<IRandom<Holder<SpawnerEntityType>>> LEVEL_0 = key(SpawnerEntityTypePoolKeys.LEVEL_0.location());

    private static TagKey<IRandom<Holder<SpawnerEntityType>>> key(ResourceLocation identifier) {
        return TagKey.create(DatapackRegistries.SPAWNER_ENTITY_TYPE_POOLS, identifier);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(LEVEL_0)
                .add(SpawnerEntityTypePoolKeys.LEVEL_0);
    }
}
