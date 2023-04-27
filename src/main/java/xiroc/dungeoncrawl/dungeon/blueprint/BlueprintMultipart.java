package xiroc.dungeoncrawl.dungeon.blueprint;

import com.google.gson.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import xiroc.dungeoncrawl.datapack.delegate.Delegate;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.AnchorProvider;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.random.ArrayUrn;
import xiroc.dungeoncrawl.util.random.WeightedRandom;

import java.lang.reflect.Type;
import java.util.Random;
import java.util.function.Consumer;

public class BlueprintMultipart {
    private final WeightedRandom<Delegate<Blueprint>> blueprints;
    private final ResourceLocation anchorType;
    private ArrayUrn<Anchor> anchors;

    public BlueprintMultipart(WeightedRandom<Delegate<Blueprint>> blueprints, ResourceLocation anchorType) {
        this.blueprints = blueprints;
        this.anchorType = anchorType;
    }

    public void resolvePool(AnchorProvider provider, Consumer<String> errorHandler) {
        ArrayUrn<Anchor> urn = provider.anchors(anchorType);
        if (urn == null) {
            errorHandler.accept("No anchors of type " + anchorType.toString() + " found");
        }
        anchors = urn;
    }

    public void createPieces(Blueprint parentBlueprint, BlockPos parentPosition, Rotation parentRotation, Random random, Consumer<DungeonPiece> pieceConsumer) {
        anchors.forEachExisting((anchor) -> {

        });
    }

    public static class Serializer implements JsonSerializer<BlueprintMultipart>, JsonDeserializer<BlueprintMultipart> {
        private static final String KEY_BLUEPRINTS = "blueprints";
        private static final String KEY_POSITIONS = "positions";

        @Override
        public BlueprintMultipart deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            WeightedRandom<Delegate<Blueprint>> blueprints = WeightedRandom.BLUEPRINT.deserialize(object.get(KEY_BLUEPRINTS));
            ResourceLocation anchorType = new ResourceLocation(object.get(KEY_POSITIONS).getAsString());
            return new BlueprintMultipart(blueprints, anchorType);
        }

        @Override
        public JsonElement serialize(BlueprintMultipart src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.add(KEY_BLUEPRINTS, WeightedRandom.BLUEPRINT.serialize(src.blueprints));
            object.addProperty(KEY_POSITIONS, src.anchorType.toString());
            return object;
        }
    }
}
