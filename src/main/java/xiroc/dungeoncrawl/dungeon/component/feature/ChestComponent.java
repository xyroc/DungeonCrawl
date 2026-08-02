package xiroc.dungeoncrawl.dungeon.component.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.loot.LootTable;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.util.storage.GlobalCodecs;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

public record ChestComponent(BlockState chest, Anchor placement, ResourceKey<LootTable> lootTable) implements DungeonComponent {
    public static final MapCodec<ChestComponent> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            GlobalCodecs.BLOCK_STATE.fieldOf("chest").forGetter(ChestComponent::chest),
            Anchor.CODEC.fieldOf("placement").forGetter(ChestComponent::placement),
            GlobalCodecs.LOOT_TABLE.fieldOf("loot_table").forGetter(ChestComponent::lootTable)
    ).apply(builder, ChestComponent::new));

    @Override
    public void generate(LevelAccessor level, BoundingBox worldGenBounds, RandomSource random, DungeonWorldGenContext worldGenContext) {
        BlockPos position = placement.position();
        if (!worldGenBounds.isInside(position)) {
            return;
        }
        if (!level.getBlockState(position.below()).isFaceSturdy(level, position.below(), Direction.UP)) {
            return;
        }
        Direction facing = placement.direction();
        if (!facing.getAxis().isHorizontal()) {
            facing = Orientation.randomHorizontalDirection(random);
        }
        BlockState chest = DungeonBlocks.applyProperty(this.chest, BlockStateProperties.HORIZONTAL_FACING, facing);

        if (level.getFluidState(position).is(FluidTags.WATER)) {
            chest = DungeonBlocks.applyProperty(chest, BlockStateProperties.WATERLOGGED, true);
        }

        WorldEditor.Unsafe.placeBlock(level, position, chest, false);
        if (level.getBlockEntity(position) instanceof RandomizableContainerBlockEntity randomizableContainerBlockEntity) {
            randomizableContainerBlockEntity.setLootTable(lootTable);
        }
    }

    @Override
    public BoundingBoxBuilder boundingBox() {
        return BoundingBoxBuilder.fromPosition(placement.position());
    }

    @Override
    public MapCodec<? extends DungeonComponent> codec() {
        return CODEC;
    }
}
