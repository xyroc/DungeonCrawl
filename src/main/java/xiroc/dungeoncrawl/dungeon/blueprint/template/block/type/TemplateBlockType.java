package xiroc.dungeoncrawl.dungeon.blueprint.template.block.type;

import com.google.common.collect.ImmutableBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.BlockChooser;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import java.util.function.Function;

public interface TemplateBlockType extends BlockChooser {
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


    /**
     * Names for all template block type codecs.
     */
    interface Names {
        String BLOCK = "block";
        String MASONRY = "masonry";
        String MASONRY_STAIRS = "masonry_stairs";
        String MASONRY_SLAB = "masonry_slab";
        String MASONRY_PILLAR = "masonry_pillar";
        String FENCING = "fencing";
        String FLOOR = "floor";
        String FLUID = "fluid";
        String WALL = "wall";
        String MATERIAL = "material";
        String MATERIAL_STAIRS = "material_stairs";
        String MATERIAL_SLAB = "material_slab";
        String MATERIAL_PILLAR = "material_pillar";
        String BUTTON = "button";
        String DOOR = "door";
        String PRESSURE_PLATE = "pressure_plate";
        String TRAPDOOR = "trapdoor";
        String FENCE = "fence";
        String FENCE_GATE = "fence_gate";
    }

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

    ImmutableBiMap<String, MapCodec<? extends TemplateBlockType>> CODECS = ImmutableBiMap.<String, MapCodec<? extends TemplateBlockType>>builder()
            .put(Names.BLOCK, FixedTemplateBlockType.CODEC)
            .put(Names.MASONRY, MASONRY.getCodec())
            .put(Names.MASONRY_STAIRS, MASONRY_STAIRS.getCodec())
            .put(Names.MASONRY_SLAB, MASONRY_SLAB.getCodec())
            .put(Names.MASONRY_PILLAR, MASONRY_PILLAR.getCodec())
            .put(Names.FENCING, FENCING.getCodec())
            .put(Names.FLOOR, FLOOR.getCodec())
            .put(Names.FLUID, FLUID.getCodec())
            .put(Names.WALL, WALL.getCodec())
            .put(Names.MATERIAL, MATERIAL.getCodec())
            .put(Names.MATERIAL_STAIRS, MATERIAL_STAIRS.getCodec())
            .put(Names.MATERIAL_SLAB, MATERIAL_SLAB.getCodec())
            .put(Names.MATERIAL_PILLAR, MATERIAL_PILLAR.getCodec())
            .put(Names.BUTTON, BUTTON.getCodec())
            .put(Names.DOOR, DOOR.getCodec())
            .put(Names.PRESSURE_PLATE, PRESSURE_PLATE.getCodec())
            .put(Names.TRAPDOOR, TRAPDOOR.getCodec())
            .put(Names.FENCE, FENCE.getCodec())
            .put(Names.FENCE_GATE, FENCE_GATE.getCodec())
            .build();

    // Codec^2
    Codec<MapCodec<? extends TemplateBlockType>> CODEC_CODEC = Codec.STRING.flatXmap(name -> {
        var codecs = CODECS;
        if (codecs.containsKey(name)) {
            return DataResult.success(codecs.get(name));
        } else {
            return DataResult.error(() -> "Unknown template block type: " + name);
        }
    }, type -> {
        var codecs = CODECS;
        if (codecs.containsValue(type)) {
            return DataResult.success(codecs.inverse().get(type));
        } else {
            return DataResult.error(() -> "Unknown template block type: " + type);
        }
    });

    Codec<TemplateBlockType> CODEC = CODEC_CODEC.dispatch(TemplateBlockType::codec, Function.identity());
}
