package xiroc.dungeoncrawl.dungeon.generator.level;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.Entrance;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.component.BlueprintComponent;
import xiroc.dungeoncrawl.dungeon.generator.element.NodeElement;
import xiroc.dungeoncrawl.dungeon.generator.plan.DungeonPlan;
import xiroc.dungeoncrawl.dungeon.generator.plan.HierarchicalPlan;
import xiroc.dungeoncrawl.dungeon.generator.plan.ListPlan;
import xiroc.dungeoncrawl.util.random.IRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClusterNodeGenerator {
    private static final int MIN_SIZE = 2;
    private static final int MAX_SIZE = 5;

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
     * All eligible blueprints for parts of the cluster node.
     */
    private final IRandom<Holder<Blueprint>> roomSet;

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
     * The list of all node elements which haven't yet been considered to attach more nodes to.
     */
    private final List<NodeElement> activeNodes = new ArrayList<>();

    public ClusterNodeGenerator(GeneratorContext upperContext, Anchor start, RandomSource random, int depth) {
        this.upperContext = upperContext;
        this.start = start;
        this.random = random;
        this.depth = depth;
        this.roomSet = Objects.requireNonNull(upperContext.levelGenerator().levelType.rooms().cluster(), "No cluster room blueprints present").roll(random);

        this.preliminaryPlan = new ListPlan(BoundingBox.infinite());
        // Use a combined plan to allow for collision checks on the preliminary cluster node layout
        final DungeonPlan combinedPlan = new HierarchicalPlan(this.preliminaryPlan, upperContext.dungeonPlan());
        this.context = new GeneratorContext(combinedPlan, upperContext.levelGenerator());
    }

    private boolean attachNode(Anchor anchor, boolean isClusterNode) {
        final LevelGenerator levelGenerator = context.levelGenerator();

        final int nextDepth = isClusterNode ? depth : depth + 1;

        for (int roomAttempt = 0; roomAttempt < 3; ++roomAttempt) {
            final Holder<Blueprint> room = isClusterNode ? roomSet.roll(random) : levelGenerator.roomChooser.nextRoom(nextDepth, random);
            final var entrances = room.value().entrances();
            if (entrances.isEmpty()) {
                continue;
            }

            for (int entranceAttempt = 0; entranceAttempt < 4; ++entranceAttempt) {
                final NodeElement node = NodeElement.attachRoom(this.context, anchor, room, nextDepth);
                if (node != null) {
                    if (!isClusterNode) {
                        // Mark the node as active so that the layer generator can use it for further generation
                        levelGenerator.addActiveNode(node);
                        levelGenerator.roomChooser.commit(node);
                    }
                    this.activeNodes.add(node);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Attempts to attach more nodes to the provided node.
     *
     * @param node               the node to attach more nodes to
     * @param attachClusterNodes true if cluster node blueprints should be used, false for normal blueprints
     * @return the number of nodes added
     */
    private int grow(NodeElement node, boolean attachClusterNodes) {
        if (node.unusedEntrances.isEmpty()) {
            return 0;
        }
        final int maxAttempts = node.unusedEntrances.size() * 2;
        final int maxNodesAdded = Math.min(3, node.unusedEntrances.size());
        int nodesAdded = 0;
        for (int attempt = 0; attempt < maxAttempts && nodesAdded < maxNodesAdded; ++attempt) {
            int chosenEntrance = random.nextInt(node.unusedEntrances.size());
            Entrance entrance = node.unusedEntrances.get(chosenEntrance);
            BlueprintComponent base = node.piece().base;
            Anchor attachmentPoint = base.blueprint().value().coordinateSpace(base.position()).rotateAndTranslateToOrigin(entrance.placement(), base.rotation());
            if (attachNode(attachmentPoint, attachClusterNodes)) {
                ++nodesAdded;
                node.unusedEntrances.remove(chosenEntrance);
                node.addEntrance(attachmentPoint, entrance);
            }
        }
        return nodesAdded;
    }

    /**
     * Attempts to generate a cluster node.
     * May or may not succeed depending on how much available space there is.
     * In the case of unsuccessful generation, no changes are made to the dungeon plan.
     *
     * @return true if the generation was successful, false otherwise
     */
    public boolean generate() {
        if (!attachNode(start, true)) {
            return false;
        }

        while (activeNodes.size() < MAX_SIZE && !activeNodes.isEmpty()) {
            final NodeElement node = activeNodes.removeFirst();
            if (node.depth > this.depth) {
                continue;
            }
            grow(node, true);
        }

        if (activeNodes.size() < MIN_SIZE) {
            return false;
        }

        int extraBorderNodes = 3;
        while (extraBorderNodes > 0 && !activeNodes.isEmpty()) {
            final NodeElement node = activeNodes.remove(random.nextInt(activeNodes.size()));
            final int nodesAdded = grow(node, false);
            extraBorderNodes -= nodesAdded;
        }

        // Add all node elements of the cluster node to the dungeon plan of the upper context.
        this.preliminaryPlan.forEach(this.upperContext.dungeonPlan()::add);
        return true;
    }

}
