package xiroc.dungeoncrawl.dungeon.generator.element;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import xiroc.dungeoncrawl.datapack.DatapackRegistries;
import xiroc.dungeoncrawl.datapack.delegate.Delegate;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.blueprint.builtin.BuiltinBlueprints;
import xiroc.dungeoncrawl.dungeon.component.BlueprintComponent;
import xiroc.dungeoncrawl.dungeon.component.TunnelComponent;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.dungeon.piece.BlueprintPiece;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.theme.BuiltinThemes;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.Orientation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CorridorElement extends DungeonElement {
    private static final int FRAGMENT_LENGTH = 3;

    private final Direction direction;
    public final DungeonElement from;
    public final DungeonElement to;
    private final BlockPos start;
    private final int fragmentationStart; // Offset from the start of the corridor at which fragments are beginning to be inserted

    private final List<Fragment> fragments;
    private final LevelGenerator levelGenerator;

    public CorridorElement(LevelGenerator levelGenerator, DungeonElement from, DungeonElement to, BlockPos start, Direction direction, BoundingBox boundingBox) {
        super(boundingBox);
        this.from = from;
        this.to = to;
        this.start = start;
        this.direction = direction;
        this.fragmentationStart = (length() % FRAGMENT_LENGTH) / 2;
        this.fragments = new ArrayList<>();
        this.levelGenerator = levelGenerator;
        fragment();
    }

    public int length() {
        return direction.getAxis() == Direction.Axis.X ? boundingBox.getXSpan() : boundingBox.getZSpan();
    }

    private void fragment() {
        final Rotation rotation = Orientation.horizontalRotation(Direction.EAST, direction);
        int remaining = length() - fragmentationStart;
        BlockPos.MutableBlockPos pos = start.mutable().move(direction, fragmentationStart);
        while (remaining >= FRAGMENT_LENGTH) {
            remaining -= FRAGMENT_LENGTH;

            Delegate<Blueprint> segmentDelegate = DatapackRegistries.BLUEPRINT.delegateOrThrow(BuiltinBlueprints.CORRIDOR_BASE_SEGMENT);
            Blueprint segment = segmentDelegate.get();
            int halfWidth = segment.zSpan() / 2;

            BlockPos offset = CoordinateSpace.rotate(Vec3i.ZERO, rotation, segment.xSpan(), segment.zSpan());
            BlockPos position = pos.offset(-offset.getX(), 0, -offset.getZ()).relative(direction.getCounterClockWise(), halfWidth);
            Delegate<PrimaryTheme> primaryTheme = DatapackRegistries.PRIMARY_THEME.delegateOrThrow(BuiltinThemes.DEFAULT);
            Delegate<SecondaryTheme> secondaryTheme = DatapackRegistries.SECONDARY_THEME.delegateOrThrow(BuiltinThemes.DEFAULT);

            BlueprintPiece corridor = new BlueprintPiece(new BlueprintComponent(segmentDelegate, position, rotation), primaryTheme, secondaryTheme, levelGenerator.stage);
            corridor.createBoundingBox();
            fragments.add(new Fragment(corridor));

            pos.move(direction, FRAGMENT_LENGTH);
        }
    }

    private void addSideSegments() {
        for (Fragment fragment : this.fragments) {
            CoordinateSpace coordinateSpace = fragment.piece.base.blueprint().get().coordinateSpace(fragment.piece.base.position());
            for (Anchor attachmentPoint : fragment.unusedJunctures) {
                Delegate<Blueprint> segmentDelegate = DatapackRegistries.BLUEPRINT.delegateOrThrow(BuiltinBlueprints.CORRIDOR_SIDE_SEGMENT);
                Blueprint segment = segmentDelegate.get();
                ImmutableList<Anchor> junctures = segment.anchors().get(BuiltinAnchorTypes.JUNCTURE);
                if (junctures == null) {
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
    public void createPieces(Consumer<StructurePiece> consumer) {
        addSideSegments();
        fragments.forEach(fragment -> consumer.accept(fragment.piece));

        Delegate<PrimaryTheme> primaryTheme = DatapackRegistries.PRIMARY_THEME.delegateOrThrow(BuiltinThemes.DEFAULT);
        Delegate<SecondaryTheme> secondaryTheme = DatapackRegistries.SECONDARY_THEME.delegateOrThrow(BuiltinThemes.DEFAULT);

        int remaining = length() - fragmentationStart;
        if (fragmentationStart > 0) {
            consumer.accept(new DungeonPiece(new TunnelComponent(start, direction, fragmentationStart, 5, 2), primaryTheme, secondaryTheme, 0));
        }
        int r = remaining % FRAGMENT_LENGTH;
        if (r > 0) {
            consumer.accept(new DungeonPiece(new TunnelComponent(start.relative(direction, length() - r), direction, r, 5, 2), primaryTheme, secondaryTheme, 0));
        }
    }

    private static class Fragment {
        public final BlueprintPiece piece;
        public final ArrayList<Anchor> unusedJunctures;

        public Fragment(BlueprintPiece piece) {
            this.piece = piece;
            var junctures = piece.base.blueprint().get().anchors().get(BuiltinAnchorTypes.JUNCTURE);
            this.unusedJunctures = junctures != null ? new ArrayList<>(junctures) : new ArrayList<>(0);
        }
    }
}
