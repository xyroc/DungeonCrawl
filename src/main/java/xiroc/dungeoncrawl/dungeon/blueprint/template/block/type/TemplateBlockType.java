package xiroc.dungeoncrawl.dungeon.blueprint.template.block.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.RegisterEvent;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.BlockChooser;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.init.ModRegistries;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import java.util.function.Function;

public interface TemplateBlockType extends BlockChooser {
    Codec<TemplateBlockType> CODEC = ModRegistries.TEMPlATE_BLOCK_TYPE.byNameCodec().dispatch(
            TemplateBlockType::codec,
            Function.identity()
    );

    ThemedTemplateBlockType.Factory FENCE_GATE = new ThemedTemplateBlockType.Factory(BlockChooser.fromSecondaryTheme(SecondaryTheme::fenceGate));

    ThemedTemplateBlockType.Factory FENCE = new ThemedTemplateBlockType.Factory(BlockChooser.fromSecondaryTheme(SecondaryTheme::fence));
    ThemedTemplateBlockType.Factory TRAPDOOR = new ThemedTemplateBlockType.Factory(BlockChooser.fromSecondaryTheme(SecondaryTheme::trapDoor));
    ThemedTemplateBlockType.Factory PRESSURE_PLATE = new ThemedTemplateBlockType.Factory(BlockChooser.fromSecondaryTheme(SecondaryTheme::pressurePlate));
    ThemedTemplateBlockType.Factory BUTTON = new ThemedTemplateBlockType.Factory(BlockChooser.fromSecondaryTheme(SecondaryTheme::button));
    ThemedTemplateBlockType.Factory MATERIAL_PILLAR = new ThemedTemplateBlockType.Factory(BlockChooser.fromSecondaryTheme(SecondaryTheme::pillar));
    ThemedTemplateBlockType.Factory MATERIAL_SLAB = new ThemedTemplateBlockType.Factory(BlockChooser.fromSecondaryTheme(SecondaryTheme::slab));
    ThemedTemplateBlockType.Factory MATERIAL_STAIRS = new ThemedTemplateBlockType.Factory(BlockChooser.fromSecondaryTheme(SecondaryTheme::stairs));
    ThemedTemplateBlockType.Factory MATERIAL = new ThemedTemplateBlockType.Factory(BlockChooser.fromSecondaryTheme(SecondaryTheme::material));
    ThemedTemplateBlockType.Factory WALL = new ThemedTemplateBlockType.Factory(BlockChooser.fromPrimaryTheme(PrimaryTheme::wall));
    ThemedTemplateBlockType.Factory FLUID = new ThemedTemplateBlockType.Factory(BlockChooser.fromPrimaryTheme(PrimaryTheme::fluid));
    ThemedTemplateBlockType.Factory FLOOR = new ThemedTemplateBlockType.Factory(BlockChooser.fromPrimaryTheme(PrimaryTheme::floor));
    ThemedTemplateBlockType.Factory FENCING = new ThemedTemplateBlockType.Factory(BlockChooser.fromPrimaryTheme(PrimaryTheme::fencing));
    ThemedTemplateBlockType.Factory MASONRY_PILLAR = new ThemedTemplateBlockType.Factory(BlockChooser.fromPrimaryTheme(PrimaryTheme::pillar));
    ThemedTemplateBlockType.Factory MASONRY_SLAB = new ThemedTemplateBlockType.Factory(BlockChooser.fromPrimaryTheme(PrimaryTheme::slab));
    ThemedTemplateBlockType.Factory MASONRY_STAIRS = new ThemedTemplateBlockType.Factory(BlockChooser.fromPrimaryTheme(PrimaryTheme::stairs));
    ThemedTemplateBlockType.Factory MASONRY = new ThemedTemplateBlockType.Factory(BlockChooser.fromPrimaryTheme(PrimaryTheme::masonry));
    DoubleBlockTemplateBlockType.Factory DOOR = new DoubleBlockTemplateBlockType.Factory(BlockChooser.fromSecondaryTheme(SecondaryTheme::door));

    static void register(final RegisterEvent.RegisterHelper<MapCodec<? extends TemplateBlockType>> registry) {
        registry.register(DungeonCrawl.locate("block"), FixedTemplateBlockType.CODEC);
        registry.register(DungeonCrawl.locate("button"), BUTTON.getCodec());
        registry.register(DungeonCrawl.locate("door"), DOOR.getCodec());
        registry.register(DungeonCrawl.locate("fence"), FENCE.getCodec());
        registry.register(DungeonCrawl.locate("fence_gate"), FENCE_GATE.getCodec());
        registry.register(DungeonCrawl.locate("fencing"), FENCING.getCodec());
        registry.register(DungeonCrawl.locate("floor"), FLOOR.getCodec());
        registry.register(DungeonCrawl.locate("fluid"), FLUID.getCodec());
        registry.register(DungeonCrawl.locate("masonry"), MASONRY.getCodec());
        registry.register(DungeonCrawl.locate("masonry_pillar"), MASONRY_PILLAR.getCodec());
        registry.register(DungeonCrawl.locate("masonry_stairs"), MASONRY_STAIRS.getCodec());
        registry.register(DungeonCrawl.locate("masonry_slab"), MASONRY_SLAB.getCodec());
        registry.register(DungeonCrawl.locate("material"), MATERIAL.getCodec());
        registry.register(DungeonCrawl.locate("material_pillar"), MATERIAL_PILLAR.getCodec());
        registry.register(DungeonCrawl.locate("material_slab"), MATERIAL_SLAB.getCodec());
        registry.register(DungeonCrawl.locate("material_stairs"), MATERIAL_STAIRS.getCodec());
        registry.register(DungeonCrawl.locate("pressure_plate"), PRESSURE_PLATE.getCodec());
        registry.register(DungeonCrawl.locate("trapdoor"), TRAPDOOR.getCodec());
        registry.register(DungeonCrawl.locate("wall"), WALL.getCodec());
    }

    MapCodec<? extends TemplateBlockType> codec();

    /**
     * Performs the operation of placing a block state that was derived from this type.
     * May result in multiple or no blocks being placed depending on type-specific logic.
     * <p>
     * <b>Note:</b> This method is unsafe, no bounding box checks are made.
     * It is assumed that the {@code position} parameter is within valid bounds.
     * Perform adequate checks beforehand.
     *
     * @param level    the level to place the block in.
     * @param position the position to place the block at.
     * @param state    the block state to place.
     * @param isSolid  whether this block state replaces air blocks or not.
     */
    default void handlePlacement(LevelAccessor level, BlockPos position, BlockState state, boolean isSolid) {
        WorldEditor.Unsafe.placeBlock(level, position, state, isSolid, true, true);
    }
}
