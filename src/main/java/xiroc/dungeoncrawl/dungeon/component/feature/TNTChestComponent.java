package xiroc.dungeoncrawl.dungeon.component.feature;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

public record TNTChestComponent(ChestComponent chest) implements DungeonComponent {
    public static final MapCodec<TNTChestComponent> CODEC = ChestComponent.CODEC.xmap(TNTChestComponent::new, TNTChestComponent::chest);

    @Override
    public void generate(LevelAccessor level, BoundingBox worldGenBounds, RandomSource random, DungeonWorldGenContext worldGenContext) {
        chest.generate(level, worldGenBounds, random, worldGenContext);

        BlockPos tntPosition = chest.placement().position().below(2);
        if (!worldGenBounds.isInside(tntPosition)) {
            return;
        }

        WorldEditor.Unsafe.placeBlock(level, tntPosition, Blocks.TNT.defaultBlockState(), false);
    }

    @Override
    public BoundingBoxBuilder boundingBox() {
        BoundingBoxBuilder bounds = chest.boundingBox();
        bounds.encapsulate(BoundingBoxBuilder.fromPosition(chest.placement().position().below(2)));
        return bounds;
    }

    @Override
    public MapCodec<? extends DungeonComponent> codec() {
        return CODEC;
    }
}
