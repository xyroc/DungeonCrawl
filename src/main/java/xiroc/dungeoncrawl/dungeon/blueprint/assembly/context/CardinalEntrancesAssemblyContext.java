package xiroc.dungeoncrawl.dungeon.blueprint.assembly.context;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintMultipart;
import xiroc.dungeoncrawl.dungeon.blueprint.Entrance;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.blueprint.assembly.type.CardinalEntrancesAssembly;
import xiroc.dungeoncrawl.dungeon.component.BlueprintComponent;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.dungeon.piece.BlueprintPiece;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.Orientation;

import java.util.List;

public class CardinalEntrancesAssemblyContext extends SimpleAssemblyContext {

    private final BlueprintComponent[] cardinalEntrances = {null, null, null, null};
    private final CardinalEntrancesAssembly assembly;

    public CardinalEntrancesAssemblyContext(CardinalEntrancesAssembly assembly) {
        this.assembly = assembly;
    }

    @Override
    public void noteOpenEntrance(Entrance entrance, Anchor placement, @Nullable BlueprintComponent entrancePart) {
        super.noteOpenEntrance(entrance, placement, entrancePart);
        Direction direction = placement.direction();
        if (!direction.getAxis().isHorizontal()) {
            return;
        }
        setEntrance(direction, entrancePart);
    }

    @Override
    public void assemble(BlueprintPiece piece, LevelGenerator levelGenerator) {
        for (Direction entranceDirection : Orientation.HORIZONTAL_DIRECTIONS) {
            BlueprintComponent entrance1 = getEntrance(entranceDirection);
            BlueprintComponent entrance2 = getEntrance(entranceDirection.getClockWise());

            if (entrance1 != null && entrance2 != null) {
                // Both entrances present -> add a corner piece
                placeCornerPiece(entrance1, entrance2, piece, entranceDirection, levelGenerator);
            } else if (entrance1 != null) {
                // Add single
                placeSinglePiece(entrance1, piece, levelGenerator, entranceDirection.getClockWise());
            } else if (entrance2 != null) {
                // Add single
                placeSinglePiece(entrance2, piece, levelGenerator, entranceDirection);
            }
        }
        super.assemble(piece, levelGenerator);
    }

    private void placeCornerPiece(BlueprintComponent entrance1, BlueprintComponent entrance2, BlueprintPiece piece, Direction entranceDirection, LevelGenerator levelGenerator) {
        ResourceLocation attachmentAnchorType = assembly.config().anchorType();

        // The target direction is rotated, but we are searching a list of unrotated anchors. So we undo the rotation before searching.
        Anchor entranceAnchor1 = findAndRotateAnchor(attachmentAnchorType, entrance1, entranceDirection.getClockWise());
        Anchor entranceAnchor2 = findAndRotateAnchor(attachmentAnchorType, entrance2, entranceDirection);

        if (entranceAnchor1 == null || entranceAnchor2 == null) {
            return;
        }

        for (int attempt = 0; attempt < 4; ++attempt) {
            Holder<Blueprint> corner = assembly.config().cornerPieces().roll(levelGenerator.random);
            Blueprint cornerBlueprint = corner.value();

            List<Anchor> junctures = cornerBlueprint.anchors().get(BuiltinAnchorTypes.JUNCTURE);
            if (junctures == null) {
                continue;
            }

            BlueprintComponent cornerComponent = null;
            // Try each juncture until we find one that attaches in a way that both entrance anchors are connected.
            for (Anchor junctureAttempt : junctures) {
                var placement = junctureAttempt.latchOnto(entranceAnchor1, cornerBlueprint);
                CoordinateSpace coordinateSpace = cornerBlueprint.coordinateSpace(placement.getFirst());

                Anchor targetAnchor = entranceAnchor2.opposite();
                boolean validPlacement = false;
                for (Anchor juncture : junctures) {
                    if (juncture == junctureAttempt) {
                        continue;
                    }
                    Anchor junctureInWorld = coordinateSpace.rotateAndTranslateToOrigin(juncture, placement.getSecond());

                    if (junctureInWorld.equals(targetAnchor)) {
                        validPlacement = true;
                        break;
                    }
                }

                if (validPlacement) {
                    cornerComponent = new BlueprintComponent(corner, placement.getFirst(), placement.getSecond());
                    break;
                }
            }

            if (cornerComponent == null) {
                continue;
            }

            BlueprintMultipart.addBlueprintPart(piece, cornerComponent, levelGenerator, 0);
            break;
        }
    }

    private void placeSinglePiece(BlueprintComponent entranceComponent, BlueprintPiece piece, LevelGenerator levelGenerator, Direction anchorDirection) {
        Anchor anchor = findAndRotateAnchor(assembly.config().anchorType(), entranceComponent, anchorDirection);
        if (anchor == null) {
            return;
        }
        BlueprintMultipart.addPart(anchor, assembly.config().singlePieces(), piece, piece.base, levelGenerator);
    }

    @Nullable
    private Anchor findAndRotateAnchor(ResourceLocation anchorType, BlueprintComponent component, Direction targetDirection) {
        List<Anchor> anchors = component.blueprint().value().anchors().get(anchorType);
        if (anchors == null) {
            return null;
        }

        // The target direction is rotated, but we are searching a list of unrotated anchors. So we undo the rotation before searching.
        Direction projectedTargetDirection = Orientation.inverseRotation(component.rotation()).rotate(targetDirection);

        for (Anchor anchor : anchors) {
            if (anchor.direction() == projectedTargetDirection) {
                return component.coordinateSpace().rotateAndTranslateToOrigin(anchor, component.rotation());
            }
        }
        return null;
    }

    private void setEntrance(Direction direction, @Nullable BlueprintComponent entrancePart) {
        cardinalEntrances[direction.get2DDataValue()] = entrancePart;
    }

    @Nullable
    private BlueprintComponent getEntrance(Direction direction) {
        return cardinalEntrances[direction.get2DDataValue()];
    }
}
