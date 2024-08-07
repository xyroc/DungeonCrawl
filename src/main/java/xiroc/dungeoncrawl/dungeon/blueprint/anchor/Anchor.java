package xiroc.dungeoncrawl.dungeon.blueprint.anchor;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Rotation;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.Orientation;

import java.lang.reflect.Type;
import java.util.stream.IntStream;

public record Anchor(BlockPos position, Direction direction) {
    public static final Codec<Anchor> CODEC = Codec.INT_STREAM.comapFlatMap(
            (encoded) -> Util.fixedSize(encoded, 4).map((values) ->
                    new Anchor(new BlockPos(values[0], values[1], values[2]), Direction.from3DDataValue(values[3]))),
            (anchor) -> IntStream.of(anchor.position.getX(), anchor.position.getY(), anchor.position.getZ(), anchor.direction.get3DDataValue())
    ).stable();

    public static Anchor of(int x, int y, int z, Direction direction) {
        return new Anchor(new BlockPos(x, y, z), direction);
    }

    public Anchor opposite() {
        return new Anchor(position.relative(direction), direction.getOpposite());
    }

    public BlockPos.MutableBlockPos latchOnto(Anchor anchor, CoordinateSpace coordinateSpace) {
        Rotation rotation = Orientation.horizontalRotation(this.direction, anchor.direction.getOpposite());
        Vec3i offset = coordinateSpace.rotateAndTranslateToOrigin(this.position, rotation);
        return anchor.position.mutable().move(anchor.direction).move(-offset.getX(), -offset.getY(), -offset.getZ());
    }

    public static class Serializer implements JsonSerializer<Anchor>, JsonDeserializer<Anchor> {
        private static final String KEY_X = "x";
        private static final String KEY_Y = "y";
        private static final String KEY_Z = "z";
        private static final String KEY_DIRECTION = "direction";

        @Override
        public Anchor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            int x = object.has(KEY_X) ? object.get(KEY_X).getAsInt() : 0;
            int y = object.has(KEY_Y) ? object.get(KEY_Y).getAsInt() : 0;
            int z = object.has(KEY_Z) ? object.get(KEY_Z).getAsInt() : 0;
            return new Anchor(new BlockPos(x, y, z), Direction.byName(object.get(KEY_DIRECTION).getAsString()));
        }

        @Override
        public JsonElement serialize(Anchor src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty(KEY_X, src.position.getX());
            object.addProperty(KEY_Y, src.position.getY());
            object.addProperty(KEY_Z, src.position.getZ());
            object.addProperty(KEY_DIRECTION, src.direction.getName());
            return object;
        }
    }
}