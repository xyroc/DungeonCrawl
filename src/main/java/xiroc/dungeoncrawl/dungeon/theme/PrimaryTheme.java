package xiroc.dungeoncrawl.dungeon.theme;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;

import java.util.Objects;

public record PrimaryTheme(ResourceLocation key,
                           BlockStateProvider material,
                           BlockStateProvider generic,
                           BlockStateProvider solid,
                           BlockStateProvider pillar,
                           BlockStateProvider floor,
                           BlockStateProvider fluid,
                           BlockStateProvider fencing,
                           BlockStateProvider stairs,
                           BlockStateProvider solidStairs,
                           BlockStateProvider slab,
                           BlockStateProvider solidSlab,
                           BlockStateProvider wall) {
    private static final String KEY_THEME = "theme";
    private static final String KEY_MATERIAL = "material";
    private static final String KEY_GENERIC = "generic";
    private static final String KEY_SOLID = "solid";
    private static final String KEY_PILLAR = "pillar";
    private static final String KEY_FLOOR = "floor";
    private static final String KEY_FLUID = "fluid";
    private static final String KEY_FENCING = "fencing";
    private static final String KEY_STAIRS = "stairs";
    private static final String KEY_SOLID_STAIRS = "solid_stairs";
    private static final String KEY_SLAB = "slab";
    private static final String KEY_SOLID_SLAB = "solid_slab";
    private static final String KEY_WALL = "wall";

    public static PrimaryTheme deserialize(ResourceLocation key, JsonElement json) {
        JsonObject object = json.getAsJsonObject();
        JsonObject theme = object.getAsJsonObject(KEY_THEME);
        Builder builder = new Builder(key)
                .material(BlockStateProvider.deserialize(theme.get(KEY_MATERIAL)))
                .generic(BlockStateProvider.deserialize(theme.get(KEY_GENERIC)))
                .solid(BlockStateProvider.deserialize(theme.get(KEY_SOLID)))
                .pillar(BlockStateProvider.deserialize(theme.get(KEY_PILLAR)))
                .floor(BlockStateProvider.deserialize(theme.get(KEY_FLOOR)))
                .fluid(BlockStateProvider.deserialize(theme.get(KEY_FLUID)))
                .fencing(BlockStateProvider.deserialize(theme.get(KEY_FENCING)))
                .stairs(BlockStateProvider.deserialize(theme.get(KEY_STAIRS)))
                .solidStairs(BlockStateProvider.deserialize(theme.get(KEY_SOLID_STAIRS)))
                .slab(BlockStateProvider.deserialize(theme.get(KEY_SLAB)))
                .solidSlab(BlockStateProvider.deserialize(theme.get(KEY_SOLID_SLAB)))
                .wall(BlockStateProvider.deserialize(theme.get(KEY_WALL)));
        return builder.build();
    }

    public static JsonObject serialize(PrimaryTheme src) {
        Gson gson = BlockStateProvider.GSON;
        JsonObject object = new JsonObject();
        JsonObject theme = new JsonObject();
        theme.add(KEY_MATERIAL, gson.toJsonTree(src.material));
        theme.add(KEY_GENERIC, gson.toJsonTree(src.generic));
        theme.add(KEY_SOLID, gson.toJsonTree(src.solid));
        theme.add(KEY_PILLAR, gson.toJsonTree(src.pillar));
        theme.add(KEY_FLOOR, gson.toJsonTree(src.floor));
        theme.add(KEY_FLUID, gson.toJsonTree(src.fluid));
        theme.add(KEY_FENCING, gson.toJsonTree(src.fencing));
        theme.add(KEY_STAIRS, gson.toJsonTree(src.stairs));
        theme.add(KEY_SOLID_STAIRS, gson.toJsonTree(src.solidStairs));
        theme.add(KEY_SLAB, gson.toJsonTree(src.slab));
        theme.add(KEY_SOLID_SLAB, gson.toJsonTree(src.solidSlab));
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
        private BlockStateProvider solid = SingleBlock.AIR;
        private BlockStateProvider generic = SingleBlock.AIR;
        private BlockStateProvider floor = SingleBlock.AIR;
        private BlockStateProvider solidStairs = SingleBlock.AIR;
        private BlockStateProvider stairs = SingleBlock.AIR;
        private BlockStateProvider material = SingleBlock.AIR;
        private BlockStateProvider wall = SingleBlock.AIR;
        private BlockStateProvider slab = SingleBlock.AIR;
        private BlockStateProvider solidSlab = SingleBlock.AIR;
        private BlockStateProvider fencing = SingleBlock.AIR;
        private BlockStateProvider fluid = SingleBlock.AIR;

        public Builder(ResourceLocation key) {
            this.key = key;
        }

        public Builder pillar(BlockStateProvider pillar) {
            this.pillar = pillar;
            return this;
        }

        public Builder solid(BlockStateProvider solid) {
            this.solid = solid;
            return this;
        }

        public Builder generic(BlockStateProvider generic) {
            this.generic = generic;
            return this;
        }

        public Builder floor(BlockStateProvider floor) {
            this.floor = floor;
            return this;
        }

        public Builder solidStairs(BlockStateProvider solidStairs) {
            this.solidStairs = solidStairs;
            return this;
        }

        public Builder stairs(BlockStateProvider stairs) {
            this.stairs = stairs;
            return this;
        }

        public Builder material(BlockStateProvider material) {
            this.material = material;
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

        public Builder solidSlab(BlockStateProvider solidSlab) {
            this.solidSlab = solidSlab;
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
            Objects.requireNonNull(material);
            Objects.requireNonNull(pillar);
            Objects.requireNonNull(generic);
            Objects.requireNonNull(solid);
            Objects.requireNonNull(floor);
            Objects.requireNonNull(fluid);
            Objects.requireNonNull(fencing);
            Objects.requireNonNull(stairs);
            Objects.requireNonNull(solidStairs);
            Objects.requireNonNull(slab);
            Objects.requireNonNull(solidSlab);
            Objects.requireNonNull(wall);
            return new PrimaryTheme(key, material, generic, solid, pillar, floor, fluid, fencing, stairs, solidStairs, slab, solidSlab, wall);
        }
    }
}