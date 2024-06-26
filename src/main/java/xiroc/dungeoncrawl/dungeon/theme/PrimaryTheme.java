package xiroc.dungeoncrawl.dungeon.theme;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;

import java.lang.reflect.Type;
import java.util.Objects;

public record PrimaryTheme(BlockStateProvider masonry,
                           BlockStateProvider pillar,
                           BlockStateProvider floor,
                           BlockStateProvider fluid,
                           BlockStateProvider fencing,
                           BlockStateProvider stairs,
                           BlockStateProvider slab,
                           BlockStateProvider wall) {

    public static class Serializer implements JsonSerializer<PrimaryTheme>, JsonDeserializer<PrimaryTheme> {
        private static final String KEY_MASONRY = "masonry";
        private static final String KEY_PILLAR = "pillar";
        private static final String KEY_FLOOR = "floor";
        private static final String KEY_FLUID = "fluid";
        private static final String KEY_FENCING = "fencing";
        private static final String KEY_STAIRS = "stairs";
        private static final String KEY_SLAB = "slab";
        private static final String KEY_WALL = "wall";

        @Override
        public PrimaryTheme deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            Builder builder = new Builder()
                    .masonry(context.deserialize(object.get(KEY_MASONRY), BlockStateProvider.class))
                    .pillar(context.deserialize(object.get(KEY_PILLAR), BlockStateProvider.class))
                    .floor(context.deserialize(object.get(KEY_FLOOR), BlockStateProvider.class))
                    .fluid(context.deserialize(object.get(KEY_FLUID), BlockStateProvider.class))
                    .fencing(context.deserialize(object.get(KEY_FENCING), BlockStateProvider.class))
                    .stairs(context.deserialize(object.get(KEY_STAIRS), BlockStateProvider.class))
                    .slab(context.deserialize(object.get(KEY_SLAB), BlockStateProvider.class))
                    .wall(context.deserialize(object.get(KEY_WALL), BlockStateProvider.class));
            return builder.build();
        }

        @Override
        public JsonElement serialize(PrimaryTheme theme, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.add(KEY_MASONRY, context.serialize(theme.masonry));
            object.add(KEY_PILLAR, context.serialize(theme.pillar));
            object.add(KEY_FLOOR, context.serialize(theme.floor));
            object.add(KEY_FLUID, context.serialize(theme.fluid));
            object.add(KEY_FENCING, context.serialize(theme.fencing));
            object.add(KEY_STAIRS, context.serialize(theme.stairs));
            object.add(KEY_SLAB, context.serialize(theme.slab));
            object.add(KEY_WALL, context.serialize(theme.wall));
            return object;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private BlockStateProvider pillar = SingleBlock.AIR;
        private BlockStateProvider masonry = SingleBlock.AIR;
        private BlockStateProvider floor = SingleBlock.AIR;
        private BlockStateProvider stairs = SingleBlock.AIR;
        private BlockStateProvider wall = SingleBlock.AIR;
        private BlockStateProvider slab = SingleBlock.AIR;
        private BlockStateProvider fencing = SingleBlock.AIR;
        private BlockStateProvider fluid = SingleBlock.AIR;

        public Builder pillar(BlockStateProvider pillar) {
            this.pillar = pillar;
            return this;
        }

        public Builder masonry(BlockStateProvider generic) {
            this.masonry = generic;
            return this;
        }

        public Builder floor(BlockStateProvider floor) {
            this.floor = floor;
            return this;
        }

        public Builder stairs(BlockStateProvider stairs) {
            this.stairs = stairs;
            return this;
        }

        public Builder wall(BlockStateProvider wall) {
            this.wall = wall;
            return this;
        }

        public Builder slab(BlockStateProvider slab) {
            this.slab = slab;
            return this;
        }

        public Builder fencing(BlockStateProvider fencing) {
            this.fencing = fencing;
            return this;
        }

        public Builder fluid(BlockStateProvider fluid) {
            this.fluid = fluid;
            return this;
        }

        public PrimaryTheme build() {
            Objects.requireNonNull(pillar);
            Objects.requireNonNull(masonry);
            Objects.requireNonNull(floor);
            Objects.requireNonNull(fluid);
            Objects.requireNonNull(fencing);
            Objects.requireNonNull(stairs);
            Objects.requireNonNull(slab);
            Objects.requireNonNull(wall);
            return new PrimaryTheme(masonry, pillar, floor, fluid, fencing, stairs, slab, wall);
        }
    }
}