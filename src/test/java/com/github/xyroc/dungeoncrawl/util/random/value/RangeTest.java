package com.github.xyroc.dungeoncrawl.util.random.value;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import xiroc.dungeoncrawl.util.random.value.Range;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class RangeTest {

    private static final Range alwaysPositive = new Range(1, 10);
    private static final Range neverNegative = new Range(0, 10);
    private static final Range neverPositive = new Range(-10, 0);
    private static final Range alwaysNegative = new Range(-10, -1);

    @Test
    void encodesCorrectly() {
        Range instance = new Range(-7, 15);

        JsonObject expected = new JsonObject();
        expected.addProperty("min", -7);
        expected.addProperty("max", 15);

        JsonElement actual = Range.CODEC.encodeStart(JsonOps.INSTANCE, instance).result().orElseThrow();

        assertThat(actual).isNotNull();
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void decodesCorrectly() {
        Range expected = new Range(-902, 36);

        JsonObject json = new JsonObject();
        json.addProperty("min", expected.min());
        json.addProperty("max", expected.max());

        Range actual = Range.CODEC.parse(JsonOps.INSTANCE, json).result().orElseThrow();

        assertThat(actual).isNotNull();
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void specifiesRangeCorrectly() {
        Range instance = new Range(-5, 5);

        assertThat(instance.isAlwaysWithin(-5, 5)).isTrue();
        assertThat(instance.isAlwaysWithin(Integer.MIN_VALUE, 5)).isTrue();
        assertThat(instance.isAlwaysWithin(-5, Integer.MAX_VALUE)).isTrue();
        assertThat(instance.isAlwaysWithin(Integer.MIN_VALUE, Integer.MAX_VALUE)).isTrue();

        assertThat(instance.isAlwaysWithin(-5, 4)).isFalse();
        assertThat(instance.isAlwaysWithin(-4, 5)).isFalse();
        assertThat(instance.isAlwaysWithin(0, 0)).isFalse();
    }

    @Test
    void checksPositiveRangeCorrectly() {
        assertThat(alwaysPositive.isAlwaysPositive()).isTrue();
        assertThat(neverNegative.isAlwaysPositive()).isFalse();
        assertThat(neverPositive.isAlwaysPositive()).isFalse();
        assertThat(alwaysNegative.isAlwaysPositive()).isFalse();

        assertThat(alwaysPositive.isAlwaysNonNegative()).isTrue();
        assertThat(neverNegative.isAlwaysNonNegative()).isTrue();
        assertThat(neverPositive.isAlwaysNonNegative()).isFalse();
        assertThat(alwaysNegative.isAlwaysNonNegative()).isFalse();
    }

    @Test
    void specifiesNegativeRangeCorrectly() {
        assertThat(alwaysPositive.isAlwaysNegative()).isFalse();
        assertThat(neverNegative.isAlwaysNegative()).isFalse();
        assertThat(neverPositive.isAlwaysNegative()).isFalse();
        assertThat(alwaysNegative.isAlwaysNegative()).isTrue();

        assertThat(alwaysPositive.isAlwaysNonPositive()).isFalse();
        assertThat(neverNegative.isAlwaysNonPositive()).isFalse();
        assertThat(neverPositive.isAlwaysNonPositive()).isTrue();
        assertThat(alwaysNegative.isAlwaysNonPositive()).isTrue();
    }

}
