package xiroc.dungeoncrawl.dungeon.generator.element;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.component.BlueprintComponent;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.component.TunnelComponent;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.dungeon.piece.BlueprintPiece;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.dungeon.type.level.CorridorStyle;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.Orientation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
        final Rotation rotation = Orientation.horizontalRotation(Direction.EAST, direction);
        int remaining = length() - fragmentationStart;
        BlockPos.MutableBlockPos pos = start.mutable().move(direction, fragmentationStart);

        int currentSegment = 0;
        final int segments = style.segments().size();

        while (remaining >= FRAGMENT_LENGTH) {
            remaining -= FRAGMENT_LENGTH;

            final Delegate<Blueprint> segmentDelegate = style.segments().get(currentSegment).roll(levelGenerator.random);
            Blueprint segment = segmentDelegate.get();
            int halfWidth = segment.zSpan() / 2;

            BlockPos offset = CoordinateSpace.rotate(Vec3i.ZERO, rotation, segment.xSpan(), segment.zSpan());
            BlockPos position = pos.offset(-offset.getX(), 0, -offset.getZ()).relative(direction.getCounterClockWise(), halfWidth);

            BlueprintPiece corridor = levelGenerator.assemblePiece(segmentDelegate, position, rotation);
            if (corridor != null) {
                fragments.add(new Fragment(corridor));
            } else {
                additionalComponents.add(new TunnelComponent(pos, direction, FRAGMENT_LENGTH, 5, 2));
            }
            pos.move(direction, FRAGMENT_LENGTH);

            currentSegment = (currentSegment + 1) % segments;
        }
    }

    private void addSideSegments() {
        for (Fragment fragment : this.fragments) {
            CoordinateSpace coordinateSpace = fragment.piece.base.blueprint().get().coordinateSpace(fragment.piece.base.position());
            for (Anchor attachmentPoint : fragment.unusedJunctures) {
                Delegate<Blueprint> segmentDelegate = style.sideSegments().roll(levelGenerator.random);
                Blueprint segment = segmentDelegate.get();
                ImmutableList<Anchor> junctures = segment.anchors().get(BuiltinAnchorTypes.JUNCTURE);
                if (junctures == null || junctures.isEmpty()) {
                    continue;
                }
                Anchor actual = coordinateSpace.rotateAndTranslateToOrigin(attachmentPoint, fragment.piece.base.rotation());
                Anchor juncture = junctures.get(levelGenerator.random.nextInt(junctures.size()));
                Rotation rotation = Orientation.horizontalRotation(juncture.direction(), actual.direction().getOpposite());
                BlockPos pos = juncture.latchOnto(actual, segment.coordinateSpace(BlockPos.ZERO));
                // TODO: check for collision, add wall component if everything else collides
                fragment.piece.addComponent(new BlueprintComponent(segmentDelegate, pos, rotation));
            }
        }
    }

    @Override
    public void createPieces(Consumer<StructurePiece> consumer, Random random) {
        addSideSegments();
        fragments.forEach(fragment -> consumer.accept(fragment.piece));

        Delegate<PrimaryTheme> primaryTheme = levelGenerator.primaryTheme;
        Delegate<SecondaryTheme> secondaryTheme = levelGenerator.secondaryTheme;

        final int stage = levelGenerator.stage;
        if (fragmentationStart > 0) {
            consumer.accept(new DungeonPiece(new TunnelComponent(start, direction, fragmentationStart, 5, 2), primaryTheme, secondaryTheme, stage));
        }
        final int remaining = (length() - fragmentationStart) % FRAGMENT_LENGTH;
        if (remaining > 0) {
            consumer.accept(new DungeonPiece(new TunnelComponent(start.relative(direction, length() - remaining), direction, remaining, 5, 2), primaryTheme, secondaryTheme, stage));
        }

        if (!additionalComponents.isEmpty()) {
            consumer.accept(DungeonPiece.withComponents(additionalComponents, primaryTheme, secondaryTheme, stage));
        }
    }

    private static class Fragment {
        public final BlueprintPiece piece;
        public final List<Anchor> unusedJunctures;

        public Fragment(BlueprintPiece piece) {
            this.piece = piece;
            var junctures = piece.base.blueprint().get().anchors().get(BuiltinAnchorTypes.JUNCTURE);
            this.unusedJunctures = junctures != null ? new ArrayList<>(junctures) : List.of();
        }
    }
}
