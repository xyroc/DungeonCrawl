package xiroc.dungeoncrawl.dungeon.blueprint.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Rotation;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.FeatureConfiguration;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.StorageHelper;

import java.util.Random;

public abstract class AnchorBasedBlueprintFeature<T extends FeatureConfiguration> extends BlueprintFeature<T, Anchor> {
    public AnchorBasedBlueprintFeature(T configuration, CoordinateSpace coordinateSpace, Rotation rotation, Random random) {
        super(configuration, coordinateSpace, rotation, random);
    }

    public AnchorBasedBlueprintFeature(CompoundTag nbt) {
        super(nbt);
    }

    @Override
    protected Anchor[] makeInstanceArray(int size) {
        return new Anchor[size];
    }

    @Override
    protected Anchor createInstance(BlockPos position, Direction facing, T configuration, CoordinateSpace coordinateSpace, Rotation rotation, Random random) {
        return new Anchor(position, facing);
    }

    @Override
    protected Anchor readInstance(CompoundTag nbt) {
        return StorageHelper.decode(nbt, Anchor.CODEC);
    }

    @Override
    protected CompoundTag writeInstance(Anchor instance) {
        return (CompoundTag) StorageHelper.encode(instance, Anchor.CODEC);
    }
}
