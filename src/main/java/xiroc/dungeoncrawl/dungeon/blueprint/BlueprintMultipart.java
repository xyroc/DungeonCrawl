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
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.generator.plan.DungeonPlan;
import xiroc.dungeoncrawl.dungeon.piece.CompoundPiece;
import xiroc.dungeoncrawl.dungeon.piece.Segment;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.util.random.IRandom;

import java.lang.reflect.Type;
import java.util.Random;

public record BlueprintMultipart(ResourceLocation anchorType, IRandom<ResourceLocation> blueprints) {
    public boolean addParts(DungeonPlan plan, CompoundPiece parent, Random random) {
        var anchors = parent.blueprint.anchors().get(anchorType);
        if (anchors == null) {
            return true;
        }
        CoordinateSpace parentCoordinateSpace = parent.blueprint.coordinateSpace(parent.position);
        main:
        for (Anchor anchor : anchors) {
            anchor = parentCoordinateSpace.rotateAndTranslateToOrigin(anchor, parent.rotation);
            for (int attempt = 0; attempt < 4; ++attempt) {
                Blueprint part = Blueprints.getBlueprint(blueprints.roll(random));
                if (addPart(plan, parent, part, anchor, random)) {
                    continue main;
                }
            }
            return false;
        }
        return true;
    }

    private boolean addPart(DungeonPlan plan, CompoundPiece parent, Blueprint part, Anchor anchor, Random random) {
        var junctures = part.anchors().get(BuiltinAnchorTypes.JUNCTURE);
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
            Rotation rotation = horizontalJuncture ? Orientation.horizontalRotation(juncture.direction(), anchor.direction().getOpposite()) : parent.rotation;
            Vec3i offset = CoordinateSpace.rotate(juncture.position(), rotation, part.xSpan(), part.zSpan());
            BlockPos pos = anchor.position().mutable().move(anchor.direction()).move(-offset.getX(), -offset.getY(), -offset.getZ());
            BoundingBoxBuilder boundingBox = part.boundingBox(rotation);
            boundingBox.move(pos);
            if (!plan.isFree(boundingBox)) {
                continue;
            }
            parent.addSegment(new Segment(part, pos, rotation));
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
            IRandom<ResourceLocation> blueprints = IRandom.IDENTIFIER.deserialize(object.get(KEY_BLUEPRINTS));
            return new BlueprintMultipart(anchorType, blueprints);
        }

        @Override
        public JsonElement serialize(BlueprintMultipart src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty(KEY_POSITIONS, src.anchorType.toString());
            object.add(KEY_BLUEPRINTS, IRandom.IDENTIFIER.serialize(src.blueprints));
            return object;
        }
    }
}
