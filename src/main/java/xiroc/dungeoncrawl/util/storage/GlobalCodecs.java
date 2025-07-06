package xiroc.dungeoncrawl.util.storage;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootTable;

public interface GlobalCodecs {
    Codec<Block> BLOCK = BuiltInRegistries.BLOCK.byNameCodec();
    Codec<Item> ITEM = BuiltInRegistries.ITEM.byNameCodec();
    Codec<Rotation> ROTATION = Codec.INT.xmap(ordinal -> Rotation.values()[ordinal], Enum::ordinal);

    Codec<BlockState> BLOCK_STATE = Codec.STRING.flatXmap(stateString -> {
        try {
            final BlockStateParser.BlockResult result = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), new StringReader(stateString), false);

            return DataResult.success(result.blockState());
        } catch (CommandSyntaxException e) {
            return DataResult.error(() -> "Invalid block state " + stateString + ": " + e.getMessage());
        }
    }, state -> {
        // Pretty much the same as BlockStateParser#serialize, except we only serialize properties with non-default values.
        final ResourceLocation key = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        final StringBuilder stateString = new StringBuilder(key.toString());

        if (!state.getProperties().isEmpty()) {
            final StringBuilder properties = new StringBuilder();
            boolean comma = false;

            properties.append('[');
            for (Property<?> property : state.getProperties()) {
                if (state.getValue(property) == state.getBlock().defaultBlockState().getValue(property)) {
                    continue; // Only serialize non-default values
                }
                if (comma) {
                    properties.append(",");
                }
                comma = true;
                appendProperty(properties, property, state.getValue(property));
            }
            properties.append(']');

            if (properties.length() > 2) {
                stateString.append(properties);
            }
        }

        return DataResult.success(stateString.toString());
    });

    Codec<ResourceKey<LootTable>> LOOT_TABLE = ResourceKey.codec(Registries.LOOT_TABLE);

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> void appendProperty(StringBuilder builder, Property<T> property, Comparable<?> value) {
        builder.append(property.getName());
        builder.append("=");
        builder.append(property.getName((T) value));
    }
}
