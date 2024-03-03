package xiroc.dungeoncrawl.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.List;

public interface StorageHelper {
    Codec<Block> BLOCK_CODEC = ResourceLocation.CODEC.xmap(Registry.BLOCK::get, Registry.BLOCK::getKey);
    Codec<List<BlockPos>> BLOCK_POS_LIST_CODEC = Codec.list(BlockPos.CODEC);

    static <T> Tag encode(T value, Codec<T> codec) {
        return codec.encodeStart(NbtOps.INSTANCE, value).result().orElseThrow();
    }

    static <T> T decode(Tag tag, Codec<T> codec) {
        return codec.decode(NbtOps.INSTANCE, tag).result().map(Pair::getFirst).orElseThrow();
    }
}
