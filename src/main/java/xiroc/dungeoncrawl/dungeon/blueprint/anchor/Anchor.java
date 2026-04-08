package xiroc.dungeoncrawl.dungeon.blueprint.anchor;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Rotation;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.Orientation;

import java.util.stream.IntStream;

public record Anchor(BlockPos position, Direction direction) {
    public static final Codec<Anchor> CODEC = Codec.INT_STREAM.comapFlatMap(
            (encoded) -> Util.fixedSize(encoded, 4).map((values) ->
                    new Anchor(new BlockPos(values[0], values[1], values[2]), Direction.from3DDataValue(values[3]))),
            (anchor) -> IntStream.of(anchor.position.getX(), anchor.position.getY(), anchor.position.getZ(), anchor.direction.get3DDataValue())
    ).stable();

    public static Anchor of(int x, int y, int z, Direction direction) {
        return new Anchor(new BlockPos(x, y, z), direction);
    }

    public Anchor opposite() {
        return new Anchor(position.relative(direction), direction.getOpposite());
    }

    /**
     * Assuming the {@link Blueprint} {@code parent} is holding this anchor, this method calculates the position and rotation it must be placed at so that this
     * {@link Anchor} ends up in opposition to the provided {@link Anchor} {@code anchor}.
     *
     * @param anchor the anchor to attach this anchor to.
     * @param parent the blueprint holding this anchor.
     * @return a pair holding the resulting position and rotation.
     */
    public Pair<BlockPos.MutableBlockPos, Rotation> latchOnto(Anchor anchor, Blueprint parent) {
        Rotation rotation = Orientation.horizontalRotation(this.direction, anchor.direction.getOpposite());
        Vec3i offset = CoordinateSpace.rotate(this.position, rotation, parent.xSpan(), parent.zSpan());
        BlockPos.MutableBlockPos position = anchor.position.mutable().move(anchor.direction).move(-offset.getX(), -offset.getY(), -offset.getZ());
        return new Pair<>(position, rotation);
    }
}