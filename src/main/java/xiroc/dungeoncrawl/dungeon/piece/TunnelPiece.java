package xiroc.dungeoncrawl.dungeon.piece;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.init.ModStructurePieceTypes;
import xiroc.dungeoncrawl.util.StorageHelper;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import java.util.Random;

public class TunnelPiece extends BaseDungeonPiece {
    private static final String NBT_KEY_DIRECTION = "Direction";
    private static final String NBT_KEY_SIZE = "Size";

    private final Direction direction;
    private final int length;
    private final int width; // the amount of blocks the walls are from the middle of the tunnel, the actual width in blocks is 2*width+1
    private final int height;

    public TunnelPiece(BlockPos start, Direction direction, int length, int width, int height, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme) {
        super(ModStructurePieceTypes.TUNNEL, null, start, Rotation.NONE, primaryTheme, secondaryTheme);
        this.direction = direction;
        this.length = length;
        this.width = width;
        this.height = height;
        makeBoundingBox();
    }

    public TunnelPiece(CompoundTag nbt) {
        super(ModStructurePieceTypes.TUNNEL, nbt);
        this.direction = Direction.from3DDataValue(nbt.getInt(NBT_KEY_DIRECTION));
        Vec3i size = StorageHelper.decode(nbt.get(NBT_KEY_SIZE), Vec3i.CODEC);
        this.length = size.getX();
        this.width = size.getY();
        this.height = size.getZ();
        makeBoundingBox();
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
        super.addAdditionalSaveData(context, nbt);
        nbt.putInt(NBT_KEY_DIRECTION, direction.get3DDataValue());
        Vec3i size = new Vec3i(this.length, this.width, this.height);
        nbt.put(NBT_KEY_SIZE, StorageHelper.encode(size, Vec3i.CODEC));
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureFeatureManager p_73428_, ChunkGenerator chunkGenerator, Random random, BoundingBox worldGenBounds, ChunkPos p_73432_, BlockPos pos) {
        BlockPos corner = position.relative(direction.getCounterClockWise(), width);

        WorldEditor.fill(level, primaryTheme.floor(), corner.relative(direction.getClockWise()), corner.relative(direction.getClockWise(), 2 * width - 1)
                .relative(direction, length - 1), worldGenBounds, random, true, true);

        WorldEditor.fill(level, primaryTheme.masonry(), corner, corner.relative(direction, length - 1).above(height - 1), worldGenBounds, random, true, true);

        WorldEditor.fill(level, primaryTheme.masonry(), corner.relative(direction.getClockWise(), 2 * width),
                corner.relative(direction.getClockWise(), 2 * width).relative(direction, length - 1).above(height - 1),
                worldGenBounds, random, true, true);

        WorldEditor.fill(level, primaryTheme.masonry(), corner.relative(direction.getClockWise()).above(height - 1),
                corner.relative(direction.getClockWise(), 2 * width - 1).above(height - 1).relative(direction, length - 1), worldGenBounds, random, true, true);

        WorldEditor.fill(level, new SingleBlock(Blocks.CAVE_AIR.defaultBlockState()), corner.above().relative(direction.getClockWise()),
                corner.above(height - 2).relative(direction, length - 1).relative(direction.getClockWise(), 2 * width - 1), worldGenBounds, random, false, true);
    }

    private void makeBoundingBox() {
        BlockPos corner1 = position.relative(direction.getCounterClockWise(), width);
        BlockPos corner2 = position.relative(direction.getClockWise(), width)
                .relative(direction, length - 1)
                .relative(Direction.UP, height - 1);
        this.boundingBox = BoundingBox.fromCorners(corner1, corner2);
    }
}
