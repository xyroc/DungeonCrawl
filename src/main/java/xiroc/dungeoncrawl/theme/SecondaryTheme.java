package xiroc.dungeoncrawl.theme;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.ResourceLocation;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;

import javax.annotation.Nullable;

public class SecondaryTheme {

    public final BlockStateProvider pillar, trapDoor, door, material, stairs, slab, fence, fenceGate, button, pressurePlate;

    protected ResourceLocation key;

    @Nullable
    protected Integer id;

    public SecondaryTheme(BlockStateProvider pillar,
                          BlockStateProvider trapDoor,
                          BlockStateProvider door,
                          BlockStateProvider material,
                          BlockStateProvider stairs,
                          BlockStateProvider slab,
                          BlockStateProvider fence,
                          BlockStateProvider fenceGate,
                          BlockStateProvider button,
                          BlockStateProvider pressurePlate) {
        this.pillar = pillar;
        this.trapDoor = trapDoor;
        this.door = door;
        this.material = material;
        this.stairs = stairs;
        this.slab = slab;
        this.fence = fence;
        this.fenceGate = fenceGate;
        this.button = button;
        this.pressurePlate = pressurePlate;
    }

    public ResourceLocation getKey() {
        return key;
    }

    public BlockStateProvider getPillar() {
        return pillar;
    }

    public BlockStateProvider getTrapDoor() {
        return trapDoor;
    }

    public BlockStateProvider getDoor() {
        return door;
    }

    public BlockStateProvider getMaterial() {
        return material;
    }

    public BlockStateProvider getStairs() {
        return stairs;
    }

    public BlockStateProvider getSlab() {
        return slab;
    }

    public BlockStateProvider getFence() {
        return fence;
    }

    public BlockStateProvider getFenceGate() {
        return fenceGate;
    }

    public BlockStateProvider getButton() {
        return button;
    }

    public BlockStateProvider getPressurePlate() {
        return pressurePlate;
    }

    public JsonObject serialize() {
        JsonObject object = new JsonObject();
        JsonObject theme = new JsonObject();
        theme.add("button", button.serialize());
        theme.add("door", door.serialize());
        theme.add("fence", fence.serialize());
        theme.add("fence_gate", fenceGate.serialize());
        theme.add("material", material.serialize());
        theme.add("pillar", pillar.serialize());
        theme.add("pressure_plate", pressurePlate.serialize());
        theme.add("slab", slab.serialize());
        theme.add("stairs", stairs.serialize());
        theme.add("trapdoor", trapDoor.serialize());
        object.add("theme", theme);
        return object;
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

        private Integer id;

        public Builder legacyId(int id) {
            this.id = id;
            return this;
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
            SecondaryTheme secondaryTheme = new SecondaryTheme(pillar, trapdoor, door, material, stairs, slab, fence, fenceGate, button, pressurePlate);
            if (id != null) {
                Theme.ID_TO_SECONDARY_THEME.put(id, secondaryTheme);
            }
            return secondaryTheme;
        }

    }

}
