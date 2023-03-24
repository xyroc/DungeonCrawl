package xiroc.dungeoncrawl.dungeon.theme;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;

import java.util.Objects;

public record SecondaryTheme(ResourceLocation key,
                             BlockStateProvider material,
                             BlockStateProvider pillar,
                             BlockStateProvider stairs,
                             BlockStateProvider slab,
                             BlockStateProvider door,
                             BlockStateProvider trapDoor,
                             BlockStateProvider fence,
                             BlockStateProvider fenceGate,
                             BlockStateProvider button,
                             BlockStateProvider pressurePlate) {
    private static final String KEY_THEME = "theme";
    private static final String KEY_MATERIAL = "material";
    private static final String KEY_PILLAR = "pillar";
    private static final String KEY_STAIRS = "stairs";
    private static final String KEY_SLAB = "slab";
    private static final String KEY_DOOR = "door";
    private static final String KEY_TRAPDOOR = "trapdoor";
    private static final String KEY_FENCE = "fence";
    private static final String KEY_FENCE_GATE = "fence_gate";
    private static final String KEY_BUTTON = "button";
    private static final String KEY_PRESSURE_PLATE = "pressure_plate";

    public static SecondaryTheme deserialize(ResourceLocation key, JsonElement json) {
        JsonObject object = json.getAsJsonObject();
        JsonObject theme = object.getAsJsonObject(KEY_THEME);
        Builder builder = new Builder(key)
                .material(BlockStateProvider.deserialize(theme.get(KEY_MATERIAL)))
                .pillar(BlockStateProvider.deserialize(theme.get(KEY_PILLAR)))
                .stairs(BlockStateProvider.deserialize(theme.get(KEY_STAIRS)))
                .slab(BlockStateProvider.deserialize(theme.get(KEY_SLAB)))
                .door(BlockStateProvider.deserialize(theme.get(KEY_DOOR)))
                .trapdoor(BlockStateProvider.deserialize(theme.get(KEY_TRAPDOOR)))
                .fence(BlockStateProvider.deserialize(theme.get(KEY_FENCE)))
                .fenceGate(BlockStateProvider.deserialize(theme.get(KEY_FENCE_GATE)))
                .button(BlockStateProvider.deserialize(theme.get(KEY_BUTTON)))
                .pressurePlate(BlockStateProvider.deserialize(theme.get(KEY_PRESSURE_PLATE)));
        return builder.build();
    }

    public static JsonObject serialize(SecondaryTheme src) {
        Gson gson = BlockStateProvider.GSON;
        JsonObject object = new JsonObject();
        JsonObject theme = new JsonObject();
        theme.add(KEY_MATERIAL, gson.toJsonTree(src.material));
        theme.add(KEY_PILLAR, gson.toJsonTree(src.pillar));
        theme.add(KEY_STAIRS, gson.toJsonTree(src.stairs));
        theme.add(KEY_SLAB, gson.toJsonTree(src.slab));
        theme.add(KEY_DOOR, gson.toJsonTree(src.door));
        theme.add(KEY_TRAPDOOR, gson.toJsonTree(src.trapDoor));
        theme.add(KEY_FENCE, gson.toJsonTree(src.fence));
        theme.add(KEY_FENCE_GATE, gson.toJsonTree(src.fenceGate));
        theme.add(KEY_BUTTON, gson.toJsonTree(src.button));
        theme.add(KEY_PRESSURE_PLATE, gson.toJsonTree(src.pressurePlate));
        object.add(KEY_THEME, theme);
        return object;
    }

    public static Builder builder(ResourceLocation key) {
        return new Builder(key);
    }

    public static class Builder {
        private final ResourceLocation key;
        private BlockStateProvider button = SingleBlock.AIR;
        private BlockStateProvider door = SingleBlock.AIR;
        private BlockStateProvider fence = SingleBlock.AIR;
        private BlockStateProvider fenceGate = SingleBlock.AIR;
        private BlockStateProvider material = SingleBlock.AIR;
        private BlockStateProvider pillar = SingleBlock.AIR;
        private BlockStateProvider pressurePlate = SingleBlock.AIR;
        private BlockStateProvider slab = SingleBlock.AIR;
        private BlockStateProvider stairs = SingleBlock.AIR;
        private BlockStateProvider trapdoor = SingleBlock.AIR;

        public Builder(ResourceLocation key) {
            this.key = key;
        }

        public Builder button(BlockStateProvider provider) {
            this.button = provider;
            return this;
        }

        public Builder door(BlockStateProvider provider) {
            this.door = provider;
            return this;
        }

        public Builder fence(BlockStateProvider provider) {
            this.fence = provider;
            return this;
        }

        public Builder fenceGate(BlockStateProvider provider) {
            this.fenceGate = provider;
            return this;
        }

        public Builder material(BlockStateProvider provider) {
            this.material = provider;
            return this;
        }

        public Builder pillar(BlockStateProvider provider) {
            this.pillar = provider;
            return this;
        }

        public Builder pressurePlate(BlockStateProvider provider) {
            this.pressurePlate = provider;
            return this;
        }

        public Builder slab(BlockStateProvider provider) {
            this.slab = provider;
            return this;
        }

        public Builder stairs(BlockStateProvider provider) {
            this.stairs = provider;
            return this;
        }

        public Builder trapdoor(BlockStateProvider provider) {
            this.trapdoor = provider;
            return this;
        }

        public SecondaryTheme build() {
            Objects.requireNonNull(key);
            Objects.requireNonNull(material);
            Objects.requireNonNull(pillar);
            Objects.requireNonNull(stairs);
            Objects.requireNonNull(slab);
            Objects.requireNonNull(door);
            Objects.requireNonNull(trapdoor);
            Objects.requireNonNull(fence);
            Objects.requireNonNull(fenceGate);
            Objects.requireNonNull(button);
            Objects.requireNonNull(pressurePlate);
            return new SecondaryTheme(key, material, pillar, stairs, slab, door, trapdoor, fence, fenceGate, button, pressurePlate);
        }
    }
}