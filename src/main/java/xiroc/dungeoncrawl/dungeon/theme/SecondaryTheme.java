package xiroc.dungeoncrawl.dungeon.theme;

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

public record SecondaryTheme(BlockStateProvider material,
                             BlockStateProvider pillar,
                             BlockStateProvider stairs,
                             BlockStateProvider slab,
                             BlockStateProvider door,
                             BlockStateProvider trapDoor,
                             BlockStateProvider fence,
                             BlockStateProvider fenceGate,
                             BlockStateProvider button,
                             BlockStateProvider pressurePlate) {


    public static class Serializer implements JsonSerializer<SecondaryTheme>, JsonDeserializer<SecondaryTheme> {
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
        @Override
        public SecondaryTheme deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            Builder builder = new Builder()
                    .material(BlockStateProvider.deserialize(object.get(KEY_MATERIAL)))
                    .pillar(BlockStateProvider.deserialize(object.get(KEY_PILLAR)))
                    .stairs(BlockStateProvider.deserialize(object.get(KEY_STAIRS)))
                    .slab(BlockStateProvider.deserialize(object.get(KEY_SLAB)))
                    .door(BlockStateProvider.deserialize(object.get(KEY_DOOR)))
                    .trapdoor(BlockStateProvider.deserialize(object.get(KEY_TRAPDOOR)))
                    .fence(BlockStateProvider.deserialize(object.get(KEY_FENCE)))
                    .fenceGate(BlockStateProvider.deserialize(object.get(KEY_FENCE_GATE)))
                    .button(BlockStateProvider.deserialize(object.get(KEY_BUTTON)))
                    .pressurePlate(BlockStateProvider.deserialize(object.get(KEY_PRESSURE_PLATE)));
            return builder.build();
        }

        @Override
        public JsonElement serialize(SecondaryTheme theme, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.add(KEY_MATERIAL, context.serialize(theme.material));
            object.add(KEY_PILLAR, context.serialize(theme.pillar));
            object.add(KEY_STAIRS, context.serialize(theme.stairs));
            object.add(KEY_SLAB, context.serialize(theme.slab));
            object.add(KEY_DOOR, context.serialize(theme.door));
            object.add(KEY_TRAPDOOR, context.serialize(theme.trapDoor));
            object.add(KEY_FENCE, context.serialize(theme.fence));
            object.add(KEY_FENCE_GATE, context.serialize(theme.fenceGate));
            object.add(KEY_BUTTON, context.serialize(theme.button));
            object.add(KEY_PRESSURE_PLATE, context.serialize(theme.pressurePlate));
            return object;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
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
            return new SecondaryTheme(material, pillar, stairs, slab, door, trapdoor, fence, fenceGate, button, pressurePlate);
        }
    }
}