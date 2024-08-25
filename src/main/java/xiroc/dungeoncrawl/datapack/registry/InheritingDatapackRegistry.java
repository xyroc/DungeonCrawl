package xiroc.dungeoncrawl.datapack.registry;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.lang3.mutable.MutableObject;
import xiroc.dungeoncrawl.datapack.DatapackDirectories;
import xiroc.dungeoncrawl.exception.DatapackLoadException;

import javax.annotation.Nullable;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InheritingDatapackRegistry<T, B extends InheritingBuilder<T, B>> extends DatapackRegistry<T> {
    private final Function<Reader, B> parser;

    InheritingDatapackRegistry(final DatapackDirectories.Directory directory,
                               final Consumer<BiConsumer<ResourceLocation, T>> builtin,
                               final Function<Reader, B> parser) {
        super(directory, builtin, (reader) -> {
            throw new UnsupportedOperationException();
        });

        this.parser = parser;
    }

    @Override
    public void reload(ResourceManager resourceManager) {
        final List<InheritanceTree<T, B>> trees = new ArrayList<>();
        final HashMap<ResourceLocation, Node<T, B>> nodes = new HashMap<>();
        final ImmutableMap.Builder<ResourceLocation, T> builder = ImmutableMap.builder();

        resourceManager.listResources(directory.path(), path -> path.endsWith(FILE_ENDING)).forEach(resource -> {
            try {
                B valueBuilder = resourceManager.getResources(resource).stream()
                        .map(res -> parser.apply(new InputStreamReader(res.getInputStream())))
                        .reduce((parent, current) -> InheritingBuilder.inheritOrReplace(current, parent))
                        .orElseThrow();

                ResourceLocation key = directory.key(resource, FILE_ENDING);
                final Node<T, B> node = nodes.computeIfAbsent(key, Node::new);
                node.builder.setValue(valueBuilder);

                if (valueBuilder.parent() != null) {
                    nodes.computeIfAbsent(valueBuilder.parent(), Node::new).children().add(node);
                } else {
                    trees.add(new InheritanceTree<>(node, (k, value) -> {
                        builder.put(k, value);
                        // Remove any node that is part of a tree.
                        nodes.remove(k);
                    }));
                }

            } catch (Exception exception) {
                throw new DatapackLoadException("Failed to load " + resource.toString() + ": " + exception.getMessage());
            }
        });

        trees.forEach(InheritanceTree::process);

        // If there are any nodes left in the map, there is a cycle and/or a reference to a nonexistent entry.
        while (!nodes.isEmpty()) {
            nodes.entrySet().stream().findAny().ifPresent(entry -> {
                final TreeInspector<T, B> treeInspector = new TreeInspector<>(nodes);
                treeInspector.inspect(entry.getValue());
            });
        }

        values = builder.build();
        isUnloaded = false;

        unresolvedReferences.forEach((key, reference) -> reference.resolve(this));
        unresolvedReferences.clear();
    }

    private record InheritanceTree<T, B extends InheritingBuilder<T, B>>(Node<T, B> root, BiConsumer<ResourceLocation, T> collector) {
        private void process() {
            processRecursively(root, null);
        }

        private void processRecursively(Node<T, B> node, @Nullable B parent) {
            // Cannot be null because the node was recognized as part of a tree
            final B builder = node.builder.getValue();

            if (parent != null) {
                builder.inherit(parent);
            }

            final T value = builder.build();
            collector.accept(node.key, value);

            for (final var child : node.children) {
                processRecursively(child, builder);
            }
        }
    }

    private record Node<T, B extends InheritingBuilder<T, B>>(ResourceLocation key, MutableObject<B> builder, List<Node<T, B>> children) {
        private Node(ResourceLocation key) {
            this(key, new MutableObject<>(null), new ArrayList<>());
        }
    }

    private record TreeInspector<T, B extends InheritingBuilder<T, B>>(Stack<ResourceLocation> stack, Set<ResourceLocation> onStack, Map<ResourceLocation, Node<T, B>> nodes) {
        public TreeInspector(Map<ResourceLocation, Node<T, B>> nodes) {
            this(new Stack<>(), new HashSet<>(), nodes);
        }

        private void inspect(Node<T, B> node) {
            nodes.remove(node.key);

            if (node.builder.getValue() == null) {
                // Not a cycle, but a nonexistent entry
                final var children = node.children.stream().map(child -> child.key.toString()).collect(Collectors.joining(","));
                throw new DatapackLoadException("Nonexistent entry " + node.key + " is inherited from by " + children);
            }

            stack.push(node.key);

            if (onStack.contains(node.key)) {
                final var cycleStart = stack.indexOf(node.key);
                final var cycleEnd = stack.lastIndexOf(node.key);
                final var cycle = stack.subList(cycleStart, cycleEnd + 1).stream().map(ResourceLocation::toString).collect(Collectors.joining("->"));
                throw new DatapackLoadException("Inheritance cycle detected: " + cycle);
            }

            onStack.add(node.key);

            for (final var child : node.children) {
                inspect(child);
            }

            stack.pop();
            onStack.remove(node.key);
        }
    }
}
