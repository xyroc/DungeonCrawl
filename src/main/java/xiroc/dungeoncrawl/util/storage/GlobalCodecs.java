package xiroc.dungeoncrawl.util.storage;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;

public interface GlobalCodecs {
    Codec<Block> BLOCK = ResourceLocation.CODEC.xmap(Registry.BLOCK::get, Registry.BLOCK::getKey);
    Codec<Rotation> ROTATION = Codec.INT.xmap(ordinal -> Rotation.values()[ordinal], Enum::ordinal);
}
