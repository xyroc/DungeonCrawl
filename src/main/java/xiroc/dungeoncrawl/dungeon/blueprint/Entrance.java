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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.datapack.delegate.Delegate;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.component.EntranceComponent;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public record Entrance(Anchor placement, Optional<Decoration> decoration, Optional<CustomParts> customParts) {
    public Entrance(Anchor placement) {
        this(placement, Optional.of(Decoration.PRIMARY), Optional.empty());
    }

    public EntranceComponent place(Anchor placement) {
        return new EntranceComponent(new Anchor(placement.position().above(), placement.direction()), decoration);
    }

    public record CustomParts(IRandom<Delegate<Blueprint>> open, IRandom<Delegate<Blueprint>> closed) {
        public static class Serializer implements JsonSerializer<CustomParts>, JsonDeserializer<CustomParts> {
            private static final String KEY_OPEN = "open";
            private static final String KEY_CLOSED = "closed";

            @Override
            public CustomParts deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
                JsonObject object = json.getAsJsonObject();
                IRandom<Delegate<Blueprint>> open = IRandom.BLUEPRINT.deserialize(object.get(KEY_OPEN));
                IRandom<Delegate<Blueprint>> closed = IRandom.BLUEPRINT.deserialize(object.get(KEY_CLOSED));
                return new CustomParts(open, closed);
            }

            @Override
            public JsonElement serialize(CustomParts customParts, Type type, JsonSerializationContext context) {
                JsonObject object = new JsonObject();
                object.add(KEY_OPEN, IRandom.BLUEPRINT.serialize(customParts.open));
                object.add(KEY_CLOSED, IRandom.BLUEPRINT.serialize(customParts.closed));
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
        Decoration NONE = (level, placement, worldGenBounds, random, primaryTheme, secondaryTheme, stage) ->
                WorldEditor.fill(level, SingleBlock.AIR,
                        placement.position().relative(placement.direction().getClockWise()),
                        placement.position().relative(placement.direction().getCounterClockWise()).above(2),
                        worldGenBounds, random, false, true, false);

        Decoration PRIMARY = (level, placement, worldGenBounds, random, primaryTheme, secondaryTheme, stage) ->
                WorldEditor.placeEntrance(level, primaryTheme.stairs(), placement.position(), placement.direction().getClockWise(), worldGenBounds, random, false, true);

        Decoration SECONDARY = (level, placement, worldGenBounds, random, primaryTheme, secondaryTheme, stage) ->
                WorldEditor.placeEntrance(level, secondaryTheme.stairs(), placement.position(), placement.direction().getClockWise(), worldGenBounds, random, false, true);

        private static IdMapper<Decoration> gatherDecorations() {
            IdMapper<Decoration> decorations = new IdMapper<>();
            decorations.addMapping(NONE, 0);
            decorations.addMapping(PRIMARY, 1);
            decorations.addMapping(SECONDARY, 2);
            return decorations;
        }

        IdMapper<Decoration> DECORATIONS = gatherDecorations();

        Codec<Decoration> CODEC = Codec.INT.xmap(DECORATIONS::byId, DECORATIONS::getId);

        void generate(LevelAccessor level, Anchor placement, BoundingBox worldGenBounds, Random random, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage);
    }
}
