package xiroc.dungeoncrawl.dungeon.blueprint.builtin.room;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.FeatureConfiguration;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.worldgen.RotatingWorldEditor;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import java.util.Random;

public record LowerStaircaseBlueprint(ImmutableMap<ResourceLocation, ImmutableList<Anchor>> anchors,
                                      ImmutableList<FeatureConfiguration> features, BlueprintSettings settings) implements Blueprint {
    @Override
    public void build(LevelAccessor world, BlockPos position, Rotation rotation, BoundingBox worldGenBounds, Random random, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        BlockStateProvider wall = primaryTheme.masonry();
        BlockStateProvider fencing = primaryTheme.fencing();
        BlockStateProvider floor = primaryTheme.floor();

        WorldEditor editor = new RotatingWorldEditor(world, coordinateSpace(position), rotation);
        editor.fill(wall, Vec3i.ZERO, new Vec3i(1, 8, 1), worldGenBounds, random, true, true, false);

        editor.fill(wall, new Vec3i(2, 1, 0), new Vec3i(2, 8, 0), worldGenBounds, random, false, true, false);
        editor.fill(fencing, new Vec3i(2, 1, 0), new Vec3i(2, 8, 0), worldGenBounds, random, true, false, false);
        editor.fill(wall, new Vec3i(3, 4, 0), new Vec3i(5, 8, 0), worldGenBounds, random, false, true, false);
        editor.fill(fencing, new Vec3i(3, 4, 0), new Vec3i(5, 8, 0), worldGenBounds, random, true, false, false);
        editor.fill(wall, new Vec3i(6, 1, 0), new Vec3i(6, 8, 0), worldGenBounds, random, false, true, false);
        editor.fill(fencing, new Vec3i(6, 1, 0), new Vec3i(6, 8, 0), worldGenBounds, random, true, false, false);

        WorldEditor.fillRing(world, wall, position.offset(4, 9, 4), 4, 3, 1, worldGenBounds, random, true, true);
        WorldEditor.fillRing(world, wall, position.offset(4, 1, 4), 4, 1, 9, worldGenBounds, random, true, true);
        WorldEditor.fillRing(world, SingleBlock.AIR, position.offset(4, 1, 4), 3, 2, 8, worldGenBounds, random, true, true);
        WorldEditor.fill(world, wall, position, position.offset(8, 0, 8), worldGenBounds, random, true, true);
    }

    @Override
    public int xSpan() {
        return 9;
    }

    @Override
    public int ySpan() {
        return 10;
    }

    @Override
    public int zSpan() {
        return 9;
    }
}