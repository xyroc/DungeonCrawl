package xiroc.dungeoncrawl.dungeon.blueprint.builtin.room;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import java.util.Random;

public record CornerRoomBlueprint(ResourceLocation key, ImmutableMap<ResourceLocation, ImmutableList<Anchor>> anchors, BlueprintSettings settings) implements Blueprint {
    @Override
    public void build(LevelAccessor world, BlockPos position, Rotation rotation, BoundingBox worldGenBounds, Random random, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        // Floor
        WorldEditor.fill(world, primaryTheme.generic(), position, position.offset(6, 0, 6), worldGenBounds, random, false, true);

        // Northern Wall
        WorldEditor.fill(world, primaryTheme.generic(), position.above(), position.offset(5, 4, 0), worldGenBounds, random, false, true);
        // Southern Wall
        WorldEditor.fill(world, primaryTheme.generic(), position.offset(1, 1, 6), position.offset(6, 4, 6), worldGenBounds, random, false, true);
        // Western Wall
        WorldEditor.fill(world, primaryTheme.generic(), position.offset(0, 1, 1), position.offset(0, 4, 6), worldGenBounds, random, false, true);
        // Eastern Wall
        WorldEditor.fill(world, primaryTheme.generic(), position.offset(6, 1, 0), position.offset(6, 4, 5), worldGenBounds, random, false, true);

        // Pillars
        WorldEditor.placePillar(world, primaryTheme.pillar(), primaryTheme.solidStairs(), position.offset(1, 1, 1), 4, false, true, true, false, worldGenBounds, random, true, true);
        WorldEditor.placePillar(world, primaryTheme.pillar(), primaryTheme.solidStairs(), position.offset(5, 1, 1), 4, false, false, true, true, worldGenBounds, random, true, true);
        WorldEditor.placePillar(world, primaryTheme.pillar(), primaryTheme.solidStairs(), position.offset(5, 1, 5), 4, true, false, false, true, worldGenBounds, random, true, true);
        WorldEditor.placePillar(world, primaryTheme.pillar(), primaryTheme.solidStairs(), position.offset(1, 1, 5), 4, true, true, false, false, worldGenBounds, random, true, true);

        // Ceiling
        BlockPos ceilingCenter = position.offset(3, 5, 3);
        WorldEditor.fillRing(world, primaryTheme.generic(), ceilingCenter, 3, 2, 1, worldGenBounds, random, false, true);

        WorldEditor.placeStairs(world, primaryTheme.stairs(), ceilingCenter.north(), worldGenBounds, Half.TOP, Direction.NORTH, random, false, true, false);
        WorldEditor.placeStairs(world, primaryTheme.stairs(), ceilingCenter.east(), worldGenBounds, Half.TOP, Direction.EAST, random, false, true, false);
        WorldEditor.placeStairs(world, primaryTheme.stairs(), ceilingCenter.south(), worldGenBounds, Half.TOP, Direction.SOUTH, random, false, true, false);
        WorldEditor.placeStairs(world, primaryTheme.stairs(), ceilingCenter.west(), worldGenBounds, Half.TOP, Direction.WEST, random, false, true, false);

        WorldEditor.placeBlock(world, primaryTheme.generic(), ceilingCenter.offset(-1, 0, -1), worldGenBounds, random, false, true, false);
        WorldEditor.placeBlock(world, primaryTheme.generic(), ceilingCenter.offset(1, 0, -1), worldGenBounds, random, false, true, false);
        WorldEditor.placeBlock(world, primaryTheme.generic(), ceilingCenter.offset(1, 0, 1), worldGenBounds, random, false, true, false);
        WorldEditor.placeBlock(world, primaryTheme.generic(), ceilingCenter.offset(-1, 0, 1), worldGenBounds, random, false, true, false);

        WorldEditor.placeBlock(world, primaryTheme.generic(), ceilingCenter.above(), worldGenBounds, random, false, true, false);

        // Air
        BlockState air = Blocks.CAVE_AIR.defaultBlockState();
        WorldEditor.placeBlock(world, air, ceilingCenter, worldGenBounds, true, true, false);
        WorldEditor.placeBlock(world, air, ceilingCenter.offset(-2, -1, 0), worldGenBounds, false, true, false);
        WorldEditor.placeBlock(world, air, ceilingCenter.offset(0, -1, 2), worldGenBounds, false, true, false);
        WorldEditor.placeBlock(world, air, ceilingCenter.offset(2, -1, 0), worldGenBounds, false, true, false);
        WorldEditor.placeBlock(world, air, ceilingCenter.offset(0, -1, -2), worldGenBounds, false, true, false);

        WorldEditor.fill(world, SingleBlock.AIR, position.offset(2, 1, 1), position.offset(4, 3, 1), worldGenBounds, random, true, true);
        WorldEditor.fill(world, SingleBlock.AIR, position.offset(5, 1, 2), position.offset(5, 3, 4), worldGenBounds, random, true, true);
        WorldEditor.fill(world, SingleBlock.AIR, position.offset(2, 1, 5), position.offset(4, 3, 5), worldGenBounds, random, true, true);
        WorldEditor.fill(world, SingleBlock.AIR, position.offset(1, 1, 2), position.offset(1, 3, 4), worldGenBounds, random, true, true);

        WorldEditor.fill(world, SingleBlock.AIR, position.offset(2, 1, 2), position.offset(4, 4, 4), worldGenBounds, random, true, true);
    }

    @Override
    public int xSpan() {
        return 7;
    }

    @Override
    public int ySpan() {
        return 9;
    }

    @Override
    public int zSpan() {
        return 7;
    }
}