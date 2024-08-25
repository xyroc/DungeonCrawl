package xiroc.dungeoncrawl.dungeon.component.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;

import java.util.Random;

public record SpawnerComponent(BlockPos position, Delegate<SpawnerType> type) implements DungeonComponent {
    public static final Codec<SpawnerComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            BlockPos.CODEC.fieldOf("position").forGetter(SpawnerComponent::position),
            ResourceLocation.CODEC.xmap(DatapackRegistries.SPAWNER_TYPE::delegateOrThrow, Delegate::key)
                    .fieldOf("type").forGetter(SpawnerComponent::type)
    ).apply(builder, SpawnerComponent::new));

    @Override
    public void generate(LevelAccessor level, BoundingBox worldGenBounds, Random random, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
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

    @Override
    public BoundingBoxBuilder boundingBox() {
        return BoundingBoxBuilder.fromPosition(position);
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
