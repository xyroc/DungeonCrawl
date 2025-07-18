package xiroc.dungeoncrawl.dungeon.component.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.loot.LootTable;
import xiroc.dungeoncrawl.dungeon.block.MetaBlock;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.util.storage.GlobalCodecs;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

public record SarcophagusComponent(Anchor placement, Holder<SpawnerType> spawnerType, ResourceKey<LootTable> lootTable) implements DungeonComponent {
    public static final Codec<SarcophagusComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Anchor.CODEC.fieldOf("placement").forGetter(SarcophagusComponent::placement),
            SpawnerType.HOLDER_CODEC.fieldOf("spawner_type").forGetter(SarcophagusComponent::spawnerType),
            GlobalCodecs.LOOT_TABLE.fieldOf("loot_table").forGetter(SarcophagusComponent::lootTable)
    ).apply(builder, SarcophagusComponent::new));

    @Override
    public void generate(LevelAccessor level, BoundingBox worldGenBounds, RandomSource random, DungeonWorldGenContext worldGenContext) {
        final BlockPos pos = placement.position().relative(placement.direction());
        final PrimaryTheme primaryTheme = worldGenContext.primaryTheme().value();
        final BlockStateProvider stair = primaryTheme.stairs();

        Rotation[] rotations = {Rotation.CLOCKWISE_90, Rotation.COUNTERCLOCKWISE_90};
        for (Rotation rotation : rotations) {
            final Direction direction = rotation.rotate(placement.direction());
            final BlockPos start = pos.relative(direction);
            final BlockPos end = start.relative(placement.direction(), 4);

            final BlockStateProvider stairBottom = new MetaBlock(Blocks.COBBLESTONE_STAIRS.defaultBlockState()
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, direction.getOpposite()))
                    .attach(stair);

            final BlockStateProvider stairTop = new MetaBlock(Blocks.COBBLESTONE_STAIRS.defaultBlockState()
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, direction.getOpposite())
                    .setValue(BlockStateProperties.HALF, Half.TOP))
                    .attach(stair);

            WorldEditor.fill(level, stairBottom, stairBottom, start, end, worldGenBounds, random, true);
            WorldEditor.fill(level, stairTop, stairTop, start.above(), end.above(), worldGenBounds, random, true);
            WorldEditor.fill(level, stairBottom, stairBottom, start.above(2), end.above(2), worldGenBounds, random, true);
        }

        WorldEditor.placeStairs(level, stair, pos, worldGenBounds, Half.BOTTOM, placement.direction(), random, true, true, true);
        WorldEditor.placeStairs(level, stair, pos.relative(placement.direction(), 4), worldGenBounds, Half.BOTTOM, placement.direction().getOpposite(), random, true, true, true);
        WorldEditor.fill(level, primaryTheme.masonry(), primaryTheme.masonry(), pos.relative(placement.direction()), pos.relative(placement.direction(), 3), worldGenBounds, random, false);

        WorldEditor.placeStairs(level, stair, pos.above(), worldGenBounds, Half.TOP, placement.direction(), random, true, true, true);
        WorldEditor.placeStairs(level, stair, pos.above().relative(placement.direction(), 4), worldGenBounds, Half.TOP, placement.direction().getOpposite(), random, true, true,
                true);

        WorldEditor.placeStairs(level, stair, pos.above(2), worldGenBounds, Half.BOTTOM, placement.direction(), random, true, true, true);
        WorldEditor.placeStairs(level, stair, pos.above(2).relative(placement.direction(), 4), worldGenBounds, Half.BOTTOM, placement.direction().getOpposite(), random, true, true, true);
        WorldEditor.placeStairs(level, stair, pos.above(2).relative(placement.direction()), worldGenBounds, Half.TOP, placement.direction().getOpposite(), random, true, true, true);
        WorldEditor.placeStairs(level, stair, pos.above(2).relative(placement.direction(), 3), worldGenBounds, Half.TOP, placement.direction(), random, true, true, true);
    }

    @Override
    public BoundingBoxBuilder boundingBox() {
        return BoundingBoxBuilder.fromCorners(
                placement.position().relative(placement.direction().getCounterClockWise()),
                placement.position().relative(placement.direction().getClockWise()).relative(placement.direction(), 4).above(3)
        );
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
