package xiroc.dungeoncrawl.datapack.registry;

import com.google.common.collect.ImmutableMap;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.fml.loading.toposort.CyclePresentException;
import net.minecraftforge.fml.loading.toposort.TopologicalSort;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.datapack.DatapackDirectory;
import xiroc.dungeoncrawl.exception.DatapackLoadException;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InheritingDatapackRegistry<T, B extends InheritingBuilder<T, B>> extends DatapackRegistry<T> {
    private final Function<Reader, B> parser;

    InheritingDatapackRegistry(final DatapackDirectory directory,
                               final Consumer<BiConsumer<ResourceLocation, T>> builtin,
                               final Function<Reader, B> parser) {
        super(directory, builtin, (reader) -> {
            throw new UnsupportedOperationException();
        });

        this.parser = parser;
    }

    @Override
    public void reload(ResourceManager resourceManager) {
        final HashMap<ResourceLocation, Node> nodes = new HashMap<>();
        final MutableGraph<Node> dependencyGraph = GraphBuilder.directed().allowsSelfLoops(false).build();

        for (ResourceLocation resource : resourceManager.listResources(directory.path(), path -> path.endsWith(FILE_ENDING))) {
            try {
                B valueBuilder = resourceManager.getResources(resource).stream()
                        .map(res -> parser.apply(new InputStreamReader(res.getInputStream())))
                        .reduce((parent, current) -> InheritingBuilder.inheritOrReplace(current, parent))
                        .orElseThrow();

                ResourceLocation key = directory.key(resource, FILE_ENDING);
                final Node node = nodes.computeIfAbsent(key, Node::new);
                node.builder = valueBuilder;

                dependencyGraph.addNode(node);

                for (ResourceLocation parentKey : valueBuilder.getParents()) {
                    Node parentNode = nodes.computeIfAbsent(parentKey, Node::new);
                    try {
                        dependencyGraph.putEdge(parentNode, node);
                    } catch (IllegalArgumentException e) {
                        // Re-throw with a friendlier message.
                        throw new DatapackLoadException("Node " + key + " inherits from itself, which is not allowed.");
                    }
                }

            } catch (Exception exception) {
                throw new DatapackLoadException("Failed to load " + resource.toString() + ": " + exception.getMessage());
            }
        }

        final ImmutableMap.Builder<ResourceLocation, T> registryBuilder = ImmutableMap.builder();

        try {
            final List<Node> nodesInTopologicalOrder = TopologicalSort.topologicalSort(dependencyGraph, null);
            for (Node node : nodesInTopologicalOrder) {
                B builder = node.builder;
                if (builder == null) {
                    final var children = dependencyGraph.successors(node).stream().map(child -> child.key.toString() + " (" + child.key.toString().length() + ")").collect(Collectors.joining(","));
                    throw new DatapackLoadException("Nonexistent entry " + node.key + " is inherited from by " + children);
                }

                for (ResourceLocation parentKey : node.builder.getParents()) {
                    // Builder is nonnull due to the topological order.
                    builder.inherit(nodes.get(parentKey).builder);
                }

                registryBuilder.put(node.key, builder.build());
            }

        } catch (CyclePresentException exception) {
            reportCycles(exception);
        }

        values = registryBuilder.build();
        isUnloaded = false;

        for (Delegate<T> reference : unresolvedReferences.values()) {
            reference.resolve(this);
        }
        unresolvedReferences.clear();
    }

    private class Node {
        private final ResourceLocation key;
        @Nullable
        private B builder;

        public Node(ResourceLocation key, @Nullable B builder) {
            this.builder = builder;
            this.key = key;
        }

        private Node(ResourceLocation key) {
            this(key, null);
        }
    }

    /**
     * Throws an exception that lists all cycles.
     * @param exception The CyclePresentException holding the set of cycles to report.
     */
    private void reportCycles(CyclePresentException exception) {
        Set<Set<Node>> cycles = exception.getCycles();

        StringBuilder errorMessage = new StringBuilder("Inheritance Cycle(s) detected:").append("\n");
        for (Set<Node> cycle : cycles) {
            for (Node node : cycle) {
                errorMessage.append(node.key);
                errorMessage.append("->");
            }
            errorMessage.append(cycle.iterator().next().key);
            errorMessage.append("\n");
        }

        throw new DatapackLoadException(errorMessage.toString());
    }
}
