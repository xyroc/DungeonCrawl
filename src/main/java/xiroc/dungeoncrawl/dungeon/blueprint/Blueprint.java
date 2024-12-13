package xiroc.dungeoncrawl.dungeon.blueprint;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.BlueprintFeature;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;

import java.util.Random;

public interface Blueprint {
    Codec<Delegate<Blueprint>> CODEC = ResourceLocation.CODEC.xmap(DatapackRegistries.BLUEPRINT::delegateOrThrow, Delegate::key);

    void build(LevelAccessor world, BlockPos position, Rotation rotation, BoundingBox worldGenBounds, Random random,
               PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage);

    int xSpan();

    int ySpan();

    int zSpan();

    ImmutableMap<ResourceLocation, ImmutableList<Anchor>> anchors();

    ImmutableList<BlueprintFeature> features();

    ImmutableList<Entrance> entrances();

    default ImmutableList<BlueprintMultipart> parts() {
        return ImmutableList.of();
    }

    default BoundingBoxBuilder boundingBox(Rotation rotation) {
        return switch (rotation) {
            case NONE, CLOCKWISE_180 -> new BoundingBoxBuilder(0, 0, 0, xSpan() - 1, ySpan() - 1, zSpan() - 1);
            case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> new BoundingBoxBuilder(0, 0, 0, zSpan() - 1, ySpan() - 1, xSpan() - 1);
        };
    }

    default CoordinateSpace coordinateSpace(BlockPos offset) {
        return new CoordinateSpace(offset, xSpan(), zSpan());
    }
}