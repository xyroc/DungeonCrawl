package xiroc.dungeoncrawl.dungeon.blueprint.feature.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.datapack.DatapackRegistries;
import xiroc.dungeoncrawl.datapack.delegate.Delegate;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.PlacedFeature;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;

import java.util.Random;

public record SpawnerFeature(BlockPos position, Delegate<SpawnerType> type) implements PlacedFeature {
    public static final Codec<PlacedFeature> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            BlockPos.CODEC.fieldOf("position").forGetter(feature -> ((SpawnerFeature) feature).position),
            ResourceLocation.CODEC.xmap(key -> Delegate.of(DatapackRegistries.SPAWNER_TYPE.get(key), key), Delegate::key)
                    .fieldOf("type").forGetter(feature -> ((SpawnerFeature) feature).type)
    ).apply(builder, SpawnerFeature::new));

    @Override
    public void place(WorldGenLevel level, Random random, BoundingBox worldGenBounds, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        if (!worldGenBounds.isInside(position)) {
            return;
        }
        level.setBlock(position, Blocks.SPAWNER.defaultBlockState(), 2);
        BlockEntity blockEntity = level.getBlockEntity(position);
        if (blockEntity instanceof SpawnerBlockEntity spawner) {
            spawner.getSpawner().load(spawner.getLevel(), position, type.get().createData(random, stage));
        } else {
            DungeonCrawl.LOGGER.warn("Could not fetch a spawner entity at {}", position);
        }
    }
}
