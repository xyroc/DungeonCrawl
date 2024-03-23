package xiroc.dungeoncrawl.dungeon.piece;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.init.ModStructurePieceTypes;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CompoundPiece extends DungeonPiece {
    private static final String NBT_KEY_SEGMENTS = "Segments";

    private final List<Segment> segments;

    public CompoundPiece(Blueprint blueprint, BlockPos position, Rotation rotation, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        super(ModStructurePieceTypes.COMPOUND, blueprint, position, rotation, primaryTheme, secondaryTheme, stage);
        this.segments = new ArrayList<>();
    }

    public CompoundPiece(CompoundTag nbt) {
        super(ModStructurePieceTypes.COMPOUND, nbt);
        ListTag segments = nbt.getList(NBT_KEY_SEGMENTS, Tag.TAG_COMPOUND);
        ImmutableList.Builder<Segment> builder = ImmutableList.builder();
        for (int i = 0; i < segments.size(); ++i) {
            builder.add(Segment.read(segments.getCompound(i)));
        }
        this.segments = builder.build();
        createBoundingBox();
    }

    public void addSegment(Segment segment) {
        this.segments.add(segment);
    }

    @Override
    public void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
        super.addAdditionalSaveData(context, nbt);
        ListTag nbtSegments = new ListTag();
        this.segments.forEach((segment) -> nbtSegments.add(segment.write()));
        nbt.put(NBT_KEY_SEGMENTS, nbtSegments);
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureFeatureManager p_73428_, ChunkGenerator chunkGenerator, Random random, BoundingBox worldGenBounds, ChunkPos p_73432_, BlockPos pos) {
        this.blueprint.build(level, this.position, this.rotation, worldGenBounds, random, this.primaryTheme, this.secondaryTheme, this.stage);
        this.segments.forEach((segment) ->
                segment.blueprint().build(level, segment.position(), segment.rotation(), worldGenBounds, random, this.primaryTheme, this.secondaryTheme, this.stage));
        placeEntrances(level, worldGenBounds, random);
    }

    @Override
    public void createBoundingBox() {
        BoundingBoxBuilder boxBuilder = this.blueprint.boundingBox(this.rotation);
        boxBuilder.move(this.position);
        if (this.segments != null) { // the segments list is null during execution of the super class constructor
            this.segments.forEach((segment) -> {
                BoundingBoxBuilder box = segment.blueprint().boundingBox(segment.rotation());
                box.move(segment.position());
                boxBuilder.encapsulate(box);
            });
        }
        this.boundingBox = boxBuilder.create();
    }
}
