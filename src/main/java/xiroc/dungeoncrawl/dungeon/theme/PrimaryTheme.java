package xiroc.dungeoncrawl.dungeon.theme;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;

import java.util.Objects;

public record PrimaryTheme(ResourceLocation key,
                           BlockStateProvider masonry,
                           BlockStateProvider pillar,
                           BlockStateProvider floor,
                           BlockStateProvider fluid,
                           BlockStateProvider fencing,
                           BlockStateProvider stairs,
                           BlockStateProvider slab,
                           BlockStateProvider wall) {
    private static final String KEY_THEME = "theme";
    private static final String KEY_MASONRY = "masonry";
    private static final String KEY_PILLAR = "pillar";
    private static final String KEY_FLOOR = "floor";
    private static final String KEY_FLUID = "fluid";
    private static final String KEY_FENCING = "fencing";
    private static final String KEY_STAIRS = "stairs";
    private static final String KEY_SLAB = "slab";
    private static final String KEY_WALL = "wall";

    public static PrimaryTheme deserialize(ResourceLocation key, JsonElement json) {
        JsonObject object = json.getAsJsonObject();
        JsonObject theme = object.getAsJsonObject(KEY_THEME);
        Builder builder = new Builder(key)
                .masonry(BlockStateProvider.deserialize(theme.get(KEY_MASONRY)))
                .pillar(BlockStateProvider.deserialize(theme.get(KEY_PILLAR)))
                .floor(BlockStateProvider.deserialize(theme.get(KEY_FLOOR)))
                .fluid(BlockStateProvider.deserialize(theme.get(KEY_FLUID)))
                .fencing(BlockStateProvider.deserialize(theme.get(KEY_FENCING)))
                .stairs(BlockStateProvider.deserialize(theme.get(KEY_STAIRS)))
                .slab(BlockStateProvider.deserialize(theme.get(KEY_SLAB)))
                .wall(BlockStateProvider.deserialize(theme.get(KEY_WALL)));
        return builder.build();
    }

    public static JsonObject serialize(PrimaryTheme src) {
        Gson gson = BlockStateProvider.GSON;
        JsonObject object = new JsonObject();
        JsonObject theme = new JsonObject();
        theme.add(KEY_MASONRY, gson.toJsonTree(src.masonry));
        theme.add(KEY_PILLAR, gson.toJsonTree(src.pillar));
        theme.add(KEY_FLOOR, gson.toJsonTree(src.floor));
        theme.add(KEY_FLUID, gson.toJsonTree(src.fluid));
        theme.add(KEY_FENCING, gson.toJsonTree(src.fencing));
        theme.add(KEY_STAIRS, gson.toJsonTree(src.stairs));
        theme.add(KEY_SLAB, gson.toJsonTree(src.slab));
        theme.add(KEY_WALL, gson.toJsonTree(src.wall));
        object.add(KEY_THEME, theme);
        return object;
    }

    public static Builder builder(ResourceLocation key) {
        return new Builder(key);
    }

    public static class Builder {
        private final ResourceLocation key;
        private BlockStateProvider pillar = SingleBlock.AIR;
        private BlockStateProvider masonry = SingleBlock.AIR;
        private BlockStateProvider floor = SingleBlock.AIR;
        private BlockStateProvider stairs = SingleBlock.AIR;
        private BlockStateProvider wall = SingleBlock.AIR;
        private BlockStateProvider slab = SingleBlock.AIR;
        private BlockStateProvider fencing = SingleBlock.AIR;
        private BlockStateProvider fluid = SingleBlock.AIR;

        public Builder(ResourceLocation key) {
            this.key = key;
        }

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
            Objects.requireNonNull(key);
            Objects.requireNonNull(pillar);
            Objects.requireNonNull(masonry);
            Objects.requireNonNull(floor);
            Objects.requireNonNull(fluid);
            Objects.requireNonNull(fencing);
            Objects.requireNonNull(stairs);
            Objects.requireNonNull(slab);
            Objects.requireNonNull(wall);
            return new PrimaryTheme(key, masonry, pillar, floor, fluid, fencing, stairs, slab, wall);
        }
    }
}