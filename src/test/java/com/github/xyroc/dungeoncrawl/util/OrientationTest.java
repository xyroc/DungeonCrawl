package com.github.xyroc.dungeoncrawl.util;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import xiroc.dungeoncrawl.util.Orientation;

import static org.assertj.core.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class OrientationTest {

    // Directions in the order of 90 degree clockwise rotations, starting from north
    private static final Direction[] DIRECTIONS = new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    private static Direction directionModulo(int index) {
        return DIRECTIONS[index % DIRECTIONS.length];
    }

    @Test
    void horizontalRotation() {
        for (int index = 0; index < DIRECTIONS.length; ++index) {
            final Direction dir = DIRECTIONS[index];

            assertThat(Orientation.horizontalRotation(dir, dir)).isEqualTo(Rotation.NONE);
            assertThat(Orientation.horizontalRotation(dir, directionModulo(index + 1))).isEqualTo(Rotation.CLOCKWISE_90);
            assertThat(Orientation.horizontalRotation(dir, directionModulo(index + 2))).isEqualTo(Rotation.CLOCKWISE_180);
            assertThat(Orientation.horizontalRotation(dir, directionModulo(index + 3))).isEqualTo(Rotation.COUNTERCLOCKWISE_90);
        }
    }

    @Test
    void numberOfClockwise90DegreeRotations() {
        for (int index = 0; index < DIRECTIONS.length; ++index) {
            final Direction dir = DIRECTIONS[index];

            assertThat(Orientation.numberOfClockwise90DegreeRotations(dir, dir)).isEqualTo(0);
            assertThat(Orientation.numberOfClockwise90DegreeRotations(dir, directionModulo(index + 1))).isEqualTo(1);
            assertThat(Orientation.numberOfClockwise90DegreeRotations(dir, directionModulo(index + 2))).isEqualTo(2);
            assertThat(Orientation.numberOfClockwise90DegreeRotations(dir, directionModulo(index + 3))).isEqualTo(3);
        }
    }

}
