package xiroc.dungeoncrawl.dungeon.blueprint.feature.type;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.ChestConfiguration;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;
import xiroc.dungeoncrawl.util.CoordinateSpace;

import java.util.Random;

public class TNTChestFeature extends ChestFeature {
    public TNTChestFeature(ChestConfiguration configuration, CoordinateSpace coordinateSpace, Rotation rotation, Random random, int stage) {
        super(configuration, coordinateSpace, rotation, random, stage);
    }

    public TNTChestFeature(CompoundTag nbt) {
        super(nbt);
    }

    @Override
    protected void place(WorldGenLevel level, Anchor anchor, Random random, BoundingBox worldGenBounds, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        BlockPos position = anchor.position();
        if (!worldGenBounds.isInside(position)) {
            return;
        }
        if (!level.getBlockState(position.below()).isFaceSturdy(level, position.below(), Direction.UP)) {
            return;
        }
        level.setBlock(position, Blocks.TRAPPED_CHEST.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, anchor.direction()), 3);
        level.setBlock(position.below(2), Blocks.TNT.defaultBlockState(), 3);
        if (level.getBlockEntity(position) instanceof RandomizableContainerBlockEntity chest) {
            ResourceLocation lootTable = this.lootTable != null ? this.lootTable : Loot.getLootTable(stage, random);
            Loot.setLoot(level, position, chest, lootTable, primaryTheme, secondaryTheme, random);
        }
    }
}
