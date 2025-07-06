package com.github.xyroc.dungeoncrawl.util.random.value;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import xiroc.dungeoncrawl.util.random.value.Constant;

import static org.assertj.core.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class ConstantTest {

    @Test
    void encodesCorrectly() {
        Constant instance = new Constant(42);

        JsonElement expected = new JsonPrimitive(42);
        JsonElement actual = Constant.CODEC.encodeStart(JsonOps.INSTANCE, instance).result().orElseThrow();

        assertThat(actual).isNotNull();
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void decodesCorrectly() {
        JsonElement json = new JsonPrimitive(42);

        Constant expected = new Constant(42);
        Constant actual = Constant.CODEC.parse(JsonOps.INSTANCE, json).result().orElseThrow();

        assertThat(actual).isNotNull();
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void specifiesRangeCorrectly() {
        Constant instance = new Constant(10);

        assertThat(instance.isAlwaysWithin(10, 10)).isTrue();
        assertThat(instance.isAlwaysWithin(Integer.MIN_VALUE, 10)).isTrue();
        assertThat(instance.isAlwaysWithin(Integer.MIN_VALUE, 9)).isFalse();
        assertThat(instance.isAlwaysWithin(10, Integer.MAX_VALUE)).isTrue();
        assertThat(instance.isAlwaysWithin(11, Integer.MAX_VALUE)).isFalse();

        assertThat(instance.isAlwaysPositive()).isTrue();
        assertThat(instance.isAlwaysNonNegative()).isTrue();
        assertThat(instance.isAlwaysNonPositive()).isFalse();
        assertThat(instance.isAlwaysNegative()).isFalse();
    }
}
