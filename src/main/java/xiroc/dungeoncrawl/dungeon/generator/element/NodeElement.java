package xiroc.dungeoncrawl.dungeon.generator.element;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
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

import java.util.ArrayList;
import java.util.function.Consumer;

public class NodeElement extends DungeonElement {
    private final BlueprintPiece piece;
    private final GeneratorContext context;

    public final int depth;
    public final ArrayList<Entrance> unusedEntrances;

    public NodeElement(BlueprintPiece piece, GeneratorContext context, int depth) {
        super(piece.getBoundingBox());
        this.piece = piece;
        this.context = context;
        this.depth = depth;
        this.unusedEntrances = Lists.newArrayList(piece.base.blueprint().value().entrances());
    }

    @Nullable
    public static NodeElement attachRoomWithCorridor(GeneratorContext context, Anchor attachmentPoint, Holder<Blueprint> room, int depth) {
        final LevelGenerator levelGenerator = context.levelGenerator();

        final BlockPos corridorStart = attachmentPoint.position().relative(attachmentPoint.direction());
        final Direction corridorDirection = attachmentPoint.direction();
        final int corridorLength = levelGenerator.levelType.settings().corridorLength.nextInt(levelGenerator.random);
        final BoundingBoxBuilder corridorBox = BoundingBoxBuilder.tunnel(corridorStart, corridorDirection, corridorLength, 8, 2);

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
    public static NodeElement attachRoom(GeneratorContext context, Anchor attachmentPoint, Holder<Blueprint> room, int depth) {
        final LevelGenerator levelGenerator = context.levelGenerator();
        final var entrances = room.value().entrances();
        if (entrances.isEmpty()) {
            return null;
        }
        final int chosenEntrance = levelGenerator.random.nextInt(entrances.size());
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

        final NodeElement node = new NodeElement(roomPiece, context, depth);
        context.dungeonPlan().add(node);
        node.unusedEntrances.remove(chosenEntrance);
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

    @Override
    public void createPieces(Consumer<StructurePiece> consumer) {
        CoordinateSpace coordinateSpace = piece.base.blueprint().value().coordinateSpace(piece.base.position());
        for (Entrance entrance : unusedEntrances) {
            Anchor position = coordinateSpace.rotateAndTranslateToOrigin(entrance.placement(), piece.base.rotation());
            entrance.customParts().ifPresent(parts -> BlueprintMultipart.addPart(position.opposite(), parts.closed(), piece, piece.base, context.levelGenerator()));
        }
        piece.updateBoundingBox();
        consumer.accept(piece);
    }

    public BlueprintPiece piece() {
        return this.piece;
    }
}
