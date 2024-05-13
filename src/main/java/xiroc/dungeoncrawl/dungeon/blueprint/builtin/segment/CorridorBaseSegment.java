package xiroc.dungeoncrawl.dungeon.blueprint.builtin.segment;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.FeatureConfiguration;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.worldgen.RotatingWorldEditor;

import java.util.Random;

public record CorridorBaseSegment(ResourceLocation key, ImmutableMap<ResourceLocation, ImmutableList<Anchor>> anchors,
                                  ImmutableList<FeatureConfiguration> features, BlueprintSettings settings) implements Blueprint {
    @Override
    public void build(LevelAccessor world, BlockPos position, Rotation rotation, BoundingBox worldGenBounds, Random random, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        RotatingWorldEditor editor = new RotatingWorldEditor(world, coordinateSpace(position), rotation);
        BlockPos origin = BlockPos.ZERO;
        editor.fill(primaryTheme.floor(), origin, origin.offset(2, 0, 2), worldGenBounds, random, true, true, false);
        editor.fill(SingleBlock.AIR, origin.above(), origin.offset(2, 3, 2), worldGenBounds, random, true, true, false);
        editor.fill(primaryTheme.masonry(), origin.above(4), origin.offset(2, 4, 2), worldGenBounds, random, false, true, false);
    }

    @Override
    public int xSpan() {
        return 3;
    }

    @Override
    public int ySpan() {
        return 5;
    }

    @Override
    public int zSpan() {
        return 3;
    }
}
