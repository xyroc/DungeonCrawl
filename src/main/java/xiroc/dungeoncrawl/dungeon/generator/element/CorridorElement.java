package xiroc.dungeoncrawl.dungeon.generator.element;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprints;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.blueprint.builtin.BuiltinBlueprints;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.dungeon.piece.CompoundPiece;
import xiroc.dungeoncrawl.dungeon.piece.Segment;
import xiroc.dungeoncrawl.dungeon.piece.TestPiece;
import xiroc.dungeoncrawl.dungeon.theme.BuiltinThemes;
import xiroc.dungeoncrawl.dungeon.theme.Themes;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class CorridorElement extends DungeonElement {
    private static final int FRAGMENT_LENGTH = 3;

    private final Direction direction;
    public final DungeonElement from;
    public final DungeonElement to;
    private final BlockPos start;
    private final int fragmentationStart; // Offset from the start of the corridor at which fragments are beginning to be inserted

    private final List<CompoundPiece> fragments;

    public CorridorElement(DungeonElement from, DungeonElement to, BlockPos start, Direction direction, BoundingBox boundingBox) {
        super(boundingBox);
        this.from = from;
        this.to = to;
        this.start = start;
        this.direction = direction;
        this.fragmentationStart = (length() % FRAGMENT_LENGTH) / 2;
        this.fragments = new ArrayList<>();
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

            CompoundPiece corridor = new CompoundPiece();
            corridor.stage = 0;
            corridor.blueprint = Blueprints.getBlueprint(BuiltinBlueprints.CORRIDOR_ARCH_SEGMENT);
            corridor.rotation = rotation;

            int halfWidth = corridor.blueprint.zSpan() / 2;

            BlockPos offset = CoordinateSpace.rotate(Vec3i.ZERO, rotation, corridor.blueprint.xSpan(), corridor.blueprint.zSpan());
            corridor.position = pos.offset(-offset.getX(), 0, -offset.getZ()).relative(direction.getCounterClockWise(), halfWidth);
            corridor.primaryTheme = Themes.getPrimary(BuiltinThemes.DEFAULT);
            corridor.secondaryTheme = Themes.getSecondary(BuiltinThemes.DEFAULT);

            corridor.createBoundingBox();
            fragments.add(corridor);

            pos.move(direction, FRAGMENT_LENGTH);
        }
    }

    private void addSideSegments(Random random) {
        for (CompoundPiece fragment : this.fragments) {
            ImmutableList<Anchor> attachmentPoints = fragment.blueprint.anchors().get(BuiltinAnchorTypes.JUNCTURE);
            if (attachmentPoints == null) {
                continue;
            }
            CoordinateSpace coordinateSpace = fragment.blueprint.coordinateSpace(fragment.position);
            for (Anchor attachmentPoint : attachmentPoints) {
                Blueprint sideSegment = Blueprints.getBlueprint(BuiltinBlueprints.CORRIDOR_SIDE_SEGMENT);
                ImmutableList<Anchor> junctures = sideSegment.anchors().get(BuiltinAnchorTypes.JUNCTURE);
                if (junctures == null) {
                    continue;
                }
                Anchor actual = coordinateSpace.rotateAndTranslateToOrigin(attachmentPoint, fragment.rotation);
                Anchor juncture = junctures.get(random.nextInt(junctures.size()));
                Rotation rotation = Orientation.horizontalRotation(juncture.direction(), actual.direction().getOpposite());
                BlockPos pos = juncture.latchOnto(actual, sideSegment.coordinateSpace(BlockPos.ZERO));
                fragment.addSegment(new Segment(sideSegment, pos, rotation));
            }
        }
    }

    @Override
    public void createPieces(Consumer<StructurePiece> consumer) {
        fragment();
        addSideSegments(new Random());
        fragments.forEach(consumer);
        int remaining = length() - fragmentationStart;
        if (fragmentationStart > 0) {
            BoundingBoxBuilder tunnel = new BoundingBoxBuilder(this.boundingBox);
            tunnel.shrink(direction, remaining);
            consumer.accept(new TestPiece(tunnel.create()));
        }
        int r = remaining % 3;
        if (r > 0) {
            BoundingBoxBuilder tunnel = new BoundingBoxBuilder(boundingBox);
            tunnel.shrink(direction.getOpposite(), length() - r);
            consumer.accept(new TestPiece(tunnel.create()));
        }
    }
}
