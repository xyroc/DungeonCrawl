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
import xiroc.dungeoncrawl.dungeon.blueprint.feature.AnchorBasedBlueprintFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.ChestConfiguration;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;
import xiroc.dungeoncrawl.util.CoordinateSpace;

import javax.annotation.Nullable;
import java.util.Random;

public class ChestFeature extends AnchorBasedBlueprintFeature<ChestConfiguration> {
    private static final String NBT_KEY_LOOT_TABLE = "LootTable";

    @Nullable
    protected final ResourceLocation lootTable;

    public ChestFeature(ChestConfiguration configuration, CoordinateSpace coordinateSpace, Rotation rotation, Random random, int stage) {
        super(configuration, coordinateSpace, rotation, random);
        this.lootTable = configuration.lootTable;
    }

    public ChestFeature(CompoundTag nbt) {
        super(nbt);
        if (nbt.contains(NBT_KEY_LOOT_TABLE)) {
            this.lootTable = new ResourceLocation(nbt.getString(NBT_KEY_LOOT_TABLE));
        } else {
            this.lootTable = null;
        }
    }

    @Override
    public void write(CompoundTag nbt) {
        super.write(nbt);
        if (lootTable != null) {
            nbt.putString(NBT_KEY_LOOT_TABLE, lootTable.toString());
        }
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
        level.setBlock(position, Blocks.CHEST.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, anchor.direction()), 3);
        if (level.getBlockEntity(position) instanceof RandomizableContainerBlockEntity chest) {
            ResourceLocation lootTable = this.lootTable != null ? this.lootTable : Loot.getLootTable(stage, random);
            Loot.setLoot(level, position, chest, lootTable, primaryTheme, secondaryTheme, random);
        }
    }
}
