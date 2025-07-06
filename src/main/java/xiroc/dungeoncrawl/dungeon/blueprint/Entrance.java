package xiroc.dungeoncrawl.dungeon.blueprint;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.serialization.Codec;
import net.minecraft.core.IdMapper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.component.EntranceComponent;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

public record Entrance(Anchor placement, Optional<Decoration> decoration, Optional<CustomParts> customParts) {
    public Entrance(Anchor placement) {
        this(placement, Optional.of(Decoration.PRIMARY), Optional.empty());
    }

    /**
     * Creates an {@link EntranceComponent} with this entrance's decoration at the provided anchor.
     *
     * @param placement the entrance anchor to place the entrance at
     * @return the {@link EntranceComponent}, or null if this entrance does not have a decoration
     */
    @Nullable
    public EntranceComponent place(Anchor placement) {
        return decoration.map(value -> new EntranceComponent(new Anchor(placement.position().above(), placement.direction()), value)).orElse(null);
    }

    public record CustomParts(IRandom<Delegate<Blueprint>> open, IRandom<Delegate<Blueprint>> closed) {
        public static class Serializer implements JsonSerializer<CustomParts>, JsonDeserializer<CustomParts> {
            private static final String KEY_OPEN = "open";
            private static final String KEY_CLOSED = "closed";

            @Override
            public CustomParts deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
                JsonObject object = json.getAsJsonObject();
                IRandom<Delegate<Blueprint>> open = context.deserialize(object.get(KEY_OPEN), Blueprint.Types.RANDOM);
                IRandom<Delegate<Blueprint>> closed = context.deserialize(object.get(KEY_CLOSED), Blueprint.Types.RANDOM);
                return new CustomParts(open, closed);
            }

            @Override
            public JsonElement serialize(CustomParts customParts, Type type, JsonSerializationContext context) {
                JsonObject object = new JsonObject();
                object.add(KEY_OPEN, context.serialize(customParts.open, Blueprint.Types.RANDOM));
                object.add(KEY_CLOSED, context.serialize(customParts.closed, Blueprint.Types.RANDOM));
                return object;
            }
        }

        public static class Builder {
            private IRandom<Delegate<Blueprint>> open = null;
            private IRandom<Delegate<Blueprint>> closed = null;

            public CustomParts build() {
                return new CustomParts(Objects.requireNonNull(open), Objects.requireNonNull(closed));
            }

            public Builder open(IRandom<Delegate<Blueprint>> open) {
                this.open = Objects.requireNonNull(open);
                return this;
            }

            public Builder closed(IRandom<Delegate<Blueprint>> closed) {
                this.closed = Objects.requireNonNull(closed);
                return this;
            }
        }
    }

    public interface Decoration {
        Decoration NONE = (level, placement, worldGenBounds, random, worldGenContext) ->
                WorldEditor.fill(level, SingleBlock.AIR,
                        null, placement.position().relative(placement.direction().getClockWise()),
                        placement.position().relative(placement.direction().getCounterClockWise()).above(2),
                        worldGenBounds, random, false);

        Decoration PRIMARY = (level, placement, worldGenBounds, random, worldGenContext) ->
                WorldEditor.placeEntrance(level, worldGenContext.primaryTheme().get().stairs(), placement.position(), placement.direction().getClockWise(), worldGenBounds, random, false, true);

        Decoration SECONDARY = (level, placement, worldGenBounds, random, worldGenContext) ->
                WorldEditor.placeEntrance(level, worldGenContext.secondaryTheme().get().stairs(), placement.position(), placement.direction().getClockWise(), worldGenBounds, random, false, true);

        private static IdMapper<Decoration> gatherDecorations() {
            IdMapper<Decoration> decorations = new IdMapper<>();
            decorations.addMapping(NONE, 0);
            decorations.addMapping(PRIMARY, 1);
            decorations.addMapping(SECONDARY, 2);
            return decorations;
        }

        IdMapper<Decoration> DECORATIONS = gatherDecorations();

        Codec<Decoration> CODEC = Codec.INT.xmap(DECORATIONS::byId, DECORATIONS::getId);

        void generate(LevelAccessor level, Anchor placement, BoundingBox worldGenBounds, RandomSource random, DungeonWorldGenContext worldGenContext);
    }
}
