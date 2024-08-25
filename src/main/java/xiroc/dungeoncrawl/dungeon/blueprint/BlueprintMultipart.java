package xiroc.dungeoncrawl.dungeon.blueprint;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.component.BlueprintComponent;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.util.random.IRandom;

import java.lang.reflect.Type;
import java.util.Random;

public record BlueprintMultipart(ResourceLocation anchorType, IRandom<Delegate<Blueprint>> blueprints) {
    public boolean addParts(DungeonPiece piece, BlueprintComponent parent, Random random) {
        var anchors = parent.blueprint().get().anchors().get(anchorType);
        if (anchors == null) {
            return true;
        }
        CoordinateSpace parentCoordinateSpace = parent.blueprint().get().coordinateSpace(parent.position());
        for (Anchor anchor : anchors) {
            anchor = parentCoordinateSpace.rotateAndTranslateToOrigin(anchor, parent.rotation());
            if (!addPart(anchor, blueprints, piece, parent, random)) {
                return false;
            }
        }
        return true;
    }

    public static boolean addPart(Anchor anchor, IRandom<Delegate<Blueprint>> parts, DungeonPiece piece, BlueprintComponent parent, Random random) {
        for (int attempt = 0; attempt < 4; ++attempt) {
            Delegate<Blueprint> part = parts.roll(random);
            if (addPart(anchor, part, piece, parent, random)) {
                return true;
            }
        }
        return false;
    }

    private static boolean addPart(Anchor anchor, Delegate<Blueprint> part, DungeonPiece piece, BlueprintComponent parent, Random random) {
        BoundingBoxBuilder parentBox = parent.boundingBox();
        Blueprint blueprint = part.get();
        var junctures = blueprint.anchors().get(BuiltinAnchorTypes.JUNCTURE);
        if (junctures == null || junctures.isEmpty()) {
            return false;
        }
        boolean horizontalAnchor = anchor.direction().getAxis().isHorizontal();
        for (int attempt = 0; attempt < junctures.size(); ++attempt) {
            Anchor juncture = junctures.get(random.nextInt(junctures.size()));
            boolean horizontalJuncture = juncture.direction().getAxis().isHorizontal();
            if (horizontalAnchor && !horizontalJuncture || !horizontalAnchor && horizontalJuncture) {
                continue;
            }
            Rotation rotation = horizontalJuncture ? Orientation.horizontalRotation(juncture.direction(), anchor.direction().getOpposite()) : parent.rotation();
            Vec3i offset = CoordinateSpace.rotate(juncture.position(), rotation, blueprint.xSpan(), blueprint.zSpan());
            BlockPos pos = anchor.position().mutable().move(anchor.direction()).move(-offset.getX(), -offset.getY(), -offset.getZ());
            BoundingBoxBuilder boundingBox = blueprint.boundingBox(rotation).move(pos);
            if (!parentBox.encapsulates(boundingBox)) {
                DungeonCrawl.LOGGER.warn("Blueprint part {} does not fit inside its parent blueprint {} when placed at anchor {}." +
                                " This should never happen and indicates a broken blueprint configuration.", part.key(), parent.blueprint().key(), juncture);
                continue;
            }
            piece.addComponent(new BlueprintComponent(part, pos, rotation));
            return true;
        }
        return false;
    }

    public static class Serializer implements JsonSerializer<BlueprintMultipart>, JsonDeserializer<BlueprintMultipart> {
        private static final String KEY_BLUEPRINTS = "blueprints";
        private static final String KEY_POSITIONS = "positions";

        @Override
        public BlueprintMultipart deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            ResourceLocation anchorType = new ResourceLocation(object.get(KEY_POSITIONS).getAsString());
            IRandom<Delegate<Blueprint>> blueprints = IRandom.BLUEPRINT.deserialize(object.get(KEY_BLUEPRINTS));
            return new BlueprintMultipart(anchorType, blueprints);
        }

        @Override
        public JsonElement serialize(BlueprintMultipart src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty(KEY_POSITIONS, src.anchorType.toString());
            object.add(KEY_BLUEPRINTS, IRandom.BLUEPRINT.serialize(src.blueprints));
            return object;
        }
    }
}
