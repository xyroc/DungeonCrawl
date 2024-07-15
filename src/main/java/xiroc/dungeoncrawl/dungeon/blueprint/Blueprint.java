package xiroc.dungeoncrawl.dungeon.blueprint;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.datapack.DatapackRegistries;
import xiroc.dungeoncrawl.datapack.delegate.Delegate;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.BlueprintFeature;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxUtils;

import java.util.Random;

public interface Blueprint {
    Codec<Delegate<Blueprint>> CODEC = ResourceLocation.CODEC.xmap(DatapackRegistries.BLUEPRINT::delegateOrThrow, Delegate::key);

    Blueprint EMPTY = new Blueprint() {
        @Override
        public void build(LevelAccessor world, BlockPos position, Rotation rotation, BoundingBox worldGenBounds, Random random,
                          PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        }

        @Override
        public int xSpan() {
            return 0;
        }

        @Override
        public int ySpan() {
            return 0;
        }

        @Override
        public int zSpan() {
            return 0;
        }

        @Override
        public ImmutableMap<ResourceLocation, ImmutableList<Anchor>> anchors() {
            return ImmutableMap.of();
        }

        @Override
        public ImmutableList<BlueprintFeature> features() {
            return ImmutableList.of();
        }

        @Override
        public BoundingBox createBoundingBox(Vec3i offset, Rotation rotation) {
            return BoundingBoxUtils.emptyBox();
        }
    };

    void build(LevelAccessor world, BlockPos position, Rotation rotation, BoundingBox worldGenBounds, Random random,
               PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage);

    int xSpan();

    int ySpan();

    int zSpan();

    ImmutableMap<ResourceLocation, ImmutableList<Anchor>> anchors();

    ImmutableList<BlueprintFeature> features();

    default ImmutableList<BlueprintMultipart> parts() {
        return ImmutableList.of();
    }

    default BoundingBoxBuilder boundingBox(Rotation rotation) {
        return switch (rotation) {
            case NONE, CLOCKWISE_180 -> new BoundingBoxBuilder(0, 0, 0, xSpan() - 1, ySpan() - 1, zSpan() - 1);
            case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> new BoundingBoxBuilder(0, 0, 0, zSpan() - 1, ySpan() - 1, xSpan() - 1);
        };
    }

    default BoundingBox createBoundingBox(Vec3i offset, Rotation rotation) {
        return switch (rotation) {
            case NONE, CLOCKWISE_180 ->
                    new BoundingBox(offset.getX(), offset.getY(), offset.getZ(), offset.getX() + xSpan() - 1, offset.getY() + ySpan() - 1, offset.getZ() + zSpan() - 1);
            case CLOCKWISE_90, COUNTERCLOCKWISE_90 ->
                    new BoundingBox(offset.getX(), offset.getY(), offset.getZ(), offset.getX() + zSpan() - 1, offset.getY() + ySpan() - 1, offset.getZ() + xSpan() - 1);
        };
    }

    default CoordinateSpace coordinateSpace(BlockPos offset) {
        return new CoordinateSpace(offset, xSpan(), zSpan());
    }

    default BlockPos centerHorizontally(BlockPos pos, Rotation rotation) {
        int z = zSpan() >> 1;
        int x = xSpan() >> 1;
        if (rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90) {
            return new BlockPos(pos.getX() - z, pos.getY(), pos.getZ() - x);
        } else {
            return new BlockPos(pos.getX() - x, pos.getY(), pos.getZ() - z);
        }
    }
}