package xiroc.dungeoncrawl.dungeon.piece;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprints;
import xiroc.dungeoncrawl.util.Orientation;

public record Segment(Blueprint blueprint, BlockPos position, Rotation rotation) {
    public static final Codec<Segment> CODEC = RecordCodecBuilder.create((builder) ->
            builder.group(ResourceLocation.CODEC.fieldOf("blueprint").forGetter(segment -> segment.blueprint.key()),
                            BlockPos.CODEC.fieldOf("position").forGetter(Segment::position),
                            Codec.INT.fieldOf("rotation").forGetter(segment -> Orientation.rotationAsInt(segment.rotation)))
                    .apply(builder, (blueprint, position, rotation) -> new Segment(Blueprints.getBlueprint(blueprint), position, Orientation.getRotation(rotation))));

    private static final String NBT_KEY_X = "X";
    private static final String NBT_KEY_Y = "Y";
    private static final String NBT_KEY_Z = "Z";
    private static final String NBT_KEY_ROTATION = "Rotation";
    private static final String NBT_KEY_BLUEPRINT = "Blueprint";

    public static Segment read(CompoundTag nbt) {
        Blueprint blueprint = Blueprints.getBlueprint(new ResourceLocation(nbt.getString(NBT_KEY_BLUEPRINT)));
        BlockPos position = new BlockPos(nbt.getInt(NBT_KEY_X), nbt.getInt(NBT_KEY_Y), nbt.getInt(NBT_KEY_Z));
        Rotation rotation = Orientation.getRotation(nbt.getInt(NBT_KEY_ROTATION));
        return new Segment(blueprint, position, rotation);
    }

    public CompoundTag write() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString(NBT_KEY_BLUEPRINT, blueprint.key().toString());
        nbt.putInt(NBT_KEY_X, position.getX());
        nbt.putInt(NBT_KEY_Y, position.getY());
        nbt.putInt(NBT_KEY_Z, position.getZ());
        nbt.putInt(NBT_KEY_ROTATION, Orientation.rotationAsInt(rotation));
        return nbt;
    }
}
