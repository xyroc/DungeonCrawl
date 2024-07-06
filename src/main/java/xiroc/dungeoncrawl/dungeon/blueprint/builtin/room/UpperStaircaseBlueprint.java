package xiroc.dungeoncrawl.dungeon.blueprint.builtin.room;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.BlueprintFeature;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import java.util.Random;

public record UpperStaircaseBlueprint(ImmutableMap<ResourceLocation, ImmutableList<Anchor>> anchors,
                                      ImmutableList<BlueprintFeature> features, BlueprintSettings settings) implements Blueprint {
    @Override
    public void build(LevelAccessor world, BlockPos position, Rotation rotation, BoundingBox worldGenBounds, Random random, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        BlockPos end = position.offset(8, 8, 8);
        BlockStateProvider wall = new SingleBlock(Blocks.RED_CONCRETE);
        WorldEditor.fill(world, wall, position.offset(1, 8, 1), position.offset(7, 8, 7), worldGenBounds, random, true, true);
        WorldEditor.fillRing(world, wall, position.offset(4, 0, 4), 4, 1, 9, worldGenBounds, random, true, true);
        WorldEditor.fillRing(world, wall, position.offset(4, 0, 4), 4, 3, 1, worldGenBounds, random, true, true);
        WorldEditor.fill(world, SingleBlock.AIR,
                position.offset(1, 1, 1),
                end.offset(-1, -1, -1),
                worldGenBounds, random, true, true);
    }

    @Override
    public int xSpan() {
        return 9;
    }

    @Override
    public int ySpan() {
        return 9;
    }

    @Override
    public int zSpan() {
        return 9;
    }
}
