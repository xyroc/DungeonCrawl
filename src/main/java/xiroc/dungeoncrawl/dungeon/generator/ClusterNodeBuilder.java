package xiroc.dungeoncrawl.dungeon.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.Entrance;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.component.BlueprintComponent;
import xiroc.dungeoncrawl.dungeon.generator.element.NodeElement;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.dungeon.piece.BlueprintPiece;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxUtils;
import xiroc.dungeoncrawl.util.random.IRandom;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class ClusterNodeBuilder {
    private static final int MIN_SIZE = 2;
    private static final int MAX_SIZE = 5;

    private final LevelGenerator levelGenerator;
    private final Anchor start;
    private final Random random;
    private final int depth;
    private final IRandom<Delegate<Blueprint>> roomSet;

    private final ArrayList<NodeElement> nodes = new ArrayList<>();

    public ClusterNodeBuilder(LevelGenerator levelGenerator, Anchor start, Random random, int depth) {
        this.levelGenerator = levelGenerator;
        this.start = start;
        this.random = random;
        this.depth = depth;
        this.roomSet = Objects.requireNonNull(levelGenerator.levelType.clusterRooms(), "No cluster room blueprints present").roll(random);
    }

    private boolean attachNode(Anchor anchor, boolean isClusterNode) {
        // Minimum required free space to even attempt attaching a node
        BoundingBoxBuilder required = BoundingBoxUtils.centeredBuilder(anchor.position().relative(anchor.direction(), 3), 2, 7);
        if (nodes.stream().anyMatch(node -> node.intersects(required)) || !levelGenerator.plan.isFree(required)) {
            return false;
        }

        final int nextDepth = isClusterNode ? depth : depth + 1;
        final boolean isEndStaircase = !isClusterNode && levelGenerator.shouldPlaceEndStaircase(nextDepth);

        for (int roomAttempt = 0; roomAttempt < 3; ++roomAttempt) {
            final Delegate<Blueprint> room = isEndStaircase ? levelGenerator.levelType.upperStaircaseRooms().roll(random) :
                    isClusterNode ? roomSet.roll(random) : levelGenerator.levelType.rooms().roll(random);
            final var entrances = room.get().entrances();
            if (entrances.isEmpty()) {
                continue;
            }

            for (int entranceAttempt = 0; entranceAttempt < 4; ++entranceAttempt) {
                final int chosenEntrance = random.nextInt(entrances.size());
                final Entrance entrance = entrances.get(chosenEntrance);
                final BlockPos roomPos = entrance.placement().latchOnto(anchor, room.get().coordinateSpace(BlockPos.ZERO));
                final Rotation roomRotation = Orientation.horizontalRotation(entrance.placement().direction(), anchor.direction().getOpposite());
                final BoundingBoxBuilder roomBox = room.get().boundingBox(roomRotation).move(roomPos);

                if (nodes.stream().noneMatch(node -> node.intersects(roomBox)) && levelGenerator.plan.isFree(roomBox)) {
                    final BlueprintPiece piece = levelGenerator.assemblePiece(room, roomPos, roomRotation);
                    if (piece != null && nodes.stream().noneMatch(node -> node.intersects(piece.getBoundingBox()))) {
                        final NodeElement node = levelGenerator.createNode(piece, nextDepth, !isClusterNode, isEndStaircase);
                        node.unusedEntrances.remove(chosenEntrance);
                        if (!isClusterNode) {
                            node.addEntrance(anchor.opposite(), entrance, levelGenerator.random);
                        }
                        nodes.add(node);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void grow(NodeElement node, boolean attachClusterNodes) {
        if (node.unusedEntrances.isEmpty()) {
            return;
        }
        final int maxAttempts = node.unusedEntrances.size() * 2;
        int placementsLeft = Math.min(3, node.unusedEntrances.size());
        for (int attempt = 0; attempt < maxAttempts && placementsLeft > 0; ++attempt) {
            int chosenEntrance = random.nextInt(node.unusedEntrances.size());
            Entrance entrance = node.unusedEntrances.get(chosenEntrance);
            BlueprintComponent base = node.piece().base;
            Anchor attachmentPoint = base.blueprint().get().coordinateSpace(base.position()).rotateAndTranslateToOrigin(entrance.placement(), base.rotation());
            if (attachNode(attachmentPoint, attachClusterNodes)) {
                --placementsLeft;
                node.unusedEntrances.remove(chosenEntrance);
                node.addEntrance(attachmentPoint, entrance, levelGenerator.random);
            }
        }
    }

    public boolean build() {
        if (!attachNode(start, true)) {
            return false;
        }

        for (int i = 0; i < nodes.size() && nodes.size() < MAX_SIZE; ++i) {
            NodeElement node = nodes.get(i);
            if (node.depth > this.depth || node.unusedEntrances.isEmpty()) {
                continue;
            }
            grow(node, true);
        }

        if (nodes.size() < MIN_SIZE) {
            return false;
        }

        int extra = Math.min(3, nodes.size());
        for (int n = 0; n < extra; ++n) {
            NodeElement node = nodes.get(nodes.size() - n - 1);
            grow(node, false);
        }

        nodes.forEach(levelGenerator.plan::add);
        DungeonCrawl.LOGGER.info("Created a cluster node with {} pieces!", nodes.size());
        return true;
    }

}
