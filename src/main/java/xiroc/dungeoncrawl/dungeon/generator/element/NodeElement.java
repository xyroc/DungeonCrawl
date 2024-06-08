package xiroc.dungeoncrawl.dungeon.generator.element;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import xiroc.dungeoncrawl.datapack.delegate.Delegate;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
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
    public final int depth;
    public final ArrayList<Anchor> unusedEntrances;

    private final BlueprintPiece piece;

    public NodeElement(BlueprintPiece piece, int depth) {
        super(piece.getBoundingBox());
        this.piece = piece;
        this.depth = depth;
        ImmutableList<Anchor> entrances = piece.base.blueprint().get().anchors().get(BuiltinAnchorTypes.ENTRANCE);
        this.unusedEntrances = entrances == null ? new ArrayList<>(0) : Lists.newArrayList(entrances);
    }

    public void update(LevelGenerator levelGenerator) {
        if (this.depth < levelGenerator.levelType.settings().maxDepth) {
            Random random = levelGenerator.random;
            int placements = 3;
            CoordinateSpace coordinateSpace = piece.base.blueprint().get().coordinateSpace(piece.base.position());
            for (int attempt = 0; !unusedEntrances.isEmpty() && attempt < 4 && placements > 0; ++attempt) {
                Anchor entrance = coordinateSpace.rotateAndTranslateToOrigin(unusedEntrances.remove(random.nextInt(unusedEntrances.size())), piece.base.rotation());

                if (random.nextInt(10) == 0 && levelGenerator.createClusterNode(entrance, random, this.depth + 1)) {
                    piece.addEntrance(entrance.position().above(), entrance.direction());
                    --placements;
                    continue;
                }

                if (attachRoom(levelGenerator, entrance.position().relative(entrance.direction()), entrance.direction())) {
                    piece.addEntrance(entrance.position().above(), entrance.direction());
                    --placements;
                }
            }
        }
    }

    private boolean attachRoom(LevelGenerator levelGenerator, BlockPos start, Direction direction) {
        Random random = levelGenerator.random;
        int corridorLength = levelGenerator.levelType.settings().corridorLength.nextInt(random);
        int roomDepth = this.depth + 1;
        boolean isEndStaircase = levelGenerator.shouldPlaceEndStaircase(roomDepth);

        BoundingBoxBuilder corridorBox = BoundingBoxUtils.tunnelBuilder(start, direction, corridorLength, 8, 2);
        if (!levelGenerator.plan.isFree(corridorBox)) {
            return false;
        }

        Delegate<Blueprint> room = isEndStaircase
                ? levelGenerator.levelType.upperStaircaseRooms().roll(random)
                : levelGenerator.levelType.rooms().roll(random);
        ImmutableList<Anchor> entrances = room.get().anchors().get(BuiltinAnchorTypes.ENTRANCE);
        if (entrances == null) {
            return false;
        }

        final int chosenEntrance = random.nextInt(entrances.size());
        Anchor entrance = entrances.get(chosenEntrance);
        Anchor corridorEnd = new Anchor(start.relative(direction, corridorLength - 1), direction);

        CoordinateSpace coordinateSpace = room.get().coordinateSpace(BlockPos.ZERO);
        BlockPos roomPosition = entrance.latchOnto(corridorEnd, coordinateSpace);
        Rotation rotation = Orientation.horizontalRotation(entrance.direction(), direction.getOpposite());

        BoundingBoxBuilder roomBox = room.get().boundingBox(rotation);
        roomBox.move(roomPosition);
        if (!levelGenerator.plan.isFree(roomBox)) {
            return false;
        }

        BlueprintPiece piece = levelGenerator.assemblePiece(room, roomPosition, rotation);
        if (piece == null) {
            return false;
        }

        NodeElement node = levelGenerator.createNode(piece, roomDepth, true, isEndStaircase);
        node.unusedEntrances.remove(chosenEntrance);
        levelGenerator.createCorridor(this, node, start, direction, corridorBox.create());

        Anchor rotatedEntrance = coordinateSpace.rotateAndTranslateToOrigin(entrance, rotation);
        node.piece.addEntrance(roomPosition.offset(rotatedEntrance.position()).above(), rotatedEntrance.direction());
        return true;
    }

    @Override
    public void createPieces(Consumer<StructurePiece> consumer) {
        piece.createBoundingBox();
        consumer.accept(piece);
    }

    public BlueprintPiece piece() {
        return this.piece;
    }
}
