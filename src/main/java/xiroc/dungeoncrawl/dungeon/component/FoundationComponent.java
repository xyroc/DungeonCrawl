package xiroc.dungeoncrawl.dungeon.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.TemplateBlockColumn;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.util.storage.GlobalCodecs;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

public class FoundationComponent implements DungeonComponent {
    private static final int PILLAR_WIDTH = 2;
    private static final int PILLAR_SPACING = 3;
    private static final int PILLAR_TILE_WIDTH = PILLAR_WIDTH + PILLAR_SPACING;
    private static final int HALF_PILLAR_SPACING = Math.ceilDiv(PILLAR_SPACING, 2);
    private static final int HALF_PILLAR_TILE_WIDTH = Math.ceilDiv(PILLAR_TILE_WIDTH, 2);
    private static final int DECORATION_SPACING = 8;

    private static int pillarStart(int coordinate, int center) {
        int mod = Math.floorMod(coordinate - center + HALF_PILLAR_TILE_WIDTH, PILLAR_TILE_WIDTH);
        if (mod < PILLAR_WIDTH) {
            return coordinate - mod;
        }
        return coordinate - mod + PILLAR_TILE_WIDTH;
    }

    private static int pillarEnd(int coordinate, int center) {
        int mod = Math.floorMod(coordinate - center + HALF_PILLAR_TILE_WIDTH, PILLAR_TILE_WIDTH);
        return coordinate - mod + PILLAR_WIDTH - 1;
    }

    private static int start(int coordinate, int center) {
        // Include one extra block of room to allow for decorative stairs on all sides of the pillars.
        return pillarStart(coordinate, center) - 1;
    }

    private static int end(int coordinate, int center) {
        // Include one extra block of room to allow for decorative stairs on all sides of the pillars.
        return pillarEnd(coordinate, center) + 1;
    }

