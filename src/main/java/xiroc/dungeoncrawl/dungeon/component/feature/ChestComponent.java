package xiroc.dungeoncrawl.dungeon.component.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.loot.LootTable;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.util.storage.GlobalCodecs;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;

public record ChestComponent(Anchor placement, ResourceKey<LootTable> lootTable) implements DungeonComponent {
    public static final MapCodec<ChestComponent> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
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
        level.setBlock(position, Blocks.CHEST.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, placement.direction()), 3);
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
