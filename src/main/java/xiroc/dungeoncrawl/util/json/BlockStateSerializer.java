package xiroc.dungeoncrawl.util.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Type;

public class BlockStateSerializer implements JsonSerializer<BlockState>, JsonDeserializer<BlockState> {
    @Override
    public BlockState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final String stateString = json.getAsString();
        final BlockStateParser parser = new BlockStateParser(new StringReader(stateString), false);
        try {
            parser.parse(false);
            if (parser.getState() == null) {
                throw new JsonParseException("Invalid block state: " + stateString);
            }
            return parser.getState();
        } catch (CommandSyntaxException e) {
            throw new JsonParseException("Invalid block state " + stateString + ": " +  e.getMessage());
        }
    }

    @Override
    public JsonElement serialize(BlockState state, Type typeOfSrc, JsonSerializationContext context) {
        final ResourceLocation key = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        if (key == null) {
            throw new JsonIOException("Could not find key for block " + state.getBlock());
        }
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
                serializeProperty(properties, property, state.getValue(property));

            }
            properties.append(']');

            if (properties.length() > 2) {
                stateString.append(properties);
            }
        }

        return new JsonPrimitive(stateString.toString());
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> void serializeProperty(StringBuilder builder, Property<T> property, Comparable<?> value) {
        builder.append(property.getName());
        builder.append("=");
        builder.append(property.getName((T) value));
    }
}
