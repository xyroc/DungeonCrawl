package com.github.xyroc.dungeoncrawl.util.bounds;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;

import static org.assertj.core.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class BoundingBoxBuilderTest {

    @Test
    void testCreateFromPosition() {
        final Vec3i position = new Vec3i(12, -7, 5);
        final BoundingBoxBuilder builder = BoundingBoxBuilder.fromPosition(position);

        assertThat(builder).isNotNull();
        assertThat(builder.minX()).isEqualTo(position.getX());
        assertThat(builder.maxX()).isEqualTo(position.getX());
        assertThat(builder.minY()).isEqualTo(position.getY());
        assertThat(builder.maxY()).isEqualTo(position.getY());
        assertThat(builder.minZ()).isEqualTo(position.getZ());
        assertThat(builder.maxZ()).isEqualTo(position.getZ());
    }

    @Test
    void testCreateFromBoundingBox() {
        final BoundingBox expected = new BoundingBox(1, 2, 3, 4, 5, 6);
        final BoundingBoxBuilder builder = new BoundingBoxBuilder(expected);

        assertThat(builder).isNotNull();
        assertThat(builder.minX()).isEqualTo(expected.minX());
        assertThat(builder.maxX()).isEqualTo(expected.maxX());
        assertThat(builder.minY()).isEqualTo(expected.minY());
        assertThat(builder.maxY()).isEqualTo(expected.maxY());
        assertThat(builder.minZ()).isEqualTo(expected.minZ());
        assertThat(builder.maxZ()).isEqualTo(expected.maxZ());
    }

    @Test
    void testCreateFromCorners() {
        final BoundingBoxBuilder builder = BoundingBoxBuilder.fromCorners(
                new Vec3i(2, 4, 7),
                new Vec3i(1, 5, 3)
        );

        assertThat(builder).isNotNull();
        assertThat(builder.minX()).isEqualTo(1);
        assertThat(builder.maxX()).isEqualTo(2);
        assertThat(builder.minY()).isEqualTo(4);
        assertThat(builder.maxY()).isEqualTo(5);
        assertThat(builder.minZ()).isEqualTo(3);
        assertThat(builder.maxZ()).isEqualTo(7);
    }

    @Test
    void testCreateSetsCorrectCoordinates() {
        final BoundingBox expected = new BoundingBox(1, 2, 3, 4, 5, 6);
        final BoundingBoxBuilder builder = new BoundingBoxBuilder(expected);

        final BoundingBox actual = builder.create();

        assertThat(actual).isNotNull();
        assertThat(actual.minX()).isEqualTo(expected.minX());
        assertThat(actual.maxX()).isEqualTo(expected.maxX());
        assertThat(actual.minY()).isEqualTo(expected.minY());
        assertThat(actual.maxY()).isEqualTo(expected.maxY());
        assertThat(actual.minZ()).isEqualTo(expected.minZ());
        assertThat(actual.maxZ()).isEqualTo(expected.maxZ());
    }

    @Test
    void testEncapsulateLargerBox() {
        final BoundingBoxBuilder smaller = new BoundingBoxBuilder(0, 0, 0, 2, 2, 2);
        final BoundingBoxBuilder larger = new BoundingBoxBuilder(-1, -1, -1, 3, 3, 3);

        smaller.encapsulate(larger);

        assertThat(smaller.minX).isEqualTo(larger.minX());
        assertThat(smaller.maxX).isEqualTo(larger.maxX());
        assertThat(smaller.minY).isEqualTo(larger.minY());
        assertThat(smaller.maxY).isEqualTo(larger.maxY());
        assertThat(smaller.minZ).isEqualTo(larger.minZ());
        assertThat(smaller.maxZ).isEqualTo(larger.maxZ());
    }

    @Test
    void testEncapsulateSmallerBox() {
        final BoundingBoxBuilder smaller = new BoundingBoxBuilder(0, 0, 0, 2, 2, 2);
        final BoundingBoxBuilder larger = new BoundingBoxBuilder(-1, -1, -1, 3, 3, 3);

        larger.encapsulate(smaller);

        assertThat(larger.minX()).isEqualTo(-1);
        assertThat(larger.minY()).isEqualTo(-1);
        assertThat(larger.minZ()).isEqualTo(-1);
        assertThat(larger.maxX()).isEqualTo(3);
        assertThat(larger.maxY()).isEqualTo(3);
        assertThat(larger.maxZ()).isEqualTo(3);
    }

    @Test
    void testMove() {
        final BoundingBoxBuilder builder = new BoundingBoxBuilder(0, 0, 0, 2, 2, 2);

        builder.move(new Vec3i(1, 2, 3));

        assertThat(builder.minX()).isEqualTo(1);
        assertThat(builder.minY()).isEqualTo(2);
        assertThat(builder.minZ()).isEqualTo(3);
        assertThat(builder.maxX()).isEqualTo(3);
        assertThat(builder.maxY()).isEqualTo(4);
        assertThat(builder.maxZ()).isEqualTo(5);
    }

}
