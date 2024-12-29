package xiroc.dungeoncrawl.dungeon.generator.element;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintMultipart;
import xiroc.dungeoncrawl.dungeon.blueprint.Entrance;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.component.EntranceComponent;
import xiroc.dungeoncrawl.dungeon.generator.level.GeneratorContext;
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

    @Nullable
    public static NodeElement attachRoomWithCorridor(GeneratorContext context, Anchor attachmentPoint, Delegate<Blueprint> room, int depth) {
        final LevelGenerator levelGenerator = context.levelGenerator();

        final BlockPos corridorStart = attachmentPoint.position().relative(attachmentPoint.direction());
        final Direction corridorDirection = attachmentPoint.direction();
        final int corridorLength = levelGenerator.levelType.settings().corridorLength.nextInt(levelGenerator.random);
        final BoundingBoxBuilder corridorBox = BoundingBoxUtils.tunnelBuilder(corridorStart, corridorDirection, corridorLength, 8, 2);

        if (!context.dungeonPlan().isFree(corridorBox)) {
            return null;
        }

        final Anchor corridorEnd = new Anchor(corridorStart.relative(corridorDirection, corridorLength - 1), corridorDirection);
        NodeElement node = attachRoom(context, corridorEnd, room, depth);
        if (node == null) {
            return null;
        }

        levelGenerator.createCorridor(corridorStart, corridorDirection, corridorBox.create());
        return node;
    }

    @Nullable
    public static NodeElement attachRoom(GeneratorContext context, Anchor attachmentPoint, Delegate<Blueprint> room, int depth) {
        final LevelGenerator levelGenerator = context.levelGenerator();
        final var entrances = room.get().entrances();
        if (entrances.isEmpty()) {
            return null;
        }
        final int chosenEntrance = levelGenerator.random.nextInt(entrances.size());
        final Entrance entrance = entrances.get(chosenEntrance);
        final BlockPos roomPosition = entrance.placement().latchOnto(attachmentPoint, room.get().coordinateSpace(BlockPos.ZERO));
        final Rotation rotation = Orientation.horizontalRotation(entrance.placement().direction(), attachmentPoint.direction().getOpposite());
        final BoundingBoxBuilder roomBox = room.get().boundingBox(rotation).move(roomPosition);

        if (!context.dungeonPlan().isFree(roomBox)) {
            return null;
        }

        final BlueprintPiece roomPiece = levelGenerator.assemblePiece(room, roomPosition, rotation);
        if (roomPiece == null) {
            return null;
        }

        final NodeElement node = new NodeElement(roomPiece, depth);
        context.dungeonPlan().add(node);
        node.unusedEntrances.remove(chosenEntrance);
        final Anchor rotatedEntrance = room.get().coordinateSpace(roomPosition).rotateAndTranslateToOrigin(entrance.placement(), rotation);
        node.addEntrance(rotatedEntrance, entrance, levelGenerator.random);
        return node;
    }

    public void addEntrance(Anchor placement, Entrance entrance, Random random) {
        EntranceComponent placedEntrance = entrance.place(placement);
        if (placedEntrance != null) {
            piece.addComponent(placedEntrance);
        }
        entrance.customParts().ifPresent(parts -> BlueprintMultipart.addPart(placement.opposite(), parts.open(), piece, piece.base, random));
    }

    @Override
    public void createPieces(Consumer<StructurePiece> consumer, Random random) {
        CoordinateSpace coordinateSpace = piece.base.blueprint().get().coordinateSpace(piece.base.position());
        for (Entrance entrance : unusedEntrances) {
            Anchor position = coordinateSpace.rotateAndTranslateToOrigin(entrance.placement(), piece.base.rotation());
            entrance.customParts().ifPresent(parts -> BlueprintMultipart.addPart(position.opposite(), parts.closed(), piece, piece.base, random));
        }
        piece.updateBoundingBox();
        consumer.accept(piece);
    }

    public BlueprintPiece piece() {
        return this.piece;
    }
}
