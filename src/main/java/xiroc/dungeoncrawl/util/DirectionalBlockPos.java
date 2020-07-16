package xiroc.dungeoncrawl.util;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */


import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

import java.util.Locale;

public class DirectionalBlockPos {

    public BlockPos position;
    public Direction direction;
    public Rotation rotation;

    public DirectionalBlockPos(BlockPos position, Direction direction) {
        this.position = position;
        this.direction = direction;
    }

    public DirectionalBlockPos(int x, int y, int z, Direction direction) {
        this.position = new BlockPos(x, y, z);
        this.direction = direction;
    }

    public DirectionalBlockPos(BlockPos position, Direction direction, Rotation rotation) {
        this.position = position;
        this.direction = direction;
        this.rotation = rotation;
    }

    public DirectionalBlockPos(int x, int y, int z, Direction direction, Rotation rotation) {
        this.position = new BlockPos(x, y, z);
        this.direction = direction;
        this.rotation = rotation;
    }

    public DirectionalBlockPos offset(Direction direction, int amount) {
        position = position.offset(direction, amount);
        return this;
    }

    public void writeToNBT(CompoundNBT nbt) {
        nbt.putInt("x", position.getX());
        nbt.putInt("y", position.getY());
        nbt.putInt("z", position.getZ());
        nbt.putString("direction", direction.toString().toUpperCase(Locale.ROOT));
        if (rotation != null) {
            nbt.putString("rotation", rotation.toString().toUpperCase(Locale.ROOT));
        }
    }

    public static DirectionalBlockPos fromNBT(CompoundNBT nbt) {
        return new DirectionalBlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"),
                Direction.valueOf(nbt.getString("direction")), nbt.contains("rotation") ? Rotation.valueOf(nbt.getString("rotation")) : null);
    }
}
