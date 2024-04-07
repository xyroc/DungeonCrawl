package xiroc.dungeoncrawl.dungeon.generator.level;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintMultipart;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprints;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.blueprint.builtin.BuiltinBlueprints;
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
import xiroc.dungeoncrawl.dungeon.theme.Themes;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LevelGenerator {
    public final DungeonPlan plan;
    public final int startHeight;
    public final int stage;
    public final Random random;

    private final List<NodeElement> activeNodes = new ArrayList<>();
    private final List<CorridorElement> corridors = new ArrayList<>();

    private NodeElement start = null;
    private NodeElement end = null;
    private boolean placeEndStaircase;
    private int clusterNodesLeft = 3;

    public LevelGenerator(DungeonPlan plan, int startHeight, int stage, Random random) {
        this.plan = plan;
        this.startHeight = startHeight;
        this.stage = stage;
        this.random = random;
        this.placeEndStaircase = stage < 4; // TODO
    }

    public void generateLevel(StaircaseBuilder staircaseBuilder) {
        if (createStart(startHeight, staircaseBuilder)) {
            return;
        }

        for (int index = 0; index < this.activeNodes.size() && this.activeNodes.size() < 64; ++index) {
            this.activeNodes.get(index).update(this);
        }
    }

    private boolean createStart(int startHeight, StaircaseBuilder staircaseBuilder) {
        final int minSeparation = 10;

        Blueprint blueprint = Blueprints.getBlueprint(BuiltinBlueprints.LOWER_STAIRCASE);
        ImmutableList<Anchor> anchors = blueprint.anchors().get(BuiltinAnchorTypes.STAIRCASE);
        if (anchors == null) {
            return true;
        }
        Anchor anchor = anchors.get(random.nextInt(anchors.size()));
        if (anchor.direction() != Direction.UP) {
            return true;
        }

        int downwards = Math.max(blueprint.ySpan() - anchor.position().getY(), minSeparation);

        BlockPos start = staircaseBuilder.atY(startHeight);
        Rotation rotation = Rotation.getRandom(random);
        BlockPos offset = CoordinateSpace.rotate(anchor.position(), rotation, blueprint.xSpan(), blueprint.zSpan());

        BlockPos roomPos = new BlockPos(
                start.getX() - offset.getX(),
                start.getY() - offset.getY() - downwards,
                start.getZ() - offset.getZ()
        );

        BoundingBoxBuilder boundingBox = blueprint.boundingBox(rotation);
        boundingBox.move(roomPos);
        if (!plan.isFree(boundingBox)) {
            return true;
        }

        DungeonPiece piece = assemblePiece(blueprint, roomPos, rotation);
        if (piece == null) {
            return true;
        }

        staircaseBuilder.bottom(offset, boundingBox.minY, boundingBox.maxY);

        NodeElement staircase = new NodeElement(piece, 0);
        plan.add(staircase);
        this.start = staircase;
        this.activeNodes.add(staircase);
        return false;
    }

    public DungeonPiece assemblePiece(Blueprint blueprint, BlockPos position, Rotation rotation) {
        ImmutableList<BlueprintMultipart> parts = blueprint.parts();
        PrimaryTheme primaryTheme = Themes.getPrimary(BuiltinThemes.DEFAULT);
        SecondaryTheme secondaryTheme = Themes.getSecondary(BuiltinThemes.DEFAULT);
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
            this.activeNodes.add(node);
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
        return placeEndStaircase && depth > 3;
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
