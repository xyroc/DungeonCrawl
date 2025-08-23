package xiroc.dungeoncrawl.dungeon.component.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;

public record SpawnerComponent(BlockPos position, Holder<SpawnerType> type) implements DungeonComponent {
    public static final MapCodec<SpawnerComponent> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            BlockPos.CODEC.fieldOf("position").forGetter(SpawnerComponent::position),
            SpawnerType.HOLDER_CODEC.fieldOf("spawner_type").forGetter(SpawnerComponent::type)
    ).apply(builder, SpawnerComponent::new));

    @Override
    public void generate(LevelAccessor level, BoundingBox worldGenBounds, RandomSource random, DungeonWorldGenContext worldGenContext) {
        if (!worldGenBounds.isInside(position)) {
            return;
        }
        level.setBlock(position, Blocks.SPAWNER.defaultBlockState(), 2);
        BlockEntity blockEntity = level.getBlockEntity(position);
        if (blockEntity instanceof SpawnerBlockEntity spawner) {
            spawner.getSpawner().load(spawner.getLevel(), position, type.value().createData(random, worldGenContext.level(), level.registryAccess()));
        } else {
            DungeonCrawl.LOGGER.warn("Could not fetch a spawner entity at {}", position);
        }
    }

    @Override
    public BoundingBoxBuilder boundingBox() {
        return BoundingBoxBuilder.fromPosition(position);
    }

    @Override
    public MapCodec<? extends DungeonComponent> codec() {
        return CODEC;
    }
}
