package com.github.xyroc.dungeoncrawl.dungeon.tier;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
    private static final Codec<TieredResource.Builder<String>> STRING_CODEC = new TieredResource.BuilderCodec<>(Codec.STRING);

    @Test
    void serializesSingleTierBuilderCorrectly() {
        final var builder = new TieredResource.Builder<>("first tier");
        final JsonElement json = STRING_CODEC.encodeStart(JsonOps.INSTANCE, builder).result().orElseThrow();

        assertThat(json).isNotNull();
        assertThat(json.getAsString()).isEqualTo("first tier");
    }

    @Test
    void serializesMultiTierBuilderCorrectly() {
        final var builder = new TieredResource.Builder<>("first tier")
                .tier("third tier", 5)
                .tier("second tier", 2);
        final JsonElement json = STRING_CODEC.encodeStart(JsonOps.INSTANCE, builder).result().orElseThrow();

        assertThat(json).isNotNull();
        assertThat(json.isJsonObject()).isTrue();
        assertThat(json.getAsJsonObject().has("tier_0")).isTrue();
        assertThat(json.getAsJsonObject().get("tier_0").getAsString()).isEqualTo("first tier");
        assertThat(json.getAsJsonObject().has("tier_2")).isTrue();
        assertThat(json.getAsJsonObject().get("tier_2").getAsString()).isEqualTo("second tier");
        assertThat(json.getAsJsonObject().has("tier_5")).isTrue();
        assertThat(json.getAsJsonObject().get("tier_5").getAsString()).isEqualTo("third tier");
    }

    @Test
    void deserializeErrorsWhenMissingTierZero() {
        final JsonObject json = new JsonObject();
        json.addProperty("tier_1", "second tier");
        json.addProperty("tier_4", "third tier");

        final DataResult<?> decoded = STRING_CODEC.decode(JsonOps.INSTANCE, json);

        assertThat(decoded.error().isPresent()).isTrue();
    }

    @Test
    void deserializesSingleTierBuilderCorrectly() {
        final JsonElement json = new JsonPrimitive("first tier");

        final var actualBuilder = STRING_CODEC.decode(JsonOps.INSTANCE, json).result().orElseThrow().getFirst();

        assertThat(actualBuilder).isNotNull();
        final var actualResource = actualBuilder.build();

        assertThat(actualResource).isInstanceOf(TieredResource.SingleTier.class);
        assertThat(actualResource.forTier(0)).isEqualTo("first tier");
    }

    @Test
    void deserializesMultiTierBuilderCorrectly() {
        final JsonObject json = new JsonObject();
        json.addProperty("tier_0", "first tier");
        json.addProperty("tier_3", "second tier");

        final var actualBuilder = STRING_CODEC.decode(JsonOps.INSTANCE, json).result().orElseThrow().getFirst();

        assertThat(actualBuilder).isNotNull();
        final var actualResource = actualBuilder.build();

        assertThat(actualResource).isInstanceOf(TieredResource.MultiTier.class);
        assertThat(actualResource.forTier(0)).isEqualTo("first tier");
        assertThat(actualResource.forTier(3)).isEqualTo("second tier");
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
