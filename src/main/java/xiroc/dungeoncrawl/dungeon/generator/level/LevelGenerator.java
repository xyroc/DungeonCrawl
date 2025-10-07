package xiroc.dungeoncrawl.dungeon.generator.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintMultipart;
import xiroc.dungeoncrawl.dungeon.blueprint.Entrance;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.component.BlueprintComponent;
import xiroc.dungeoncrawl.dungeon.generator.element.CorridorElement;
import xiroc.dungeoncrawl.dungeon.generator.element.FreeEntranceElement;
import xiroc.dungeoncrawl.dungeon.generator.element.NodeElement;
import xiroc.dungeoncrawl.dungeon.generator.plan.DungeonPlan;
import xiroc.dungeoncrawl.dungeon.generator.staircase.StaircasePlanner;
import xiroc.dungeoncrawl.dungeon.piece.BlueprintPiece;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.dungeon.type.SecretRoom;
import xiroc.dungeoncrawl.dungeon.type.level.LevelType;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;

import java.util.ArrayList;
import java.util.List;

public class LevelGenerator {
    public final LevelType levelType;
    public final int startHeight;
    public final int stage;
    public final RandomSource random;
    public final Holder<PrimaryTheme> primaryTheme;
    public final Holder<SecondaryTheme> secondaryTheme;
    public final GeneratorContext generatorContext;

    private final DungeonPlan plan;
    protected final RoomChooser roomChooser;
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
                          RandomSource random,
                          Holder<PrimaryTheme> primaryTheme,
                          Holder<SecondaryTheme> secondaryTheme,
                          boolean placeExit,
                          List<SecretRoom> extraSecretRooms) {
        this.levelType = levelType;
        this.plan = plan;
        this.startHeight = startHeight;
        this.stage = stage;
        this.random = random;
        this.primaryTheme = primaryTheme;
        this.secondaryTheme = secondaryTheme;

        this.clusterNodesLeft = levelType.rooms().cluster() != null ? levelType.settings().maxClusterNodes : 0;
        this.generatorContext = new GeneratorContext(plan, this);

        final List<RoomChooser.RoomEntry> additionalSpecialRooms = new ArrayList<>(1);
        if (placeExit) {
            additionalSpecialRooms.add(new RoomChooser.RoomEntry(levelType.rooms().upperStaircase(), 3, 1, this::setEndStaircase));
        }
        this.roomChooser = new RoomChooser(levelType, additionalSpecialRooms, random);

        final List<SecretRoom> secretRooms = new ArrayList<>(extraSecretRooms.size() + levelType.secretRooms().size());
        secretRooms.addAll(extraSecretRooms);
        secretRooms.addAll(levelType.secretRooms());
        this.secretRoomGenerator = new SecretRoomGenerator(secretRooms);
    }

    private boolean createStart(StaircasePlanner staircasePlanner) {
        final Holder<Blueprint> roomHolder = levelType.rooms().lowerStaircase().roll(random);
        final Blueprint room = roomHolder.value();
        final List<Anchor> staircaseAnchors = room.anchors().get(BuiltinAnchorTypes.STAIRCASE);
        if (staircaseAnchors == null) {
            DungeonCrawl.LOGGER.warn("Blueprint {} is used as a lower staircase room but does not have any staircase anchors.", roomHolder.getKey());
            return true;
        }
        final Anchor staircaseAnchor = staircaseAnchors.get(random.nextInt(staircaseAnchors.size()));
        if (staircaseAnchor.direction() == Direction.DOWN) {
            DungeonCrawl.LOGGER.warn("Blueprint {} has a downwards facing staircase anchor but it used as a lower staircase room, which requires an upwards facing staircase",
                    roomHolder.getKey());
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

        final BlueprintPiece piece = assemblePiece(roomHolder, roomPos, rotation);
        if (piece == null) {
            return true;
        }

        staircasePlanner.setBottom(piece, boundingBox.minY + offset.getY());

        final NodeElement staircase = new NodeElement(piece, generatorContext, 0);
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
        node.connectToAdjacentNodes();

        final BlueprintPiece piece = node.piece();
        final CoordinateSpace coordinateSpace = piece.base.blueprint().value().coordinateSpace(piece.base.position());
        final int nextDepth = node.depth + 1;
        int placementsLeft = 1 + (1 + random.nextInt(4)) / 2;
        final List<Entrance> entrances = node.unusedEntrances;

        for (int attempt = 0; !entrances.isEmpty() && attempt < 4 && placementsLeft > 0; ++attempt) {
            final int entranceIndex = random.nextInt(entrances.size());
            final Entrance entrance = entrances.get(entranceIndex);
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
                node.addEntrance(placement, entrance);
                --placementsLeft;
                entrances.remove(entranceIndex);
            }
        }

        // Add marker elements for unused entrances.
        for (Entrance entrance : node.unusedEntrances) {
            final Anchor placement = coordinateSpace.rotateAndTranslateToOrigin(entrance.placement(), piece.base.rotation());
            final BoundingBox boundingBox = BoundingBoxBuilder.fromPosition(placement.position())
                    .resize(placement.direction(), 16)
                    .create();
            plan.add(new FreeEntranceElement(node, entrance, placement, boundingBox));
        }
    }

    @Nullable
    private NodeElement attachRoomWithCorridor(Anchor placement, int depth) {
        for (int attempt = 0; attempt < 3; ++attempt) {
            final Holder<Blueprint> room = roomChooser.nextRoom(depth, random);
            final NodeElement node = NodeElement.attachRoomWithCorridor(generatorContext, placement, room, depth);
            if (node != null) {
                roomChooser.commit(node);
                return node;
            }
        }
        return null;
    }

    @Nullable
    public BlueprintPiece assemblePiece(Holder<Blueprint> blueprint, BlockPos position, Rotation rotation) {
        BlueprintComponent baseComponent = new BlueprintComponent(blueprint, position, rotation);
        BlueprintPiece piece = new BlueprintPiece(baseComponent, new DungeonWorldGenContext(primaryTheme, secondaryTheme, baseComponent.position().getY(), stage));

        blueprint.value().populateFeatures(this, piece.base.position(), piece.base.rotation(), piece::addComponent);

        List<BlueprintMultipart> parts = blueprint.value().parts();
        if (parts.isEmpty()) {
            return piece;
        }

        for (BlueprintMultipart part : parts) {
            if (!part.addParts(piece, baseComponent, this)) {
                return null;
            }
        }
        piece.updateBoundingBox();
        return piece;
    }

    public void createCorridor(BlockPos start, Direction direction, BoundingBox boundingBox) {
        CorridorElement corridor = new CorridorElement(generatorContext, start, direction, boundingBox);
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
