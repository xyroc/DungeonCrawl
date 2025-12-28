package com.github.xyroc.dungeoncrawl.dungeon.tier;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import xiroc.dungeoncrawl.dungeon.tier.TieredResource;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TieredResourceTest {
    private static final Codec<TieredResource<String>> STRING_CODEC = TieredResource.Codecs.makeCodec(Codec.STRING);

    @Test
    void serializesSingleTierBuilderCorrectly() {
        final var instance = new TieredResource.Builder<>("first tier").build();
        final JsonElement json = STRING_CODEC.encodeStart(JsonOps.INSTANCE, instance).result().orElseThrow();

        assertThat(json).isNotNull();
        assertThat(json.getAsString()).isEqualTo("first tier");
    }

    @Test
    void serializesMultiTierBuilderCorrectly() {
        final var instance = new TieredResource.Builder<>("first tier")
                .tier("third tier", 5)
                .tier("second tier", 2)
                .build();
        final JsonElement json = STRING_CODEC.encodeStart(JsonOps.INSTANCE, instance).result().orElseThrow();

        final JsonElement expected = JsonParser.parseString("""
                {
                    "0": "first tier",
                    "2": "second tier",
                    "5": "third tier"
                }
                """);

        assertThat(json).isNotNull();
        assertThat(json).isEqualTo(expected);
    }

    @Test
    void deserializeErrorsWhenMissingTierZero() {
        final JsonObject json = JsonParser.parseString("""
                {
                    "1": "second tier",
                    "4": "third tier"
                }
                """).getAsJsonObject();

        final DataResult<?> decoded = STRING_CODEC.decode(JsonOps.INSTANCE, json);

        assertThat(decoded.error().isPresent()).isTrue();
        assertThat(decoded.error().orElseThrow().message()).containsIgnoringCase("tier 0");
    }

    @Test
    void deserializesSingleTierBuilderCorrectly() {
        final JsonElement json = new JsonPrimitive("first tier");

        final var deserialized = STRING_CODEC.decode(JsonOps.INSTANCE, json).result().orElseThrow().getFirst();

        assertThat(deserialized).isNotNull();
        assertThat(deserialized).isInstanceOf(TieredResource.SingleTier.class);
        assertThat(deserialized.forTier(0)).isEqualTo("first tier");
    }

    @Test
    void deserializesMultiTierBuilderCorrectly() {
        final JsonElement json = JsonParser.parseString("""
                {
                    "0": "first tier",
                    "3": "second tier"
                }
                """);

        final var deserialized = STRING_CODEC.decode(JsonOps.INSTANCE, json).result().orElseThrow().getFirst();

        assertThat(deserialized).isNotNull();
        assertThat(deserialized).isInstanceOf(TieredResource.MultiTier.class);
        assertThat(deserialized.forTier(0)).isEqualTo("first tier");
        assertThat(deserialized.forTier(3)).isEqualTo("second tier");
    }

    @Test
    void buildsSingleTierResource() {
        final var builder = new TieredResource.Builder<>(new Object());
        final var tieredResource = builder.build();

        assertThat(tieredResource).isNotNull();
        assertThat(tieredResource).isInstanceOf(TieredResource.SingleTier.class);
    }

    @Test
    void buildsMultiTierResource() {
        final var builder = new TieredResource.Builder<>(new Object())
                .tier(new Object(), 2);
        final var tieredResource = builder.build();

        assertThat(tieredResource).isNotNull();
        assertThat(tieredResource).isInstanceOf(TieredResource.MultiTier.class);
    }

    @Test
    void singleTierReturnsCorrectResource() {
        final String resource = "resource";
        final var tieredResource = new TieredResource.SingleTier<>(resource);

        assertThat(tieredResource.forTier(0)).isEqualTo(resource);
        assertThat(tieredResource.forTier(1)).isEqualTo(resource);
    }

    @Test
    void multiTierReturnsCorrectResource() {
        final var tieredResource = new TieredResource.MultiTier<>("first tier", ImmutableList.of(
                new TieredResource.Tier<>("second tier", 3),
                new TieredResource.Tier<>("third tier", 5))
        );

        assertThat(tieredResource.forTier(0)).isEqualTo("first tier");
        assertThat(tieredResource.forTier(1)).isEqualTo("first tier");
        assertThat(tieredResource.forTier(2)).isEqualTo("first tier");
        assertThat(tieredResource.forTier(3)).isEqualTo("second tier");
        assertThat(tieredResource.forTier(4)).isEqualTo("second tier");
        assertThat(tieredResource.forTier(5)).isEqualTo("third tier");
        assertThat(tieredResource.forTier(Integer.MAX_VALUE)).isEqualTo("third tier");
    }

}