    public static final MapCodec<FoundationComponent> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            BoundingBox.CODEC.fieldOf("bounds").forGetter(c -> c.boundingBox),
            GlobalCodecs.INT_ARRAY.fieldOf("pillars").forGetter(c -> c.pillars),
            Codec.INT.fieldOf("gridSizeX").forGetter(c -> c.pillarGridSizeX),
            Codec.INT.fieldOf("gridSizeZ").forGetter(c -> c.pillarGridSizeZ),
            Codec.INT.fieldOf("interconnectStart").forGetter(c -> c.interconnectStartHeight)
    ).apply(builder, FoundationComponent::new));

    private final BoundingBox boundingBox;
    /**
     * Holds the downwards offset from the foundation height for each pillar. Only strictly positive values cause a pillar to generate.
     */
    private final int[] pillars;
    /**
     * The size of the pillar grid along the X-axis.
     */
    private final int pillarGridSizeX;
    /**
     * The size of the pillar grid along the Z-axis.
     */
    private final int pillarGridSizeZ;

    private int interconnectStartHeight = Integer.MAX_VALUE;

    public FoundationComponent(BoundingBox pieceBounds, Iterable<DungeonComponent> components, DungeonWorldGenContext worldGenContext) {
        BlockPos pieceCenter = pieceBounds.getCenter();

        this.boundingBox = new BoundingBox(
                start(pieceBounds.minX(), pieceCenter.getX()), pieceBounds.minY() - 2, start(pieceBounds.minZ(), pieceCenter.getZ()),
                end(pieceBounds.maxX(), pieceCenter.getX()), pieceBounds.maxY(), end(pieceBounds.maxZ(), pieceCenter.getZ())
        );

        int pillarStartX = pillarStart(pieceBounds.minX(), pieceCenter.getX());
        int pillarStartZ = pillarStart(pieceBounds.minZ(), pieceCenter.getZ());
        int pillarEndX = pillarEnd(pieceBounds.maxX(), pieceCenter.getX());
        int pillarEndZ = pillarEnd(pieceBounds.maxZ(), pieceCenter.getZ());

        this.pillarGridSizeX = Math.ceilDiv(pillarEndX - pillarStartX, PILLAR_TILE_WIDTH);
        this.pillarGridSizeZ = Math.ceilDiv(pillarEndZ - pillarStartZ, PILLAR_TILE_WIDTH);

        this.pillars = new int[pillarGridSizeX * pillarGridSizeZ];

        for (DungeonComponent component : components) {
            if (component instanceof BlueprintComponent blueprintComponent) {
                planPillars(blueprintComponent, worldGenContext, pillarStartX, pillarStartZ);
            }
        }
    }

    private FoundationComponent(BoundingBox boundingBox, int[] pillars, int pillarGridSizeX, int pillarGridSizeZ, int interconnectStartHeight) {
        this.boundingBox = boundingBox;
        this.pillars = pillars;
        this.pillarGridSizeX = pillarGridSizeX;
        this.pillarGridSizeZ = pillarGridSizeZ;
        this.interconnectStartHeight = interconnectStartHeight;
    }

    /**
     * Adds supporting pillars for the given blueprint component. The blueprint component should be within the bounding box of the foundation component this is called on.
     * May be called for multiple blueprint components to plan pillars for each of them.
     *
     * @param component           The blueprint component to add pillars for.
     * @param worldGenContext     The world gen context. Needed for the foundation height.
     * @param overallPillarStartX The x-coordinate in the world where the foundation grid starts.
     * @param overallPillarStartZ The z-coordinate in the world where the foundation grid starts.
     */
    private void planPillars(BlueprintComponent component, DungeonWorldGenContext worldGenContext, int overallPillarStartX, int overallPillarStartZ) {
        BoundingBoxBuilder bounds = component.boundingBox();
        Blueprint blueprint = component.blueprint().value();
        BlockPos blueprintPos = component.position();

        Rotation inverseRotation = Orientation.inverseRotation(component.rotation());
        // To invert the rotation of a position within the blueprint, we need the rotated span along the x and z axis.
        int rotatedBlueprintSpanX;
        int rotatedBlueprintSpanZ;
        switch (component.rotation()) {
            case NONE, CLOCKWISE_180 -> {
                rotatedBlueprintSpanX = blueprint.xSpan();
                rotatedBlueprintSpanZ = blueprint.zSpan();
            }
            case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> {
                rotatedBlueprintSpanX = blueprint.zSpan();
                rotatedBlueprintSpanZ = blueprint.xSpan();
            }
            default -> throw new IllegalStateException("Unexpected rotation: " + component.rotation());
        }

        // The center of the blueprint component.
        int centerX = (bounds.minX() + bounds.maxX()) / 2;
        int centerZ = (bounds.minZ() + bounds.maxZ()) / 2;

        // The start and end coordinates of the local pillar grid within the world.
        int pillarStartX = pillarStart(bounds.minX(), centerX);
        int pillarStartZ = pillarStart(bounds.minZ(), centerZ);
        int pillarEndX = pillarEnd(bounds.maxX(), centerX);
        int pillarEndZ = pillarEnd(bounds.maxZ(), centerZ);

        // The size of the local pillar grid for the blueprint component.
        int localPillarGridSizeX = Math.ceilDiv(pillarEndX - pillarStartX, PILLAR_TILE_WIDTH);
        int localPillarGridSizeZ = Math.ceilDiv(pillarEndZ - pillarStartZ, PILLAR_TILE_WIDTH);

        // The offset of the local grid within the overall grid of the dungeon piece.
        int pillarGridOffsetX = (pillarStartX - overallPillarStartX) / PILLAR_TILE_WIDTH;
        int pillarGridOffsetZ = (pillarStartZ - overallPillarStartZ) / PILLAR_TILE_WIDTH;

        // Iterate over all possible pillars in the local grid to determine whether to generate a pillar and, if so, at which height.
        for (int localPillarGridX = 0; localPillarGridX < localPillarGridSizeX; ++localPillarGridX) {
            for (int localPillarGridZ = 0; localPillarGridZ < localPillarGridSizeZ; ++localPillarGridZ) {

                for (int x = 0; x < PILLAR_WIDTH; ++x) {
                    for (int z = 0; z < PILLAR_WIDTH; ++z) {

                        int pillarWorldX = pillarStartX + localPillarGridX * PILLAR_TILE_WIDTH + x;
                        int pillarWorldZ = pillarStartZ + localPillarGridZ * PILLAR_TILE_WIDTH + z;

                        if (pillarWorldX < bounds.minX() || pillarWorldX > bounds.maxX() || pillarWorldZ < bounds.minZ() || pillarWorldZ > bounds.maxZ()) {
                            continue;
                        }

                        int posXWithinRotatedBlueprint = pillarWorldX - blueprintPos.getX();
                        int posZWithinRotatedBlueprint = pillarWorldZ - blueprintPos.getZ();

                        // Invert the rotation to find the position of the corresponding template block column in the blueprint.
                        Vec3i posWithinBlueprint = CoordinateSpace.rotate(posXWithinRotatedBlueprint, 0, posZWithinRotatedBlueprint, inverseRotation, rotatedBlueprintSpanX, rotatedBlueprintSpanZ);

                        // Check if the pillar would be supporting anything and find the correct height to generate it at.
                        TemplateBlockColumn column = blueprint.columnAt(posWithinBlueprint.getX(), posWithinBlueprint.getZ());
                        if (column != null) {
                            int pillarGridX = pillarGridOffsetX + localPillarGridX;
                            int pillarGridZ = pillarGridOffsetZ + localPillarGridZ;

                            int pillarIndex = pillarGridX + this.pillarGridSizeX * pillarGridZ;

                            int foundationStart = column.lowestY() + blueprintPos.getY() - 1;
                            this.interconnectStartHeight = Math.min(this.interconnectStartHeight, foundationStart);

                            int downwardsOffset = worldGenContext.foundationHeight() - foundationStart;

                            this.pillars[pillarIndex] = Math.max(this.pillars[pillarIndex], downwardsOffset);
                        }

                    }
                }

            }
        }
    }

    @Override
    public void generate(LevelAccessor level, BoundingBox worldGenBounds, RandomSource random, DungeonWorldGenContext worldGenContext) {
        if (!worldGenBounds.intersects(this.boundingBox)) {
            return;
        }

        BoundingBox workingArea = new BoundingBox(
                Math.max(this.boundingBox.minX(), worldGenBounds.minX()),
                worldGenBounds.minY(),
                Math.max(this.boundingBox.minZ(), worldGenBounds.minZ()),
                Math.min(this.boundingBox.maxX(), worldGenBounds.maxX()),
                worldGenBounds.maxY(),
                Math.min(this.boundingBox.maxZ(), worldGenBounds.maxZ())
        );

        BlockStateProvider palette = worldGenContext.primaryTheme().value().masonry();
        BlockStateProvider stairs = worldGenContext.primaryTheme().value().stairs();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int pillarGridX = 0; pillarGridX < this.pillarGridSizeX; ++pillarGridX) {
            for (int pillarGridZ = 0; pillarGridZ < this.pillarGridSizeZ; ++pillarGridZ) {
                final int pillarIndex = pillarGridX + this.pillarGridSizeX * pillarGridZ;

                final int downwardsOffset = this.pillars[pillarIndex];
                if (downwardsOffset <= 0) {
                    continue;
                }

                final int height = worldGenContext.foundationHeight() - downwardsOffset + 1;
                final int tileX = boundingBox.minX() + 1 + pillarGridX * PILLAR_TILE_WIDTH;
                final int tileZ = boundingBox.minZ() + 1 + pillarGridZ * PILLAR_TILE_WIDTH;

                int maxPillarHeight = 0;
                for (int x = 0; x < PILLAR_WIDTH; x++) {
                    for (int z = 0; z < PILLAR_WIDTH; z++) {
                        pos.set(tileX + x, height - 1, tileZ + z);
                        if (!workingArea.isInside(pos)) {
                            continue;
                        }

                        int downwards = WorldEditor.Unsafe.countEmptyBlocksDownwards(level, pos) - 1;
                        maxPillarHeight = Math.max(maxPillarHeight, downwards);
                        WorldEditor.fill(level, palette, palette, pos, pos.below(downwards), workingArea, random, false);
                    }
                }

                final int interconnectHeight = height - 1;

                decoratePillar(level, workingArea, random, tileX, tileZ, interconnectHeight, pos, palette, stairs);

                int pillarBottom = height - maxPillarHeight;
                for (int decorationHeight = this.interconnectStartHeight - DECORATION_SPACING; decorationHeight - pillarBottom >= DECORATION_SPACING / 2; decorationHeight -= DECORATION_SPACING) {
                    decoratePillar(level, workingArea, random, tileX, tileZ, decorationHeight, pos, palette, stairs);
                }
            }
        }
    }

    private void decoratePillar(LevelAccessor level, BoundingBox workingArea, RandomSource random, int tileX, int tileZ, int height, BlockPos.MutableBlockPos cursor, BlockStateProvider pillar, BlockStateProvider stairs) {
        int stairsHeight = height - 1;
        for (int x = 0; x < PILLAR_WIDTH; x++) {
            for (int z = 0; z < HALF_PILLAR_SPACING; z++) {
                cursor.set(tileX + x, height, tileZ + PILLAR_WIDTH + z);
                WorldEditor.placeBlock(level, pillar, cursor, workingArea, random, true, false, false);
                cursor.set(tileX + x, height, tileZ - z - 1);
                WorldEditor.placeBlock(level, pillar, cursor, workingArea, random, true, false, false);
            }

            cursor.set(tileX + x, stairsHeight, tileZ + PILLAR_WIDTH);
            WorldEditor.placeBlock(level, stairs.get(cursor, random)
                            .trySetValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                            .trySetValue(BlockStateProperties.HALF, Half.TOP),
                    cursor, workingArea, true, false, true);

            // Switch to the opposite side of the pillar
            cursor.move(0, 0, -PILLAR_WIDTH - 1);
            WorldEditor.placeBlock(level, stairs.get(cursor, random)
                            .trySetValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
                            .trySetValue(BlockStateProperties.HALF, Half.TOP),
                    cursor, workingArea, true, false, true);

        }

        for (int z = 0; z < PILLAR_WIDTH; z++) {
            for (int x = 0; x < HALF_PILLAR_SPACING; x++) {
                cursor.set(tileX + PILLAR_WIDTH + x, height, tileZ + z);
                WorldEditor.placeBlock(level, pillar, cursor, workingArea, random, true, false, false);
                cursor.set(tileX - x - 1, height, tileZ + z);
                WorldEditor.placeBlock(level, pillar, cursor, workingArea, random, true, false, false);
            }

            cursor.set(tileX + PILLAR_WIDTH, stairsHeight, tileZ + z);
            WorldEditor.placeBlock(level, stairs.get(cursor, random)
                            .trySetValue(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)
                            .trySetValue(BlockStateProperties.HALF, Half.TOP),
                    cursor, workingArea, true, false, true);

            // Switch to the opposite side of the pillar
            cursor.move(-PILLAR_WIDTH - 1, 0, 0);
            WorldEditor.placeBlock(level, stairs.get(cursor, random)
                            .trySetValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)
                            .trySetValue(BlockStateProperties.HALF, Half.TOP),
                    cursor, workingArea, true, false, true);

        }
    }

    @Override
    public BoundingBoxBuilder boundingBox() {
        return new BoundingBoxBuilder(this.boundingBox);
    }

    @Override
    public MapCodec<? extends DungeonComponent> codec() {
        return CODEC;
    }
}
