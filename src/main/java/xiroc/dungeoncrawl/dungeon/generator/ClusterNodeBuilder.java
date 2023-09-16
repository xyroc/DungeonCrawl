package xiroc.dungeoncrawl.dungeon.generator;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprints;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.blueprint.builtin.BuiltinBlueprints;
import xiroc.dungeoncrawl.dungeon.generator.element.NodeElement;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxUtils;
import xiroc.dungeoncrawl.util.random.WeightedRandom;

import java.util.ArrayList;
import java.util.Random;

public class ClusterNodeBuilder {
    private static final WeightedRandom<Blueprint> CLUSTER_NODES = new WeightedRandom.Builder<Blueprint>()
            .add(Blueprints.getBlueprint(DungeonCrawl.locate("cluster_test_1")), 2)
            .add(Blueprints.getBlueprint(DungeonCrawl.locate("cluster_test_2")))
            .build();

    private static final int MIN_SIZE = 2;
    private static final int MAX_SIZE = 5;

    private final LevelGenerator levelGenerator;
    private final Anchor start;
    private final Random random;
    private final int depth;

    private final ArrayList<NodeElement> nodes = new ArrayList<>();

    public ClusterNodeBuilder(LevelGenerator levelGenerator, Anchor start, Random random, int depth) {
        this.levelGenerator = levelGenerator;
        this.start = start;
        this.random = random;
        this.depth = depth;
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
            Blueprint room = isEndStaircase ? Blueprints.getBlueprint(BuiltinBlueprints.UPPER_STAIRCASE) :
                    isClusterNode ? CLUSTER_NODES.roll(random) : Blueprints.getBlueprint(BuiltinBlueprints.CORNER_ROOM);
            ImmutableList<Anchor> entrances = room.anchors().get(BuiltinAnchorTypes.ENTRANCE);
            if (entrances == null || entrances.isEmpty()) {
                continue;
            }
            for (int anchorAttempt = 0; anchorAttempt < 4; ++anchorAttempt) {
                final int chosenEntrance = random.nextInt(entrances.size());
                Anchor entrance = entrances.get(chosenEntrance);
                BlockPos pos = entrance.latchOnto(anchor, room.coordinateSpace(BlockPos.ZERO));
                Rotation rotation = Orientation.horizontalRotation(entrance.direction(), anchor.direction().getOpposite());
                BoundingBoxBuilder boundingBoxBuilder = room.boundingBox(rotation);
                boundingBoxBuilder.move(pos);

                if (nodes.stream().noneMatch(node -> node.intersects(boundingBoxBuilder)) && levelGenerator.plan.isFree(boundingBoxBuilder)) {
                    NodeElement node = levelGenerator.createNode(pos, room, rotation, boundingBoxBuilder.create(), nextDepth, !isClusterNode, isEndStaircase);
                    node.piece().stage = levelGenerator.stage;
                    node.unusedEntrances.remove(chosenEntrance);
                    nodes.add(node);
                    return true;
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
            int entrance = random.nextInt(node.unusedEntrances.size());
            Anchor attachmentPoint = node.piece().blueprint.coordinateSpace(node.piece().position).rotateAndTranslateToOrigin(node.unusedEntrances.get(entrance),
                    node.piece().rotation);
            if (attachNode(attachmentPoint, attachClusterNodes)) {
                --placementsLeft;
                node.unusedEntrances.remove(entrance);
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
