package xiroc.dungeoncrawl.data.tags;

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
import xiroc.dungeoncrawl.data.pool.BlueprintPools;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.util.random.IRandom;

import java.util.concurrent.CompletableFuture;

public class BlueprintPoolTags extends TagsProvider<IRandom<Holder<Blueprint>>> {
    public static final TagKey<IRandom<Holder<Blueprint>>> LAB_CONTAINER = key(BlueprintPools.LAB_CONTAINER);

    public BlueprintPoolTags(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, DatapackRegistries.BLUEPRINT_POOLS, pLookupProvider, DungeonCrawl.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(LAB_CONTAINER)
                .add(BlueprintPools.LAB_CONTAINER);
    }

    private static TagKey<IRandom<Holder<Blueprint>>> key(ResourceKey<?> key) {
        return key(key.location());
    }

    private static TagKey<IRandom<Holder<Blueprint>>> key(ResourceLocation identifier) {
        return TagKey.create(DatapackRegistries.BLUEPRINT_POOLS, identifier);
    }
}
