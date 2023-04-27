package xiroc.dungeoncrawl.dungeon.blueprint.feature.type;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.BlueprintFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.SpawnerConfiguration;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.type.instance.SpawnerInstance;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.CoordinateSpace;

import java.util.Random;

public class SpawnerFeature extends BlueprintFeature<SpawnerConfiguration, SpawnerInstance> {
    public SpawnerFeature(SpawnerConfiguration configuration, CoordinateSpace coordinateSpace, Rotation rotation, Random random, int stage) {
        super(configuration, coordinateSpace, rotation, random);
    }

    public SpawnerFeature(CompoundTag nbt) {
        super(nbt);
    }

    @Override
    protected SpawnerInstance[] makeInstanceArray(int size) {
        return new SpawnerInstance[size];
    }

    @Override
    protected SpawnerInstance createInstance(BlockPos position, Direction facing, SpawnerConfiguration configuration, CoordinateSpace coordinateSpace, Rotation rotation, Random random) {
        return new SpawnerInstance(position, configuration.spawnerTypes.roll(random));
    }

    @Override
    protected SpawnerInstance readInstance(CompoundTag nbt) {
        return new SpawnerInstance(nbt);
    }

    @Override
    protected CompoundTag writeInstance(SpawnerInstance instance) {
        CompoundTag nbt = new CompoundTag();
        instance.write(nbt);
        return nbt;
    }

    @Override
    protected void place(WorldGenLevel level, SpawnerInstance instance, Random random, BoundingBox worldGenBounds, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        if (!worldGenBounds.isInside(instance.position)) {
            return;
        }
        level.setBlock(instance.position, Blocks.SPAWNER.defaultBlockState(), 2);
        BlockEntity blockEntity = level.getBlockEntity(instance.position);
        if (blockEntity instanceof SpawnerBlockEntity spawner) {
            spawner.getSpawner().load(spawner.getLevel(), instance.position, instance.spawnerType.get().createData(random, stage));
        } else {
            DungeonCrawl.LOGGER.warn("Could not fetch a spawner entity at {}", instance.position);
        }
    }
}
