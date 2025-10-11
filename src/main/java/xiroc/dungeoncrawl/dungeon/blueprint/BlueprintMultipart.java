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
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.util.random.IRandom;

public record BlueprintMultipart(ResourceLocation anchorType, IRandom<Holder<Blueprint>> variants) {
    public static final Codec<BlueprintMultipart> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("positions").forGetter(BlueprintMultipart::anchorType),
            Blueprint.RANDOM_HOLDER_CODEC.fieldOf("variants").forGetter(BlueprintMultipart::variants)
    ).apply(instance, BlueprintMultipart::new));

    public boolean addParts(DungeonPiece piece, BlueprintComponent parent, LevelGenerator levelGenerator) {
        var anchors = parent.blueprint().value().anchors().get(anchorType);
        if (anchors == null) {
            return true;
        }
        CoordinateSpace parentCoordinateSpace = parent.blueprint().value().coordinateSpace(parent.position());
        for (Anchor anchor : anchors) {
            anchor = parentCoordinateSpace.rotateAndTranslateToOrigin(anchor, parent.rotation());
            if (!addPart(anchor, variants, piece, parent, levelGenerator)) {
                return false;
            }
        }
        return true;
    }

    public static boolean addPart(Anchor anchor, IRandom<Holder<Blueprint>> parts, DungeonPiece piece, BlueprintComponent parent, LevelGenerator levelGenerator) {
        for (int attempt = 0; attempt < 4; ++attempt) {
            Holder<Blueprint> part = parts.roll(levelGenerator.random);
            if (addPart(anchor, part, piece, parent, levelGenerator)) {
                return true;
            }
        }
        return false;
    }

    private static boolean addPart(Anchor anchor, Holder<Blueprint> part, DungeonPiece piece, BlueprintComponent parent, LevelGenerator levelGenerator) {
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
            piece.addComponent(new BlueprintComponent(part, pos, rotation));
            part.value().populateFeatures(levelGenerator, pos, rotation, piece::addComponent);
            return true;
        }
        return false;
    }
}
