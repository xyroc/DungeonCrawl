package xiroc.dungeoncrawl.dungeon.generator.element;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.BlueprintFeature;
import xiroc.dungeoncrawl.dungeon.component.BlueprintComponent;
import xiroc.dungeoncrawl.dungeon.component.CuboidComponent;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.component.TunnelComponent;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.dungeon.piece.BlueprintPiece;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.dungeon.type.SecretRoom;
import xiroc.dungeoncrawl.dungeon.type.level.CorridorStyle;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CorridorElement extends DungeonElement {
    private static final int FRAGMENT_LENGTH = 3;

    private final LevelGenerator levelGenerator;
    private final Direction direction;
    private final BlockPos start;
    private final CorridorStyle style;
    private final int fragmentationStart; // Offset from the start of the corridor at which fragments are beginning to be inserted

    private final List<Fragment> fragments;
    private final List<DungeonComponent> additionalComponents;

    public CorridorElement(LevelGenerator levelGenerator, BlockPos start, Direction direction, BoundingBox boundingBox) {
        super(boundingBox);
        this.levelGenerator = levelGenerator;
        this.start = start;
        this.direction = direction;
        this.style = levelGenerator.levelType.corridorStyles().roll(levelGenerator.random);
        this.fragmentationStart = (length() % FRAGMENT_LENGTH) / 2;
        this.fragments = new ArrayList<>();
        this.additionalComponents = new ArrayList<>(0);
        fragment();
    }

    public int length() {
        return direction.getAxis() == Direction.Axis.X ? boundingBox.getXSpan() : boundingBox.getZSpan();
    }

    private void fragment() {
        int remaining = length() - fragmentationStart;
        final BlockPos.MutableBlockPos pos = start.mutable().move(direction, fragmentationStart);

        int currentSegment = 0;
        final int segments = style.segments().size();

        while (remaining >= FRAGMENT_LENGTH) {
            final Delegate<Blueprint> segmentDelegate = style.segments().get(currentSegment).roll(levelGenerator.random);
            final Blueprint segment = segmentDelegate.get();

            final Anchor attachmentPoint = new Anchor(pos.relative(direction.getOpposite()), direction);
            final List<Anchor> corridorAnchors = segment.anchors().get(BuiltinAnchorTypes.CORRIDOR);

            boolean placementFailed = false;

            if (corridorAnchors == null || corridorAnchors.isEmpty()) {
                DungeonCrawl.LOGGER.warn("Corridor segment blueprint {} does not have any anchors of the type {} and can therefore not generate.", segmentDelegate.key(),
                        BuiltinAnchorTypes.CORRIDOR);
                placementFailed = true;
            }

            if (!placementFailed) {
                final Anchor corridorAnchor = corridorAnchors.get(levelGenerator.random.nextInt(corridorAnchors.size()));
                final var placement = corridorAnchor.latchOnto(attachmentPoint, segmentDelegate.get());
                final BlueprintPiece corridor = levelGenerator.assemblePiece(segmentDelegate, placement.getFirst(), placement.getSecond());
                if (corridor != null) {
                    fragments.add(new Fragment(corridor));
                } else {
                    placementFailed = true;
                }
            }

            if (placementFailed) {
                additionalComponents.add(new TunnelComponent(pos, direction, FRAGMENT_LENGTH, 5, 2));
            }
            pos.move(direction, FRAGMENT_LENGTH);

            remaining -= FRAGMENT_LENGTH;
            currentSegment = (currentSegment + 1) % segments;
        }
    }

    private void addSideSegments() {
        for (Fragment fragment : this.fragments) {
            final Blueprint mainSegment = fragment.piece.base.blueprint().get();
            final CoordinateSpace coordinateSpace = mainSegment.coordinateSpace(fragment.piece.base.position());

            for (Anchor juncture : fragment.unusedJunctures) {
                final Anchor attachmentPoint = coordinateSpace.rotateAndTranslateToOrigin(juncture, fragment.piece.base.rotation());
                if (attachmentPoint.direction().getAxis() != this.direction.getClockWise().getAxis()) {
                    // Side segments must be horizontal and perpendicular to the corridor's direction.
                    continue;
                }
                final BlueprintComponent sideSegment = createSideSegment(style.sideSegments().roll(levelGenerator.random), attachmentPoint);

                if (sideSegment != null) {
                    fragment.piece.addComponent(sideSegment);
                    for (BlueprintFeature feature : sideSegment.blueprint().get().features()) {
                        feature.create(levelGenerator, fragment.piece::addComponent, null, sideSegment.blueprint().get(), sideSegment.position(), sideSegment.rotation());
                    }
                } else {
                    // Side segment could not be placed, close the side off with a wall
                    final BlockPos wallPlacement = attachmentPoint.position().relative(attachmentPoint.direction()).above();
                    final BoundingBoxBuilder mainSegmentBounds = fragment.piece.base.boundingBox();
                    switch (attachmentPoint.direction().getAxis()) {
                        case X -> {
                            final CuboidComponent wall = new CuboidComponent(new BoundingBox(
                                    wallPlacement.getX(),
                                    wallPlacement.getY(),
                                    mainSegmentBounds.minZ,
                                    wallPlacement.getX(),
                                    mainSegmentBounds.maxY - 1,
                                    mainSegmentBounds.maxZ
                            ));
                            fragment.piece.addComponent(wall);
                        }
                        case Z -> {
                            final CuboidComponent wall = new CuboidComponent(new BoundingBox(
                                    mainSegmentBounds.minX,
                                    wallPlacement.getY(),
                                    wallPlacement.getZ(),
                                    mainSegmentBounds.maxX,
                                    mainSegmentBounds.maxY - 1,
                                    wallPlacement.getZ()
                            ));
                            fragment.piece.addComponent(wall);
                        }
                    }
                }
            }
            fragment.unusedJunctures.clear();
            fragment.piece.updateBoundingBox();
        }
    }

    @Nullable
    private BlueprintComponent createSideSegment(Delegate<Blueprint> segment, Anchor attachmentPoint) {
        final ImmutableList<Anchor> junctures = segment.get().anchors().get(BuiltinAnchorTypes.JUNCTURE);
        if (junctures == null || junctures.isEmpty()) {
            return null;
        }

        for (int attempt = 0; attempt < junctures.size(); ++attempt) {
            final int chosenJuncture = levelGenerator.random.nextInt(junctures.size());
            final Anchor juncture = junctures.get(chosenJuncture);
            final var placement = juncture.latchOnto(attachmentPoint, segment.get());
            final BlueprintComponent segmentComponent = new BlueprintComponent(segment, placement.getFirst(), placement.getSecond());

            if (levelGenerator.plan.anyMatch(segmentComponent.boundingBox(), element -> element != this)) {
                // Collision with another element.
                continue;
            }

            return segmentComponent;
        }

        return null;
    }

    public boolean attachSecretRoomWithEntrance(SecretRoom room) {
        final RandomSource random = levelGenerator.random;
        for (int fragmentAttempt = 0; fragmentAttempt < fragments.size(); fragmentAttempt++) {
            final Fragment fragment = fragments.get(random.nextInt(fragments.size()));
            final List<Anchor> junctures = fragment.unusedJunctures;
            final CoordinateSpace coordinateSpace = fragment.piece.base.blueprint().get().coordinateSpace(fragment.piece.base.position());

            for (int junctureAttempt = 0; junctureAttempt < junctures.size(); junctureAttempt++) {
                final int chosenJuncture = random.nextInt(junctures.size());
                final Anchor juncture = junctures.get(chosenJuncture);
                final Anchor attachmentPoint = coordinateSpace.rotateAndTranslateToOrigin(juncture, fragment.piece.base.rotation());

                final BlueprintComponent entranceSegment = attachSecretRoomWithEntrance(room, attachmentPoint);
                if (entranceSegment != null) {
                    fragment.piece.addComponent(entranceSegment);
                    junctures.remove(chosenJuncture);
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    private BlueprintComponent attachSecretRoomWithEntrance(SecretRoom room, Anchor attachmentPoint) {
        final Delegate<Blueprint> entranceBlueprint = room.entrances().roll(levelGenerator.random);

        final BlueprintComponent entranceSegment = createSideSegment(entranceBlueprint, attachmentPoint);
        if (entranceSegment == null) {
            return null;
        }

        final ImmutableList<Anchor> entrances = entranceSegment.blueprint().get().anchors().get(BuiltinAnchorTypes.ENTRANCE);
        if (entrances == null || entrances.isEmpty()) {
            return null;
        }

        final CoordinateSpace coordinateSpace = entranceSegment.blueprint().get().coordinateSpace(entranceSegment.position());
        for (int entranceAttempt = 0; entranceAttempt < entrances.size(); entranceAttempt++) {
            final Anchor entrance = entrances.get(levelGenerator.random.nextInt(entrances.size()));
            final Anchor roomAttachmentPoint = coordinateSpace.rotateAndTranslateToOrigin(entrance, entranceSegment.rotation());

            if (attachSecretRoom(room, roomAttachmentPoint)) {
                return entranceSegment;
            }
        }

        return null;
    }

    private boolean attachSecretRoom(SecretRoom room, Anchor attachmentPoint) {
        final int entranceTunnelLength = 3 + levelGenerator.random.nextInt(5);

        final BlockPos tunnelStart = attachmentPoint.position().relative(attachmentPoint.direction());
        final DungeonComponent tunnelComponent = new TunnelComponent(tunnelStart, attachmentPoint.direction(), entranceTunnelLength, 4, 1);

        if (!levelGenerator.plan.isFree(tunnelComponent.boundingBox())) {
            return false;
        }

        final Anchor roomAttachmentPoint = new Anchor(tunnelStart.relative(attachmentPoint.direction(), entranceTunnelLength - 1), attachmentPoint.direction());
        final Delegate<Blueprint> roomVariant = room.variants().roll(levelGenerator.random);
        final NodeElement node = NodeElement.attachRoom(levelGenerator.generatorContext, roomAttachmentPoint, roomVariant, 0);

        if (node != null) {
            // TODO: create component dungeon element type, instantiate one to hold the tunnel
            node.piece().addComponent(tunnelComponent);
            node.piece().updateBoundingBox();
            return true;
        }

        return false;
    }

    @Override
    public void createPieces(Consumer<StructurePiece> consumer, RandomSource random) {
        addSideSegments();
        fragments.forEach(fragment -> consumer.accept(fragment.piece));

        Delegate<PrimaryTheme> primaryTheme = levelGenerator.primaryTheme;
        Delegate<SecondaryTheme> secondaryTheme = levelGenerator.secondaryTheme;

        final int stage = levelGenerator.stage;
        final DungeonWorldGenContext tunnelGenContext = new DungeonWorldGenContext(primaryTheme, secondaryTheme, start.getY() - 1, stage);

        if (fragmentationStart > 0) {
            consumer.accept(new DungeonPiece(new TunnelComponent(start, direction, fragmentationStart, 5, 2), tunnelGenContext));
        }
        final int remaining = (length() - fragmentationStart) % FRAGMENT_LENGTH;
        if (remaining > 0) {
            consumer.accept(new DungeonPiece(new TunnelComponent(start.relative(direction, length() - remaining), direction, remaining, 5, 2), tunnelGenContext));
        }

        if (!additionalComponents.isEmpty()) {
            consumer.accept(DungeonPiece.withComponents(additionalComponents, tunnelGenContext));
        }
    }

    private static class Fragment {
        public final BlueprintPiece piece;
        public final List<Anchor> unusedJunctures;

        public Fragment(BlueprintPiece piece) {
            this.piece = piece;
            var junctures = piece.base.blueprint().get().anchors().get(BuiltinAnchorTypes.JUNCTURE);
            this.unusedJunctures = junctures != null ? new ArrayList<>(junctures) : new ArrayList<>(0);
        }
    }
}
