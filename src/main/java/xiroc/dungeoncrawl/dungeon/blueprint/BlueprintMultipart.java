package xiroc.dungeoncrawl.dungeon.blueprint;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.component.BlueprintComponent;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.dungeon.piece.BlueprintPiece;
import xiroc.dungeoncrawl.exception.ExceededMultipartDepthException;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.util.random.IRandom;

public record BlueprintMultipart(ResourceLocation anchorType, IRandom<Holder<Blueprint>> variants) {
    /**
     * The maximum number of recursive calls to {@code addParts} that may occur when populating a piece.
     */
    private static final int MAX_POPULATION_DEPTH = 3;

    public static final Codec<BlueprintMultipart> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("positions").forGetter(BlueprintMultipart::anchorType),
            Blueprint.RANDOM_HOLDER_CODEC.fieldOf("variants").forGetter(BlueprintMultipart::variants)
    ).apply(instance, BlueprintMultipart::new));

    /**
     * Populates the piece with blueprint parts.
     *
     * @param piece          The piece to add the blueprint components to.
     * @param parent         The blueprint component that is being populated. Must be a member of the {@code piece}.
     * @param levelGenerator The level generator.
     * @return {@code true} if population <b>failed</b>, {@code false} if successful.
     */
    public boolean addParts(BlueprintPiece piece, BlueprintComponent parent, LevelGenerator levelGenerator) {
        return addParts(piece, parent, levelGenerator, 0);
    }

    /**
     * Populates the piece with blueprint parts.
     *
     * @param piece          The piece to add the blueprint components to.
     * @param parent         The blueprint component that is being populated. Must be a member of the {@code piece}.
     * @param levelGenerator The level generator.
     * @param depth          The depth of this method call. Increases when added parts are populated with parts themselves.
     *                       Maximum of {@link BlueprintMultipart#MAX_POPULATION_DEPTH}.
     * @return {@code true} if population <b>failed</b>, {@code false} if successful.
     */
    private boolean addParts(BlueprintPiece piece, BlueprintComponent parent, LevelGenerator levelGenerator, int depth) {
        if (depth > MAX_POPULATION_DEPTH) {
            throw new ExceededMultipartDepthException(parent.blueprint());
        }
        var anchors = parent.blueprint().value().anchors().get(anchorType);
        if (anchors == null) {
            return false;
        }
        CoordinateSpace parentCoordinateSpace = parent.blueprint().value().coordinateSpace(parent.position());
        for (Anchor anchor : anchors) {
            anchor = parentCoordinateSpace.rotateAndTranslateToOrigin(anchor, parent.rotation());
            try {
                if (!addPart(anchor, variants, piece, parent, levelGenerator, depth)) {
                    return true;
                }
            } catch (ExceededMultipartDepthException exception) {
                var wrappedException = new ExceededMultipartDepthException(parent.blueprint(), exception);
                if (depth > 0) {
                    // Wrap the exception in one that knows the parent blueprint so that, at the top level, we can reconstruct the chain of blueprints that exceeded the limit
                    throw wrappedException;
                }
                warnAboutDepthLimitExcession(wrappedException);
                // Soft error. The blueprint should not be instantiated, but we also don't want to crash because of this.
                return true;
            }
        }
        return false;
    }

    public static boolean addPart(Anchor anchor, IRandom<Holder<Blueprint>> parts, BlueprintPiece piece, BlueprintComponent parent, LevelGenerator levelGenerator) {
        try {
            return addPart(anchor, parts, piece, parent, levelGenerator, 0);
        } catch (ExceededMultipartDepthException e) {
            warnAboutDepthLimitExcession(e);
            return false;
        }
    }

    private static boolean addPart(Anchor anchor, IRandom<Holder<Blueprint>> parts, BlueprintPiece piece, BlueprintComponent parent, LevelGenerator levelGenerator, int depth) {
        for (int attempt = 0; attempt < 4; ++attempt) {
            Holder<Blueprint> part = parts.roll(levelGenerator.random);
            if (addPart(anchor, part, piece, parent, levelGenerator, depth)) {
                return true;
            }
        }
        return false;
    }

    private static boolean addPart(Anchor anchor, Holder<Blueprint> part, BlueprintPiece piece, BlueprintComponent parent, LevelGenerator levelGenerator, int depth) {
        BoundingBoxBuilder parentBox = parent.boundingBox();
        Blueprint blueprint = part.value();
        var junctures = blueprint.anchors().get(BuiltinAnchorTypes.JUNCTURE);
        if (junctures == null || junctures.isEmpty()) {
            return false;
        }
        boolean horizontalAnchor = anchor.direction().getAxis().isHorizontal();
        for (int attempt = 0; attempt < junctures.size(); ++attempt) {
            Anchor juncture = junctures.get(levelGenerator.random.nextInt(junctures.size()));
            boolean horizontalJuncture = juncture.direction().getAxis().isHorizontal();
            if (horizontalAnchor && !horizontalJuncture || !horizontalAnchor && horizontalJuncture) {
                continue;
            }
            Rotation rotation = horizontalJuncture ? Orientation.horizontalRotation(juncture.direction(), anchor.direction().getOpposite()) : parent.rotation();
            Vec3i offset = CoordinateSpace.rotate(juncture.position(), rotation, blueprint.xSpan(), blueprint.zSpan());
            BlockPos pos = anchor.position().mutable()
                    .move(anchor.direction())
                    .move(-offset.getX(), -offset.getY(), -offset.getZ());
            BoundingBoxBuilder boundingBox = blueprint.boundingBox(rotation).move(pos);
            if (!parentBox.encapsulates(boundingBox)) {
                DungeonCrawl.LOGGER.warn("Blueprint part {} does not fit inside its parent blueprint {} when placed at anchor {}." +
                        " This should never happen and indicates a broken blueprint configuration.", part.getKey(), parent.blueprint().getKey(), juncture);
                continue;
            }
            BlueprintComponent component = new BlueprintComponent(part, pos, rotation);
            piece.addComponent(component);
            blueprint.populateFeatures(levelGenerator, pos, rotation, piece::addComponent);
            for (var subPart : blueprint.parts()) {
                if (subPart.addParts(piece, component, levelGenerator, depth + 1)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Reconstructs the chain of blueprints that was populated from the chain of exceptions and prints a warning about it.
     *
     * @param exception The top level exception.
     */
    private static void warnAboutDepthLimitExcession(ExceededMultipartDepthException exception) {
        StringBuilder blueprintChain = new StringBuilder();
        while (exception != null) {
            if (!blueprintChain.isEmpty()) {
                blueprintChain.append("->");
            }
            blueprintChain.append(exception.blueprint.getKey());
            exception = exception.cause;
        }
        DungeonCrawl.LOGGER.warn("Exceeded the blueprint depth limit (which is {}) when populating blueprints: {}.", MAX_POPULATION_DEPTH, blueprintChain);
    }
}
