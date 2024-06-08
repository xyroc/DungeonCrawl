package xiroc.dungeoncrawl.dungeon.generator.level;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.datapack.DatapackRegistries;
import xiroc.dungeoncrawl.datapack.delegate.Delegate;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintMultipart;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.generator.ClusterNodeBuilder;
import xiroc.dungeoncrawl.dungeon.generator.StaircaseBuilder;
import xiroc.dungeoncrawl.dungeon.generator.element.CorridorElement;
import xiroc.dungeoncrawl.dungeon.generator.element.DungeonElement;
import xiroc.dungeoncrawl.dungeon.generator.element.NodeElement;
import xiroc.dungeoncrawl.dungeon.generator.plan.DungeonPlan;
import xiroc.dungeoncrawl.dungeon.piece.CompoundPiece;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.theme.BuiltinThemes;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.dungeon.type.LevelType;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LevelGenerator {
    public final LevelType levelType;
    public final DungeonPlan plan;
    public final int startHeight;
    public final int stage;
    public final Random random;

    private final List<NodeElement> nodes = new ArrayList<>();
    private final List<CorridorElement> corridors = new ArrayList<>();

    private NodeElement start = null;
    private NodeElement end = null;
    private boolean placeEndStaircase;
    private int clusterNodesLeft;

    public LevelGenerator(LevelType levelType, DungeonPlan plan, int startHeight, int stage, Random random) {
        this.levelType = levelType;
        this.plan = plan;
        this.startHeight = startHeight;
        this.stage = stage;
        this.random = random;
        this.clusterNodesLeft = levelType.clusterRooms() != null ? levelType.settings().maxClusterNodes : 0;
        this.placeEndStaircase = stage < 4; // TODO
    }

    public void generateLevel(StaircaseBuilder staircaseBuilder) {
        if (createStart(staircaseBuilder)) {
            return;
        }

        final int maxNodes = levelType.settings().maxRooms;
        for (int index = 0; index < this.nodes.size() && this.nodes.size() < maxNodes; ++index) {
            this.nodes.get(index).update(this);
        }
    }

    private boolean createStart(StaircaseBuilder staircaseBuilder) {
        final int minSeparation = 10;

        Delegate<Blueprint> roomDelegate = levelType.lowerStaircaseRooms().roll(random);
        Blueprint room = roomDelegate.get();
        ImmutableList<Anchor> anchors = room.anchors().get(BuiltinAnchorTypes.STAIRCASE);
        if (anchors == null) {
            return true;
        }
        Anchor anchor = anchors.get(random.nextInt(anchors.size()));
        if (anchor.direction() != Direction.UP) {
            return true;
        }

        int downwards = Math.max(room.ySpan() - anchor.position().getY(), minSeparation);

        BlockPos start = staircaseBuilder.atY(startHeight);
        Rotation rotation = Rotation.getRandom(random);
        BlockPos offset = CoordinateSpace.rotate(anchor.position(), rotation, room.xSpan(), room.zSpan());

        BlockPos roomPos = new BlockPos(
                start.getX() - offset.getX(),
                start.getY() - offset.getY() - downwards,
                start.getZ() - offset.getZ()
        );

        BoundingBoxBuilder boundingBox = room.boundingBox(rotation);
        boundingBox.move(roomPos);
        if (!plan.isFree(boundingBox)) {
            return true;
        }

        DungeonPiece piece = assemblePiece(room, roomPos, rotation);
        if (piece == null) {
            return true;
        }

        staircaseBuilder.bottom(offset, boundingBox.minY, boundingBox.maxY);

        NodeElement staircase = new NodeElement(piece, 0);
        plan.add(staircase);
        this.start = staircase;
        this.nodes.add(staircase);
        return false;
    }

    public DungeonPiece assemblePiece(Blueprint blueprint, BlockPos position, Rotation rotation) {
        ImmutableList<BlueprintMultipart> parts = blueprint.parts();
        PrimaryTheme primaryTheme = DatapackRegistries.PRIMARY_THEME.get(BuiltinThemes.DEFAULT);
        SecondaryTheme secondaryTheme = DatapackRegistries.SECONDARY_THEME.get(BuiltinThemes.DEFAULT);
        if (parts.isEmpty()) {
            return new DungeonPiece(blueprint, position, rotation, primaryTheme, secondaryTheme, stage, random);
        }
        CompoundPiece piece = new CompoundPiece(blueprint, position, rotation, primaryTheme, secondaryTheme, stage, random);
        for (BlueprintMultipart part : parts) {
            if (!part.addParts(plan, piece, random)) {
                return null;
            }
        }
        piece.createBoundingBox();
        return piece;
    }

    public NodeElement createNode(DungeonPiece piece, int depth, boolean isActive, boolean isEndStaircase) {
        NodeElement node = new NodeElement(piece, depth);
        this.plan.add(node);
        if (isActive) {
            this.nodes.add(node);
        }
        if (isEndStaircase) {
            placeEndStaircase = false;
            end = node;
        }
        return node;
    }

    public CorridorElement createCorridor(DungeonElement from, DungeonElement to, BlockPos start, Direction direction, BoundingBox boundingBox) {
        CorridorElement corridor = new CorridorElement(this, from, to, start, direction, boundingBox);
        this.plan.add(corridor);
        this.corridors.add(corridor);
        return corridor;
    }

    public static BoundingBox tunnelBoundingBox(Vec3i start, Vec3i end, Direction direction, int size) {
        Vec3i from = start.relative(direction.getCounterClockWise(), size);
        Vec3i to = end.relative(direction.getClockWise(), size);
        return BoundingBox.fromCorners(from, to);
    }

    public boolean shouldPlaceEndStaircase(int depth) {
        return placeEndStaircase && depth >= levelType.settings().minStaircaseDepth;
    }

    public boolean createClusterNode(Anchor attachmentPoint, Random random, int depth) {
        if (clusterNodesLeft == 0) {
            return false;
        }
        ClusterNodeBuilder clusterNodeBuilder = new ClusterNodeBuilder(this, attachmentPoint, random, depth + 1);
        if (clusterNodeBuilder.build()) {
            --clusterNodesLeft;
            return true;
        }
        return false;
    }

    public NodeElement start() {
        return this.start;
    }

    public NodeElement end() {
        return this.end;
    }
}
