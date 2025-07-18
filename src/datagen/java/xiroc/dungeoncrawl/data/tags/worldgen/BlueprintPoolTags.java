package xiroc.dungeoncrawl.data.tags.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.util.random.IRandom;

import java.util.concurrent.CompletableFuture;

public class BlueprintPoolTags extends TagsProvider<IRandom<Holder<Blueprint>>> {
    public BlueprintPoolTags(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, DatapackRegistries.BLUEPRINT_POOLS, pLookupProvider, DungeonCrawl.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

    }
}
