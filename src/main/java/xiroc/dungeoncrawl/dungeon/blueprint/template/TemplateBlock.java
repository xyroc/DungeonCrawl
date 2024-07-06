package xiroc.dungeoncrawl.dungeon.blueprint.template;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;
import xiroc.dungeoncrawl.dungeon.block.MetaBlock;
import xiroc.dungeoncrawl.exception.DatapackLoadException;

import java.lang.reflect.Type;
import java.util.Locale;

public record TemplateBlock(PlacementProperties placementProperties, Vec3i position, Block block, MetaBlock properties) {
    public record PlacementProperties(TemplateBlockType blockType, boolean isSolid) {
        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;

            PlacementProperties that = (PlacementProperties) other;

            if (isSolid != that.isSolid) return false;
            return blockType == that.blockType;
        }

        public static class Serializer implements JsonSerializer<PlacementProperties>, JsonDeserializer<PlacementProperties> {
            private static final String KEY_BLOCK_TYPE = "type";
            private static final String KEY_IS_SOLID = "solid";

            private static TemplateBlockType getBlockType(String name) {
                try {
                    return TemplateBlockType.valueOf(name.toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException e) {
                    throw new DatapackLoadException("Invalid blueprint block type: " + name);
                }
            }

            @Override
            public PlacementProperties deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                JsonObject object = jsonElement.getAsJsonObject();
                TemplateBlockType blockType = getBlockType(object.get(KEY_BLOCK_TYPE).getAsString());
                boolean isSolid = object.get(KEY_IS_SOLID).getAsBoolean();
                return blockType.placementProperties(isSolid);
            }

            @Override
            public JsonElement serialize(PlacementProperties placementProperties, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonObject object = new JsonObject();
                object.addProperty(KEY_BLOCK_TYPE, placementProperties.blockType.name().toLowerCase(Locale.ROOT));
                object.addProperty(KEY_IS_SOLID, placementProperties.isSolid);
                return object;
            }
        }
    }
}