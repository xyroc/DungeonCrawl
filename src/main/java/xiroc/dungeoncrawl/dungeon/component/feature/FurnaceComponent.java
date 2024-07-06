package xiroc.dungeoncrawl.dungeon.component.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.util.random.value.RandomValue;
import xiroc.dungeoncrawl.util.random.value.Range;

import java.util.Random;

public record FurnaceComponent(Anchor placement, boolean lit) implements DungeonComponent {
    public static final Codec<FurnaceComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Anchor.CODEC.fieldOf("pos").forGetter(FurnaceComponent::placement),
            Codec.BOOL.fieldOf("lit").forGetter(FurnaceComponent::lit)
    ).apply(builder, FurnaceComponent::new));

    private static final RandomValue COAL_AMOUNT = new Range(1, 8);

    @Override
    public void generate(LevelAccessor level, BoundingBox worldGenBounds, Random random, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        BlockPos position = placement.position();
        if (!worldGenBounds.isInside(position)) {
            return;
        }
        BlockState block = Blocks.FURNACE.defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, placement.direction())
                .setValue(BlockStateProperties.LIT, lit);
        level.setBlock(position, block, 3);
        if (level.getBlockEntity(position) instanceof AbstractFurnaceBlockEntity furnace) {
            furnace.setItem(1, new ItemStack(Items.COAL, COAL_AMOUNT.nextInt(random)));
        }
    }

    @Override
    public BoundingBoxBuilder boundingBox() {
        return BoundingBoxBuilder.fromPosition(placement.position());
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
