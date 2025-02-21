package xiroc.dungeoncrawl.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;

public interface StorageHelper {
    Codec<Block> BLOCK_CODEC = ResourceLocation.CODEC.xmap(Registry.BLOCK::get, Registry.BLOCK::getKey);
    Codec<Rotation> ROTATION_CODEC = Codec.INT.xmap(ordinal -> Rotation.values()[ordinal], Enum::ordinal);

    static <T> Tag encode(T value, Codec<T> codec) {
        return codec.encodeStart(NbtOps.INSTANCE, value).result().orElseThrow();
    }

    static <T> T decode(Tag tag, Codec<T> codec) {
        return codec.decode(NbtOps.INSTANCE, tag).result().map(Pair::getFirst).orElseThrow();
    }
}
