package xiroc.dungeoncrawl.dungeon.component.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;

import java.util.Optional;
import java.util.Random;

public record TNTChestComponent(Anchor placement, Optional<ResourceLocation> lootTable) implements DungeonComponent {
    public static final Codec<TNTChestComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Anchor.CODEC.fieldOf("placement").forGetter(TNTChestComponent::placement),
            ResourceLocation.CODEC.optionalFieldOf("loot_table").forGetter(TNTChestComponent::lootTable)
    ).apply(builder, TNTChestComponent::new));

    @Override
    public void generate(LevelAccessor level, BoundingBox worldGenBounds, Random random, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
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

    @Override
    public BoundingBoxBuilder boundingBox() {
        return BoundingBoxBuilder.fromCorners(placement.position(), placement.position().below(2));
    }

    @Override
    public int componentType() {
        return DECODERS.getId(CODEC);
    }

    @Override
    public <T> DataResult<T> encode(DynamicOps<T> ops) {
        return CODEC.encodeStart(ops, this);
    }
}
