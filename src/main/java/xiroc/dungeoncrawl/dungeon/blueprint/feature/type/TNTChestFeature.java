package xiroc.dungeoncrawl.dungeon.blueprint.feature.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.PlacedFeature;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;

import java.util.Optional;
import java.util.Random;

public record TNTChestFeature(Anchor placement, Optional<ResourceLocation> lootTable) implements PlacedFeature {
    public static final Codec<PlacedFeature> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Anchor.CODEC.fieldOf("placement").forGetter(feature -> ((TNTChestFeature) feature).placement),
            ResourceLocation.CODEC.optionalFieldOf("loot_table").forGetter(feature -> ((TNTChestFeature) feature).lootTable)
    ).apply(builder, TNTChestFeature::new));

    @Override
    public void place(WorldGenLevel level, Random random, BoundingBox worldGenBounds, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        BlockPos position = placement.position();
        if (!worldGenBounds.isInside(position)) {
            return;
        }
        if (!level.getBlockState(position.below()).isFaceSturdy(level, position.below(), Direction.UP)) {
            return;
        }
        level.setBlock(position, Blocks.TRAPPED_CHEST.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, placement.direction()), 3);
        level.setBlock(position.below(2), Blocks.TNT.defaultBlockState(), 3);
        ResourceLocation lootTable = this.lootTable.orElse(Loot.getLootTable(stage, random));
        RandomizableContainerBlockEntity.setLootTable(level, random, position, lootTable);
    }
}
