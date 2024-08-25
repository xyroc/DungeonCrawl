package xiroc.dungeoncrawl.dungeon.generator.element;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintMultipart;
import xiroc.dungeoncrawl.dungeon.blueprint.Entrance;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.dungeon.piece.BlueprintPiece;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxUtils;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;

public class NodeElement extends DungeonElement {
    private final BlueprintPiece piece;

    public final int depth;
    public final ArrayList<Entrance> unusedEntrances;

    public NodeElement(BlueprintPiece piece, int depth) {
        super(piece.getBoundingBox());
        this.piece = piece;
        this.depth = depth;
        this.unusedEntrances = Lists.newArrayList(piece.base.blueprint().get().entrances());
    }

    public void update(LevelGenerator levelGenerator) {
        if (this.depth < levelGenerator.levelType.settings().maxDepth) {
            final Random random = levelGenerator.random;
            final CoordinateSpace coordinateSpace = piece.base.blueprint().get().coordinateSpace(piece.base.position());
            int placements = 3;
            for (int attempt = 0; !unusedEntrances.isEmpty() && attempt < 4 && placements > 0; ++attempt) {
                final Entrance entrance = unusedEntrances.remove(random.nextInt(unusedEntrances.size()));
                final Anchor placement = coordinateSpace.rotateAndTranslateToOrigin(entrance.placement(), piece.base.rotation());

                if (levelGenerator.createClusterNode(placement, this.depth + 1) || attachRoom(levelGenerator, placement)) {
                    addEntrance(placement, entrance, random);
                    --placements;
                } else {
                    entrance.customParts().ifPresent(parts -> BlueprintMultipart.addPart(placement.opposite(), parts.closed(), piece, piece.base, random));
                }
            }
        }
    }

    private boolean attachRoom(LevelGenerator levelGenerator, Anchor placement) {
        final Random random = levelGenerator.random;

        final BlockPos corridorStart = placement.position().relative(placement.direction());
        final Direction corridorDirection = placement.direction();
        final int corridorLength = levelGenerator.levelType.settings().corridorLength.nextInt(random);
        final BoundingBoxBuilder corridorBox = BoundingBoxUtils.tunnelBuilder(corridorStart, corridorDirection, corridorLength, 8, 2);

        if (!levelGenerator.plan.isFree(corridorBox)) {
            return false;
        }

        final int roomDepth = this.depth + 1;
        final boolean isEndStaircase = levelGenerator.shouldPlaceEndStaircase(roomDepth);

        final Delegate<Blueprint> room = isEndStaircase
                ? levelGenerator.levelType.upperStaircaseRooms().roll(random)
                : levelGenerator.levelType.rooms().roll(random);
        final var entrances = room.get().entrances();
        if (entrances.isEmpty()) {
            return false;
        }

        final int chosenEntrance = random.nextInt(entrances.size());
        final Entrance entrance = entrances.get(chosenEntrance);
        final Anchor corridorEnd = new Anchor(corridorStart.relative(corridorDirection, corridorLength - 1), corridorDirection);

        final CoordinateSpace coordinateSpace = room.get().coordinateSpace(BlockPos.ZERO);
        final BlockPos roomPosition = entrance.placement().latchOnto(corridorEnd, coordinateSpace);
        final Rotation rotation = Orientation.horizontalRotation(entrance.placement().direction(), corridorDirection.getOpposite());
        final BoundingBoxBuilder roomBox = room.get().boundingBox(rotation).move(roomPosition);

        if (!levelGenerator.plan.isFree(roomBox)) {
            return false;
        }

        final BlueprintPiece roomPiece = levelGenerator.assemblePiece(room, roomPosition, rotation);
        if (roomPiece == null) {
            return false;
        }

        final NodeElement node = levelGenerator.createNode(roomPiece, roomDepth, true, isEndStaircase);
        node.unusedEntrances.remove(chosenEntrance);
        levelGenerator.createCorridor(this, node, corridorStart, corridorDirection, corridorBox.create());

        final Anchor rotatedEntrance = room.get().coordinateSpace(roomPosition).rotateAndTranslateToOrigin(entrance.placement(), rotation);
        node.addEntrance(rotatedEntrance, entrance, random);
        return true;
    }

    public void addEntrance(Anchor placement, Entrance entrance, Random random) {
        piece.addComponent(entrance.place(placement));
        entrance.customParts().ifPresent(parts -> BlueprintMultipart.addPart(placement.opposite(), parts.open(), piece, piece.base, random));
    }

    @Override
    public void createPieces(Consumer<StructurePiece> consumer, Random random) {
        CoordinateSpace coordinateSpace = piece.base.blueprint().get().coordinateSpace(piece.base.position());
        for (Entrance entrance : unusedEntrances) {
            Anchor position = coordinateSpace.rotateAndTranslateToOrigin(entrance.placement(), piece.base.rotation());
            entrance.customParts().ifPresent(parts -> BlueprintMultipart.addPart(position.opposite(), parts.closed(), piece, piece.base, random));
        }
        piece.createBoundingBox();
        consumer.accept(piece);
    }

    public BlueprintPiece piece() {
        return this.piece;
    }
}
