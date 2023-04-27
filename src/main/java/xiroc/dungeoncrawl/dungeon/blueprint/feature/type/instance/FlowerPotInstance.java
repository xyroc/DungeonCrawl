package xiroc.dungeoncrawl.dungeon.blueprint.feature.type.instance;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class FlowerPotInstance extends BaseFeatureInstance {
    private static final String NBT_KEY_FLOWER = "Flower";

    public final Block flower;

    public FlowerPotInstance(BlockPos position, Block flower) {
        super(position);
        this.flower = flower;
    }

    public FlowerPotInstance(CompoundTag nbt) {
        super(nbt);
        this.flower = Registry.BLOCK.get(new ResourceLocation(nbt.getString(NBT_KEY_FLOWER)));
    }

    @Override
    public void write(CompoundTag nbt) {
        super.write(nbt);
        nbt.putString(NBT_KEY_FLOWER, Registry.BLOCK.getKey(flower).toString());
    }
}
