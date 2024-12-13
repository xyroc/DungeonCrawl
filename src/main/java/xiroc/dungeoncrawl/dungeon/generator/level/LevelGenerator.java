package xiroc.dungeoncrawl.dungeon.generator.level;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintMultipart;
import xiroc.dungeoncrawl.dungeon.blueprint.Entrance;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.component.BlueprintComponent;
import xiroc.dungeoncrawl.dungeon.generator.StaircaseBuilder;
import xiroc.dungeoncrawl.dungeon.generator.element.CorridorElement;
import xiroc.dungeoncrawl.dungeon.generator.element.NodeElement;
import xiroc.dungeoncrawl.dungeon.generator.plan.DungeonPlan;
import xiroc.dungeoncrawl.dungeon.piece.BlueprintPiece;
import xiroc.dungeoncrawl.dungeon.theme.BuiltinThemes;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.dungeon.type.LevelType;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LevelGenerator {
    public final LevelType levelType;
    public final DungeonPlan plan;
    public final int startHeight;
    public final int stage;
    public final Random random;
    public final GeneratorContext generatorContext;

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
        this.generatorContext = new GeneratorContext(plan, this);
    }

    private boolean createStart(StaircaseBuilder staircaseBuilder) {
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

        int downwards = Math.max(room.ySpan() - anchor.position().getY(), levelType.settings().minSeparation);

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

        BlueprintPiece piece = assemblePiece(roomDelegate, roomPos, rotation);
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

    public void generateLevel(StaircaseBuilder staircaseBuilder) {
        if (createStart(staircaseBuilder)) {
            return;
        }

        final int maxNodes = levelType.settings().maxRooms;
        for (int index = 0; index < this.nodes.size() && this.nodes.size() < maxNodes; ++index) {
            final NodeElement node = this.nodes.get(index);
            if (node.depth < levelType.settings().maxDepth) {
                growNode(node);
            }
        }
    }

    public void growNode(NodeElement node) {
        final BlueprintPiece piece = node.piece();
        final CoordinateSpace coordinateSpace = piece.base.blueprint().get().coordinateSpace(piece.base.position());
        final int nextDepth = node.depth + 1;
        int maxRooms = 1 + (1 + random.nextInt(4)) / 2;
        for (int attempt = 0; !node.unusedEntrances.isEmpty() && attempt < 4 && maxRooms > 0; ++attempt) {
            final Entrance entrance = node.unusedEntrances.remove(random.nextInt(node.unusedEntrances.size()));
            final Anchor placement = coordinateSpace.rotateAndTranslateToOrigin(entrance.placement(), piece.base.rotation());

            boolean nodeCreated = createClusterNode(placement, nextDepth);
            if (!nodeCreated) {
                NodeElement newNode = NodeElement.attachRoomWithCorridor(this.generatorContext, placement, nextDepth);
                if (newNode != null) {
                    nodeCreated = true;
                    this.nodes.add(newNode);
                }
            }

            if (nodeCreated) {
                node.addEntrance(placement, entrance, random);
                --maxRooms;
            } else {
                entrance.customParts().ifPresent(parts -> BlueprintMultipart.addPart(placement.opposite(), parts.closed(), piece, piece.base, random));
            }
        }
    }

    @Nullable
    public BlueprintPiece assemblePiece(Delegate<Blueprint> blueprint, BlockPos position, Rotation rotation) {
        Delegate<PrimaryTheme> primaryTheme = DatapackRegistries.PRIMARY_THEME.delegateOrThrow(BuiltinThemes.DEFAULT);
        Delegate<SecondaryTheme> secondaryTheme = DatapackRegistries.SECONDARY_THEME.delegateOrThrow(BuiltinThemes.DEFAULT);

        BlueprintComponent baseComponent = new BlueprintComponent(blueprint, position, rotation);
        BlueprintPiece piece = new BlueprintPiece(baseComponent, primaryTheme, secondaryTheme, stage);

        for (var feature : blueprint.get().features()) {
            feature.create(piece::addComponent, null, blueprint.get(), piece.base.position(), piece.base.rotation(), random, stage);
        }

        ImmutableList<BlueprintMultipart> parts = blueprint.get().parts();
        if (parts.isEmpty()) {
            return piece;
        }

        for (BlueprintMultipart part : parts) {
            if (!part.addParts(piece, baseComponent, random)) {
                return null;
            }
        }
        piece.updateBoundingBox();
        return piece;
    }

    public void createCorridor(BlockPos start, Direction direction, BoundingBox boundingBox) {
        CorridorElement corridor = new CorridorElement(this, start, direction, boundingBox);
        this.plan.add(corridor);
        this.corridors.add(corridor);
    }

    public boolean shouldPlaceEndStaircase(int depth) {
        return placeEndStaircase && depth >= levelType.settings().minStaircaseDepth;
    }

    public void setEndStaircase(NodeElement element) {
        this.end = element;
        this.placeEndStaircase = false;
    }

    public void addActiveNode(NodeElement node) {
        this.nodes.add(node);
    }

    public boolean createClusterNode(Anchor attachmentPoint, int depth) {
        if (clusterNodesLeft == 0 || random.nextInt(10) != 0) {
            return false;
        }
        ClusterNodeGenerator clusterNodeGenerator = new ClusterNodeGenerator(generatorContext, attachmentPoint, random, depth + 1);
        if (clusterNodeGenerator.generate()) {
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
