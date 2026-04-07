package xiroc.dungeoncrawl.dungeon.generator.element;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintMultipart;
import xiroc.dungeoncrawl.dungeon.blueprint.Entrance;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.component.EntranceComponent;
import xiroc.dungeoncrawl.dungeon.generator.level.GeneratorContext;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.dungeon.piece.BlueprintPiece;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.util.collections.iterators.IteratorSequence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class NodeElement extends DungeonElement {
    private final BlueprintPiece piece;
    private final GeneratorContext context;
    private final CoordinateSpace coordinateSpace;

    public final int depth;

    public final EntranceChooser normalEntrances;
    public final EntranceChooser clusterEntrances;

    public NodeElement(BlueprintPiece piece, GeneratorContext context, int depth) {
        this(piece, context, depth, null, null);
    }

    /**
     * Instantiates a new node element.
     *
     * @param piece            The blueprint piece with the main blueprint of the node.
     * @param context          The generator context.
     * @param depth            The generation depth of the node.
     * @param normalEntrances  The normal entrances to use for this node.
     * @param clusterEntrances The cluster entrances to use for this node.
     */
    public NodeElement(BlueprintPiece piece, GeneratorContext context, int depth, @Nullable EntranceChooser normalEntrances, @Nullable EntranceChooser clusterEntrances) {
        super(piece.getBoundingBox());
        this.piece = piece;
        this.context = context;
        final Blueprint blueprint = piece.base.blueprint().value();
        this.coordinateSpace = blueprint.coordinateSpace(piece.base.position());
        this.depth = depth;
        if (normalEntrances != null && clusterEntrances != null) {
            this.normalEntrances = normalEntrances;
            this.clusterEntrances = clusterEntrances;
        } else if (normalEntrances == null && clusterEntrances == null) {
            final var hybridEntrances = new ArrayList<>(blueprint.hybridEntrances());
            this.normalEntrances = new EntranceChooser(new ArrayList<>(blueprint.entrances()), hybridEntrances);
            this.clusterEntrances = new EntranceChooser(new ArrayList<>(blueprint.clusterEntrances()), hybridEntrances);
        } else if (normalEntrances != null) {
            this.normalEntrances = normalEntrances;
            this.clusterEntrances = new EntranceChooser(new ArrayList<>(blueprint.clusterEntrances()), normalEntrances.hybridEntrances);
        } else {
            this.normalEntrances = new EntranceChooser(new ArrayList<>(blueprint.entrances()), clusterEntrances.hybridEntrances);
            this.clusterEntrances = clusterEntrances;
        }
    }

    /**
     * Searches for unused entrances of nearby adjacent nodes and attempts to connect to them with a corridor.
     */
    public void connectToAdjacentNodes() {
        if (normalEntrances.isEmpty()) {
            return;
        }

        final List<FreeEntranceElement> nearbyUnusedEntrances = Lists.newArrayList();
        context.dungeonPlan().forEachIn(this, element -> {
            if (element instanceof FreeEntranceElement freeEntranceElement && freeEntranceElement.isValid()) {
                nearbyUnusedEntrances.add(freeEntranceElement);
            }
        });

        if (nearbyUnusedEntrances.isEmpty()) {
            return;
        }

        for (FreeEntranceElement marker : nearbyUnusedEntrances) {
            for (int entranceIndex = 0; entranceIndex < normalEntrances.size(); entranceIndex++) {
                final Entrance entrance = normalEntrances.get(entranceIndex);
                final Anchor entrancePlacement = coordinateSpace.rotateAndTranslateToOrigin(entrance.placement(), piece.base.rotation());
                // Check alignment.
                if (entrancePlacement.direction() != marker.placement.direction().getOpposite()) {
                    continue;
                }
                final boolean aligned = switch (entrancePlacement.direction().getAxis()) {
                    case X -> entrancePlacement.position().getZ() == marker.placement.position().getZ();
                    case Z -> entrancePlacement.position().getX() == marker.placement.position().getX();
                    case Y -> false; // Vertical connections not allowed.
                };
                if (!aligned) {
                    continue;
                }
                final int distance = switch (entrancePlacement.direction().getAxis()) {
                    case X -> Math.abs(entrancePlacement.position().getX() - marker.placement.position().getX()) - 1;
                    case Z -> Math.abs(entrancePlacement.position().getZ() - marker.placement.position().getZ()) - 1;
                    default -> 0; // Unreachable.
                };
                final BlockPos corridorStart = entrancePlacement.position().relative(entrancePlacement.direction());
                final BoundingBoxBuilder corridorBox = BoundingBoxBuilder.tunnel(corridorStart, entrancePlacement.direction(), distance, 8, 2);
                if (!context.dungeonPlan().isFree(corridorBox)) {
                    continue;
                }
                context.levelGenerator().createCorridor(corridorStart, entrancePlacement.direction(), corridorBox.create());
                addEntrance(entrancePlacement, entrance);
                marker.node.addEntrance(marker.placement, marker.entrance);
                marker.node.normalEntrances.remove(marker.entrance);
                marker.invalidate();
                normalEntrances.remove(entranceIndex);
                break; // Free entrance claimed. Continue with the next free entrance marker.
            }
        }
    }

    @Nullable
    public static NodeElement attachRoomWithCorridor(GeneratorContext context, Anchor attachmentPoint, Holder<Blueprint> room, int depth) {
        final LevelGenerator levelGenerator = context.levelGenerator();

        final BlockPos corridorStart = attachmentPoint.position().relative(attachmentPoint.direction());
        final Direction corridorDirection = attachmentPoint.direction();
        final int corridorLength = levelGenerator.levelType.settings().corridorLength.nextInt(levelGenerator.random);

        final BoundingBoxBuilder corridorBox = corridorLength > 0
                ? BoundingBoxBuilder.tunnel(corridorStart, corridorDirection, corridorLength, 8, 2)
                : null;

        if (corridorBox != null && !context.dungeonPlan().isFree(corridorBox)) {
            return null;
        }

        final Anchor corridorEnd = new Anchor(corridorStart.relative(corridorDirection, corridorLength - 1), corridorDirection);
        NodeElement node = attachRoom(context, corridorEnd, room, depth);
        if (node == null) {
            return null;
        }

        if (corridorBox != null) {
            levelGenerator.createCorridor(corridorStart, corridorDirection, corridorBox.create());
        }
        return node;
    }

    @Nullable
    public static NodeElement attachRoom(GeneratorContext context, Anchor attachmentPoint, Holder<Blueprint> room, int depth) {
        return attachRoom(context, attachmentPoint, room, depth, false);
    }

    @Nullable
    public static NodeElement attachRoom(GeneratorContext context, Anchor attachmentPoint, Holder<Blueprint> room, int depth, boolean useClusterEntrances) {
        final EntranceChooser entrances = useClusterEntrances ? EntranceChooser.clusterEntrances(room.value()) : EntranceChooser.normalEntrances(room.value());
        if (entrances.isEmpty()) {
            return null;
        }
        final LevelGenerator levelGenerator = context.levelGenerator();
        final int chosenEntrance = entrances.chooseEntrance(levelGenerator.random);
        final Entrance entrance = entrances.get(chosenEntrance);
        final var roomPlacement = entrance.placement().latchOnto(attachmentPoint, room.value());
        final BlockPos roomPosition = roomPlacement.getFirst();
        final Rotation roomRotation = roomPlacement.getSecond();
        final BoundingBoxBuilder roomBox = room.value().boundingBox(roomRotation).move(roomPosition);

        if (!context.dungeonPlan().isFree(roomBox)) {
            return null;
        }

        final BlueprintPiece roomPiece = levelGenerator.assemblePiece(room, roomPosition, roomRotation);
        if (roomPiece == null) {
            return null;
        }

        final NodeElement node = new NodeElement(roomPiece, context, depth, useClusterEntrances ? null : entrances, useClusterEntrances ? entrances : null);
        context.dungeonPlan().add(node);
        entrances.remove(chosenEntrance);
        final Anchor rotatedEntrance = room.value().coordinateSpace(roomPosition).rotateAndTranslateToOrigin(entrance.placement(), roomRotation);
        node.addEntrance(rotatedEntrance, entrance);
        return node;
    }

    public void addEntrance(Anchor placement, Entrance entrance) {
        EntranceComponent placedEntrance = entrance.place(placement);
        if (placedEntrance != null) {
            piece.addComponent(placedEntrance);
        }
        entrance.customParts().ifPresent(parts -> BlueprintMultipart.addPart(placement.opposite(), parts.open(), piece, piece.base, context.levelGenerator()));
    }

    private void closeOffEntrances(Iterable<Entrance> entrances) {
        for (Entrance entrance : entrances) {
            if (entrance.customParts().isEmpty()) {
                continue;
            }
            Anchor position = coordinateSpace.rotateAndTranslateToOrigin(entrance.placement(), piece.base.rotation());
            BlueprintMultipart.addPart(position.opposite(), entrance.customParts().get().closed(), piece, piece.base, context.levelGenerator());
        }
    }

    @Override
    public void createPieces(Consumer<StructurePiece> consumer) {
        closeOffEntrances(normalEntrances);
        // Clear hybrid entrances since they were already handled through the normal entrances.
        clusterEntrances.hybridEntrances.clear();
        closeOffEntrances(clusterEntrances);
        piece.updateBoundingBox();
        consumer.accept(piece);
    }

    public BlueprintPiece piece() {
        return this.piece;
    }

    public record EntranceChooser(List<Entrance> entrances,
                                  List<Entrance> hybridEntrances) implements Iterable<Entrance> {
        public static EntranceChooser normalEntrances(Blueprint blueprint) {
            return new EntranceChooser(new ArrayList<>(blueprint.entrances()), new ArrayList<>(blueprint.hybridEntrances()));
        }

        public static EntranceChooser clusterEntrances(Blueprint blueprint) {
            return new EntranceChooser(new ArrayList<>(blueprint.clusterEntrances()), new ArrayList<>(blueprint.hybridEntrances()));
        }

        public boolean isEmpty() {
            return entrances.isEmpty() && hybridEntrances.isEmpty();
        }

        public int size() {
            return entrances.size() + hybridEntrances.size();
        }

        /**
         * Returns the index of a random entrance in the pool.
         *
         * @param random The source of randomness to use.
         * @return The random index.
         */
        public int chooseEntrance(RandomSource random) {
            return random.nextInt(size());
        }

        /**
         * Returns the entrance at the specified index.
         *
         * @param index The index of the entrance to retrieve.
         * @return The entrance.
         * @throws IndexOutOfBoundsException if the index is out of range.
         */
        public Entrance get(int index) {
            if (index >= entrances.size()) {
                return hybridEntrances.get(index - entrances.size());
            }
            return entrances.get(index);
        }

        /**
         * Removes the entrance at the specified index from the pool.
         *
         * @param index The index of the entrance to remove.
         * @return The removed entrance.
         * @throws IndexOutOfBoundsException if the index is out of range.
         */
        public Entrance remove(int index) {
            if (index >= entrances.size()) {
                return hybridEntrances.remove(index - entrances.size());
            }
            return entrances.remove(index);
        }

        /**
         * Removes an entrance from the pool.
         *
         * @param entrance The entrance to remove.
         * @return {@code true} if the entrance was in the pool.
         */
        public boolean remove(Entrance entrance) {
            if (entrances.remove(entrance)) {
                return true;
            }
            return hybridEntrances.remove(entrance);
        }

        /**
         * Removes all remaining entrances.
         */
        public void clear() {
            entrances.clear();
            hybridEntrances.clear();
        }

        @Override
        public @NotNull Iterator<Entrance> iterator() {
            return new IteratorSequence<>(List.of(entrances, hybridEntrances));
        }
    }
}
