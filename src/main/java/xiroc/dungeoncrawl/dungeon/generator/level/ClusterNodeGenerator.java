package xiroc.dungeoncrawl.dungeon.generator.level;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.Entrance;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.component.BlueprintComponent;
import xiroc.dungeoncrawl.dungeon.generator.element.NodeElement;
import xiroc.dungeoncrawl.dungeon.generator.plan.DungeonPlan;
import xiroc.dungeoncrawl.dungeon.generator.plan.HierarchicalPlan;
import xiroc.dungeoncrawl.dungeon.generator.plan.ListPlan;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.random.IRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClusterNodeGenerator {
    /**
     * The initial attachment point.
     */
    private final Anchor start;
    private final RandomSource random;

    /**
     * The depth value which all node elements belonging to the cluster node will share.
     */
    private final int depth;

    /**
     * A dungeon plan holding all nodes making up the cluster node.
     */
    private final DungeonPlan preliminaryPlan;

    /**
     * The generator context specific to this {@link ClusterNodeGenerator}.
     * Holds a dungeon plan derived by combining the preliminary plan with the plan from the upper generator context.
     * All generation logic for the cluster node is done using this context, meaning that collision checks are done first on the preliminary plan
     * and then on the upper context's dungeon plan.
     * This allows us to simulate adding nodes to the real plan without modifying it, meaning that we can easily discard the added nodes afterward in
     * case the generation of the cluster node did not succeed.
     */
    private final GeneratorContext context;

    /**
     * The generator context this {@link ClusterNodeGenerator} is being used in.
     * All nodes making up the cluster node are added to the dungeon plan belonging to this context once generation of the cluster node has
     * concluded successfully.
     * In case the generation did not succeed, the plan is not modified, meaning that the cluster node is effectively discarded.
     */
    private final GeneratorContext upperContext;

    /**
     * The cluster node settings in use.
     */
    private final ClusterNodeSettings settings;

    public ClusterNodeGenerator(GeneratorContext upperContext, Anchor start, RandomSource random, int depth) {
        this.upperContext = upperContext;
        this.start = start;
        this.random = random;
        this.depth = depth;
        this.settings = Objects.requireNonNull(upperContext.levelGenerator().levelType.rooms().cluster(), "No cluster nodes specified").roll(random);

        this.preliminaryPlan = new ListPlan(BoundingBox.infinite());
        // Use a combined plan to allow for collision checks on the preliminary cluster node layout
        final DungeonPlan combinedPlan = new HierarchicalPlan(this.preliminaryPlan, upperContext.dungeonPlan());
        this.context = new GeneratorContext(combinedPlan, upperContext.levelGenerator());
    }

    @Nullable
    private NodeElement attachNode(Anchor anchor, IRandom<Holder<Blueprint>> rooms, boolean useClusterEntrances) {
        for (int roomAttempt = 0; roomAttempt < 3; ++roomAttempt) {
            final Holder<Blueprint> room = rooms.roll(random);
            for (int entranceAttempt = 0; entranceAttempt < 4; ++entranceAttempt) {
                final NodeElement node = NodeElement.attachRoom(this.context, anchor, room, this.depth, useClusterEntrances);
                if (node != null) {
                    return node;
                }
            }
        }
        return null;
    }

    record ClusterNode(NodeElement node, List<ClusterNode> children) {
        public ClusterNode(NodeElement node) {
            this(node, new ArrayList<>(node.clusterEntrances.size()));
        }

        /**
         * Recursively removes this node and all children from the dungeon plan.
         *
         * @param plan The plan to remove the nodes from.
         */
        void delete(DungeonPlan plan) {
            plan.remove(node);
            for (var child : children) {
                child.delete(plan);
            }
        }
    }

    /**
     * Recursively attaches more nodes.
     *
     * @param clusterNode The node to attach new nodes to.
     * @param budget      The maximum number of nodes that may be added by this operation.
     * @param depth       The current recursive depth.
     * @return The number of attached nodes. Will be zero in the case of failure.
     */
    private int growRecursively(ClusterNode clusterNode, int budget, int depth) {
        final NodeElement node = clusterNode.node;
        final NodeElement.EntranceChooser unusedEntrances = node.clusterEntrances;
        if (unusedEntrances.isEmpty()) {
            return 0;
        }
        int totalNodesAdded = 0;
        final BlueprintComponent base = node.piece().base;
        final CoordinateSpace coordinateSpace = base.blueprint().value().coordinateSpace(base.position());

        for (int entrancesLeft = unusedEntrances.size(); entrancesLeft > 0; --entrancesLeft) {
            final Entrance entrance = unusedEntrances.get(entrancesLeft - 1);
            final Anchor attachmentPoint = coordinateSpace.rotateAndTranslateToOrigin(entrance.placement(), base.rotation());
            // Spread the remaining budget evenly across the remaining entrances
            final int budgetForThisEntrance = budget / entrancesLeft;
            final int nodesAdded = growRecursivelyFromAttachmentPoint(clusterNode, attachmentPoint, budgetForThisEntrance, depth);
            if (nodesAdded > 0) {
                totalNodesAdded += nodesAdded;
                budget -= nodesAdded;
                node.addEntrance(attachmentPoint, entrance, true);
            } else {
                clusterNode.delete(preliminaryPlan);
                return 0;
            }
        }
        unusedEntrances.clear();
        return totalNodesAdded;
    }

    /**
     * Recursively attach more nodes to the parent node at the provided attachment point.
     *
     * @param parent          The parent node to attach new nodes to.
     * @param attachmentPoint The attachment point at the parent node.
     * @param budget          The maximum number of nodes that may be added.
     * @param depth           The recursive depth of the operation.
     * @return The number of attached nodes. Will be zero in the case of failure.
     */
    private int growRecursivelyFromAttachmentPoint(ClusterNode parent, Anchor attachmentPoint, int budget, int depth) {
        if (budget <= 0) {
            return 0;
        }
        boolean isTerminal = budget == 1;
        // If there's only one room left in the budget,  try to add a terminal node.
        IRandom<Holder<Blueprint>> rooms = isTerminal ? settings.terminals() : settings.nonTerminals();

        int attemptsLeft = 2;

        while (attemptsLeft > 0) {
            --attemptsLeft;
            if (attemptsLeft == 0) {
                isTerminal = true;
                rooms = settings.terminals();
            }

            final NodeElement newNode = attachNode(attachmentPoint, rooms, isTerminal);

            if (newNode == null) {
                continue;
            }

            final ClusterNode newClusterNode = new ClusterNode(newNode);
            int totalNodesAdded = 1;

            if (!isTerminal) {
                final int nodesAdded = growRecursively(newClusterNode, budget - 1, depth + 1);
                if (nodesAdded == 0) {
                    continue;
                }
                totalNodesAdded += nodesAdded;

            }
            parent.children.add(newClusterNode);
            return totalNodesAdded;
        }

        return 0;
    }

    /**
     * Attempts to generate a cluster node.
     * May or may not succeed depending on how much available space there is.
     * In the case of unsuccessful generation, no changes are made to the dungeon plan.
     *
     * @return true if the generation was successful, false otherwise
     */
    public boolean generateRecursively() {
        NodeElement startNode = attachNode(start, settings.terminals(), false);
        if (startNode == null) {
            return false;
        }
        int roomsAdded = 1 + growRecursively(new ClusterNode(startNode), settings.maxSize() - 1, 0);
        if (roomsAdded < settings.minSize()) {
            return false;
        }
        preliminaryPlan.forEach(this.upperContext.dungeonPlan()::add);
        preliminaryPlan.forEach(element -> {
            if (element instanceof NodeElement node && !node.normalEntrances.isEmpty()) {
                if (random.nextFloat() >= settings.fanOut()) {
                    return;
                }
                this.upperContext.levelGenerator().growNode(node);
            }
        });
        return true;
    }

}
