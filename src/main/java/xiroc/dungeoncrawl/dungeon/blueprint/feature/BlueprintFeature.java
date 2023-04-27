package xiroc.dungeoncrawl.dungeon.blueprint.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.FeatureConfiguration;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.random.ArrayUrn;

import java.util.Random;

public abstract class BlueprintFeature<T extends FeatureConfiguration, P> {
    private static final String NBT_KEY_INSTANCES = "Instances";

    protected final P[] instances;

    public BlueprintFeature(T configuration, CoordinateSpace coordinateSpace, Rotation rotation, Random random) {
        ArrayUrn<Anchor> potentialPositions = null;
        int amount = Math.min(configuration.amount.nextInt(random), potentialPositions.elementsLeft());
        instances = makeInstanceArray(amount);
        for (int i = 0; i < amount; ++i) {
            Anchor anchor = potentialPositions.roll(random);
            instances[i] = createInstance(coordinateSpace.rotateAndTranslateToOrigin(anchor.position(), rotation), rotation.rotate(anchor.direction()), configuration, coordinateSpace, rotation, random);
        }
        potentialPositions.reset();
    }

    public BlueprintFeature(CompoundTag nbt) {
        ListTag nbtInstances = nbt.getList(NBT_KEY_INSTANCES, Tag.TAG_COMPOUND);
        instances = makeInstanceArray(nbtInstances.size());
        for (int i = 0; i < instances.length; ++i) {
            instances[i] = readInstance(nbtInstances.getCompound(i));
        }
    }

    protected abstract void place(WorldGenLevel level, P instance, Random random, BoundingBox worldGenBounds, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage);

    public void write(CompoundTag nbt) {
        ListTag nbtInstances = new ListTag();
        for (P instance : instances) {
            nbtInstances.add(writeInstance(instance));
        }
        nbt.put(NBT_KEY_INSTANCES, nbtInstances);
    }

    /*
     * Necessary because Java generics are... underwhelming
     */
    protected abstract P[] makeInstanceArray(int size);

    protected abstract P createInstance(BlockPos position, Direction facing, T configuration, CoordinateSpace coordinateSpace, Rotation rotation, Random random);

    protected abstract P readInstance(CompoundTag nbt);

    protected abstract CompoundTag writeInstance(P instance);

    public void placeAll(WorldGenLevel level, Random random, BoundingBox worldGenBounds, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        for (P instance : instances) {
            place(level, instance, random, worldGenBounds, primaryTheme, secondaryTheme, stage);
        }
    }

    protected interface Constructor<T extends FeatureConfiguration> {
        BlueprintFeature<T, ?> create(T configuration, CoordinateSpace coordinateSpace, Rotation rotation, Random random, int stage);
    }

}