package xiroc.dungeoncrawl.util.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Type;

public class ItemSerializer implements JsonSerializer<Item>, JsonDeserializer<Item> {
    @Override
    public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final ResourceLocation key = new ResourceLocation(json.getAsString());
        final Item item = ForgeRegistries.ITEMS.getValue(key);
        if (item == null) {
            throw new JsonParseException("The item " + key + "  does not exist");
        }
        return item;
    }

    @Override
    public JsonElement serialize(Item item, Type typeOfSrc, JsonSerializationContext context) {
        final ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
        if (key == null) {
            throw new JsonIOException("Could not find key for item " + item);
        }
        return new JsonPrimitive(key.toString());
    }
}
