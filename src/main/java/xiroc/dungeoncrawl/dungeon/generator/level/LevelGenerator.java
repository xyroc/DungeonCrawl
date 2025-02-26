package xiroc.dungeoncrawl.dungeon.generator.level;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintMultipart;
import xiroc.dungeoncrawl.dungeon.blueprint.Entrance;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.component.BlueprintComponent;
import xiroc.dungeoncrawl.dungeon.generator.staircase.StaircasePlanner;
import xiroc.dungeoncrawl.dungeon.generator.element.CorridorElement;
import xiroc.dungeoncrawl.dungeon.generator.element.NodeElement;
import xiroc.dungeoncrawl.dungeon.generator.plan.DungeonPlan;
import xiroc.dungeoncrawl.dungeon.piece.BlueprintPiece;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.dungeon.type.SecretRoom;
import xiroc.dungeoncrawl.dungeon.type.level.LevelType;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.Orientation;
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
    public final Delegate<PrimaryTheme> primaryTheme;
    public final Delegate<SecondaryTheme> secondaryTheme;
    public final GeneratorContext generatorContext;
    public final RoomChooser roomChooser;

    private final SecretRoomGenerator secretRoomGenerator;
    private final List<NodeElement> nodes = new ArrayList<>();
    protected final List<CorridorElement> corridors = new ArrayList<>();

    private NodeElement start = null;
    private NodeElement end = null;
    private int clusterNodesLeft;

    public LevelGenerator(LevelType levelType,
                          DungeonPlan plan,
                          int startHeight,
                          int stage,
                          Random random,
                          Delegate<PrimaryTheme> primaryTheme,
                          Delegate<SecondaryTheme> secondaryTheme,
                          boolean placeExit,
                          List<SecretRoom> extraSecretRooms) {
        this.levelType = levelType;
        this.plan = plan;
        this.startHeight = startHeight;
        this.stage = stage;
        this.random = random;
        this.primaryTheme = primaryTheme;
        this.secondaryTheme = secondaryTheme;

        this.clusterNodesLeft = levelType.clusterRooms() != null ? levelType.settings().maxClusterNodes : 0;
        this.generatorContext = new GeneratorContext(plan, this);

        final List<RoomChooser.RoomEntry> additionalSpecialRooms = new ArrayList<>(1);
        if (placeExit) {
            additionalSpecialRooms.add(new RoomChooser.RoomEntry(levelType.upperStaircaseRooms(), 3, 1, this::setEndStaircase));
        }
        this.roomChooser = new RoomChooser(levelType, additionalSpecialRooms, random);

        final List<SecretRoom> secretRooms = new ArrayList<>(extraSecretRooms.size() + levelType.secretRooms().size());
        secretRooms.addAll(extraSecretRooms);
        secretRooms.addAll(levelType.secretRooms());
        this.secretRoomGenerator = new SecretRoomGenerator(secretRooms);
    }

    private boolean createStart(StaircasePlanner staircasePlanner) {
        final Delegate<Blueprint> roomDelegate = levelType.lowerStaircaseRooms().roll(random);
        final Blueprint room = roomDelegate.get();
        final ImmutableList<Anchor> staircaseAnchors = room.anchors().get(BuiltinAnchorTypes.STAIRCASE);
        if (staircaseAnchors == null) {
            DungeonCrawl.LOGGER.warn("Blueprint {} is used as a lower staircase room but does not have any staircase anchors.", roomDelegate.key());
            return true;
        }
        final Anchor staircaseAnchor = staircaseAnchors.get(random.nextInt(staircaseAnchors.size()));
        if (staircaseAnchor.direction() == Direction.DOWN) {
            DungeonCrawl.LOGGER.warn("Blueprint {} has a downwards facing staircase anchor but it used as a lower staircase room, which requires an upwards facing staircase",
                    roomDelegate.key());
            return true;
        }

        final int downwards = Math.max(room.ySpan() - staircaseAnchor.position().getY(), levelType.settings().minSeparation);

        final Direction staircaseConstraint = staircaseAnchor.direction().getAxis().isHorizontal() ? staircaseAnchor.direction() : null;
        final Rotation rotation = staircaseConstraint != null ?
                Orientation.horizontalRotation(staircaseConstraint, staircasePlanner.getFacingAt(startHeight - downwards)) :
                Rotation.getRandom(random);
        final BlockPos offset = CoordinateSpace.rotate(staircaseAnchor.position(), rotation, room.xSpan(), room.zSpan());

        final BlockPos center = staircasePlanner.getCenterAtY(startHeight);
        final BlockPos roomPos = new BlockPos(
                center.getX() - offset.getX(),
                center.getY() - offset.getY() - downwards,
                center.getZ() - offset.getZ()
        );

        final BoundingBoxBuilder boundingBox = room.boundingBox(rotation).move(roomPos);
        if (!plan.isFree(boundingBox)) {
            return true;
        }

        final BlueprintPiece piece = assemblePiece(roomDelegate, roomPos, rotation);
        if (piece == null) {
            return true;
        }

        staircasePlanner.setBottom(boundingBox.minY + offset.getY(), boundingBox.maxY);

        final NodeElement staircase = new NodeElement(piece, 0);
        plan.add(staircase);
        this.start = staircase;
        this.nodes.add(staircase);
        return false;
    }

    public void generateLevel(StaircasePlanner staircasePlanner) {
        if (createStart(staircasePlanner)) {
            return;
        }

        final int maxNodes = levelType.settings().maxRooms;
        for (int index = 0; index < this.nodes.size() && this.nodes.size() < maxNodes; ++index) {
            final NodeElement node = this.nodes.get(index);
            if (node.depth < levelType.settings().maxDepth) {
                growNode(node);
            }
        }

        secretRoomGenerator.generateSecretRooms(this);
    }

    private void growNode(NodeElement node) {
        final BlueprintPiece piece = node.piece();
        final CoordinateSpace coordinateSpace = piece.base.blueprint().get().coordinateSpace(piece.base.position());
        final int nextDepth = node.depth + 1;
        int maxRooms = 1 + (1 + random.nextInt(4)) / 2;
        for (int attempt = 0; !node.unusedEntrances.isEmpty() && attempt < 4 && maxRooms > 0; ++attempt) {
            final Entrance entrance = node.unusedEntrances.remove(random.nextInt(node.unusedEntrances.size()));
            final Anchor placement = coordinateSpace.rotateAndTranslateToOrigin(entrance.placement(), piece.base.rotation());

            boolean nodeCreated = createClusterNode(placement, nextDepth);
            if (!nodeCreated) {
                final NodeElement newNode = attachRoomWithCorridor(placement, nextDepth);
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
    private NodeElement attachRoomWithCorridor(Anchor placement, int depth) {
        for (int attempt = 0; attempt < 3; ++attempt) {
            final Delegate<Blueprint> room = roomChooser.nextRoom(depth, random);
            final NodeElement node = NodeElement.attachRoomWithCorridor(generatorContext, placement, room, depth);
            if (node != null) {
                roomChooser.commit(node);
                return node;
            }
        }
        return null;
    }

    @Nullable
    public BlueprintPiece assemblePiece(Delegate<Blueprint> blueprint, BlockPos position, Rotation rotation) {
        BlueprintComponent baseComponent = new BlueprintComponent(blueprint, position, rotation);
        BlueprintPiece piece = new BlueprintPiece(baseComponent, primaryTheme, secondaryTheme, stage);

        for (var feature : blueprint.get().features()) {
            feature.create(this, piece::addComponent, null, blueprint.get(), piece.base.position(), piece.base.rotation());
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

    public void setEndStaircase(NodeElement element) {
        this.end = element;
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
