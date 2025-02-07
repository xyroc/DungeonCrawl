package com.github.xyroc.dungeoncrawl.dungeon.tier;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import xiroc.dungeoncrawl.dungeon.tier.TieredResource;

import java.lang.reflect.Type;

import static org.assertj.core.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TieredResourceTest {

    private static final Type TIERED_STRING_TYPE = TypeToken.getParameterized(TieredResource.class, String.class).getType();

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(TIERED_STRING_TYPE, new TieredResource.BuilderSerializer<String>(String.class))
            .create();

    @Test
    void serializesSingleTierBuilderCorrectly() {
        final var builder = new TieredResource.Builder<>("first tier");
        final JsonElement json = GSON.toJsonTree(builder, TIERED_STRING_TYPE);

        assertThat(json).isNotNull();
        assertThat(json.getAsString()).isEqualTo("first tier");

        final var deserializedBuilder = GSON.fromJson(json, TIERED_STRING_TYPE);
        assertThat(deserializedBuilder).isNotNull();
        assertThat(deserializedBuilder).usingRecursiveComparison().isEqualTo(builder);
    }

    @Test
    void serializesMultiTierBuilderCorrectly() {
        final var builder = new TieredResource.Builder<>("first tier")
                .tier("second tier", 2)
                .tier("third tier", 5);
        final JsonElement json = GSON.toJsonTree(builder, TIERED_STRING_TYPE);

        assertThat(json).isNotNull();
        assertThat(json.isJsonObject()).isTrue();
        assertThat(json.getAsJsonObject().has("tier_0")).isTrue();
        assertThat(json.getAsJsonObject().get("tier_0").getAsString()).isEqualTo("first tier");
        assertThat(json.getAsJsonObject().has("tier_2")).isTrue();
        assertThat(json.getAsJsonObject().get("tier_2").getAsString()).isEqualTo("second tier");
        assertThat(json.getAsJsonObject().has("tier_5")).isTrue();
        assertThat(json.getAsJsonObject().get("tier_5").getAsString()).isEqualTo("third tier");

        final var deserializedBuilder = GSON.fromJson(json, TIERED_STRING_TYPE);
        assertThat(deserializedBuilder).isNotNull();
        assertThat(deserializedBuilder).usingRecursiveComparison().isEqualTo(builder);
    }

    @Test
    void deserializeThrowsWhenMissingTierZero() {
        final JsonObject json = new JsonObject();
        json.addProperty("tier_1", "second tier");
        json.addProperty("tier_4", "third tier");

        assertThatThrownBy(() -> GSON.fromJson(json, TIERED_STRING_TYPE)).isInstanceOf(JsonParseException.class);
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
