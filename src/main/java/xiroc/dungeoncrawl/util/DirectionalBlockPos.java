/*
        Dungeon Crawl, a procedural dungeon generator for Minecraft 1.14 and later.
        Copyright (C) 2020

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package xiroc.dungeoncrawl.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Rotation;

import java.util.Locale;

public class DirectionalBlockPos {

    public BlockPos position;
    public Direction direction;
    public Rotation rotation;

    public DirectionalBlockPos(int x, int y, int z, Direction direction) {
        this.position = new BlockPos(x, y, z);
        this.direction = direction;
    }

    public DirectionalBlockPos(int x, int y, int z, Direction direction, Rotation rotation) {
        this.position = new BlockPos(x, y, z);
        this.direction = direction;
        this.rotation = rotation;
    }

    public DirectionalBlockPos offset(Direction direction, int amount) {
        position = position.relative(direction, amount);
        return this;
    }

    public void writeToNBT(CompoundTag nbt) {
        nbt.putInt("x", position.getX());
        nbt.putInt("y", position.getY());
        nbt.putInt("z", position.getZ());
        nbt.putInt("direction", direction.get3DDataValue());
        if (rotation != null) {
            nbt.putString("rotation", rotation.toString().toUpperCase(Locale.ROOT));
        }
    }

    public static DirectionalBlockPos fromNBT(CompoundTag nbt) {
        return new DirectionalBlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"),
                Direction.from3DDataValue(nbt.getInt("direction")), nbt.contains("rotation") ? Rotation.valueOf(nbt.getString("rotation")) : null);
    }

}
