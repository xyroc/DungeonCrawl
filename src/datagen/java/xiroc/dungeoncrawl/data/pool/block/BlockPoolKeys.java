package xiroc.dungeoncrawl.data.pool.block;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import xiroc.dungeoncrawl.data.DataGen;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface BlockPoolKeys {
    ResourceLocation __CROPS = DataGen.resource("crops");

    ResourceKey<IRandom<BlockState>> FARMLAND_CROPS = key(DataGen.resource(__CROPS,"farmland"));

    private static ResourceKey<IRandom<BlockState>> key(ResourceLocation identifier) {
        return ResourceKey.create(DatapackRegistries.BLOCK_STATE_POOLS, identifier);
    }
}
