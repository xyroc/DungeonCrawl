package xiroc.dungeoncrawl.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.data.pool.block.BlockPoolKeys;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.util.random.IRandom;

import java.util.concurrent.CompletableFuture;

public class BlockPoolTags extends TagsProvider<IRandom<BlockState>> {
    public BlockPoolTags(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, DatapackRegistries.BLOCK_STATE_POOLS, pLookupProvider, DungeonCrawl.MOD_ID, existingFileHelper);
    }

    private static TagKey<IRandom<BlockState>> key(ResourceLocation identifier) {
        return TagKey.create(DatapackRegistries.BLOCK_STATE_POOLS, identifier);
    }

    public static final TagKey<IRandom<BlockState>> FARMLAND_CROPS = key(BlockPoolKeys.FARMLAND_CROPS.location());

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(FARMLAND_CROPS)
                .add(BlockPoolKeys.FARMLAND_CROPS);
    }
}
