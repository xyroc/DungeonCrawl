package com.github.xyroc.dungeoncrawl.util.random;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.TestOnly;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import xiroc.dungeoncrawl.util.random.IRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestOnly
@TestMethodOrder(MethodOrderer.MethodName.class)
public class IRandomTest {

    @Test
    void testBuildEmptyFails() {
        final var builder = new IRandom.Builder<>();
        assertThatThrownBy(builder::build).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void testBuildSuccessful() {
        final var builder = new IRandom.Builder<>();
        builder.add(new Object());

        final var random = builder.build();

        assertThat(random).isNotNull();
    }

    private record TestObject(int number) {
        public static final Codec<TestObject> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("my_number").forGetter(TestObject::number)
        ).apply(instance, TestObject::new));
    }

    private static final Codec<IRandom.Builder<TestObject>> BUILDER_TEST_OBJECT_CODEC = IRandom.makeBuilderCodec(TestObject.CODEC, "test_object", null);
    private static final Codec<IRandom.Builder<Integer>> BUILDER_INT_CODEC = IRandom.makeBuilderCodec(Codec.INT, "integer", null);
    private static final Codec<IRandom.Builder<Either<TestObject, Integer>>> BUILDER_POTENTIALLY_INLINED_OBJECT_CODEC = IRandom.makeBuilderCodec(
            Codec.either(TestObject.CODEC, Codec.INT), "1", null
    );

    @Test
    void serializesEmptyBuilderCorrectly() {
        IRandom.Builder<TestObject> builder = new IRandom.Builder<>();

        JsonElement actual = BUILDER_TEST_OBJECT_CODEC.encodeStart(JsonOps.INSTANCE, builder).result().orElseThrow();
        JsonElement expected = new JsonArray();

        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(JsonArray.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void deserializesEmptyBuilderCorrectly() {
        JsonElement json = new JsonArray();

        IRandom.Builder<TestObject> actual = BUILDER_TEST_OBJECT_CODEC.parse(JsonOps.INSTANCE, json).result().orElseThrow();
        IRandom.Builder<TestObject> expected = new IRandom.Builder<>();

        assertThat(actual).isNotNull();
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void serializesNonPooledPrimitiveBuilderCorrectly() {
        IRandom.Builder<Integer> builder = new IRandom.Builder<>();

        builder.add(42, 1);
        builder.add(16, 2);

        JsonElement expected = JsonParser.parseString("""
                [
                    42,
                    {
                        "integer": 16,
                        "weight": 2
                    }
                ]
                """);

        JsonElement actual = BUILDER_INT_CODEC.encodeStart(JsonOps.INSTANCE, builder).result().orElseThrow();

        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(JsonArray.class);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deserializesNonPooledPrimitiveBuilderCorrectly() {
        JsonElement json = JsonParser.parseString("""
                [
                    42,
                    {
                        "integer": 43,
                        "weight": 71
                    }
                ]
                """);

        IRandom.Builder<Integer> expected = new IRandom.Builder<>();
        expected.add(42);
        expected.add(43, 71);

        IRandom.Builder<Integer> actual = BUILDER_INT_CODEC.parse(JsonOps.INSTANCE, json).result().orElseThrow();

        assertThat(actual).isNotNull();
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void serializesNonPooledMapObjectBuilderCorrectly() {
        IRandom.Builder<TestObject> builder = new IRandom.Builder<>();
        builder.add(new TestObject(16));
        builder.add(new TestObject(32), 8);

        JsonElement expected = JsonParser.parseString("""
                [
                    {
                        "test_object": {
                            "my_number": 16
                        }
                    },
                    {
                        "test_object": {
                            "my_number": 32
                        },
                        "weight": 8
                    }
                ]
                """);

        JsonElement actual = BUILDER_TEST_OBJECT_CODEC.encodeStart(JsonOps.INSTANCE, builder).result().orElseThrow();

        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(JsonArray.class);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deserializesNonPooledMapObjectBuilderCorrectly() {
        JsonElement json = JsonParser.parseString("""
                [
                    {
                        "test_object": {
                            "my_number": 16
                        }
                    },
                    {
                        "test_object": {
                            "my_number": 32
                        },
                        "weight": 8
                    }
                ]
                """);

        IRandom.Builder<TestObject> expected = new IRandom.Builder<>();
        expected.add(new TestObject(16));
        expected.add(new TestObject(32), 8);

        IRandom.Builder<TestObject> actual = BUILDER_TEST_OBJECT_CODEC.parse(JsonOps.INSTANCE, json).result().orElseThrow();

        assertThat(actual).isNotNull();
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

}
