package xiroc.dungeoncrawl.dungeon.blueprint.feature.type.instance;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public class BaseFeatureInstance {
    private static final String NBT_KEY_X = "X";
    private static final String NBT_KEY_Y = "Y";
    private static final String NBT_KEY_Z = "Z";

    public final BlockPos position;

    public BaseFeatureInstance(BlockPos position) {
        this.position = position;
    }

    public BaseFeatureInstance(CompoundTag nbt) {
        this.position = new BlockPos(nbt.getInt(NBT_KEY_X), nbt.getInt(NBT_KEY_Y), nbt.getInt(NBT_KEY_Z));
    }

    public void write(CompoundTag nbt) {
        nbt.putInt(NBT_KEY_X, position.getX());
        nbt.putInt(NBT_KEY_Y, position.getY());
        nbt.putInt(NBT_KEY_Z, position.getZ());
    }
}
