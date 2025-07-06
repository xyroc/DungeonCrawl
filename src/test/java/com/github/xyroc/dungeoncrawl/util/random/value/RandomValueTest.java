package com.github.xyroc.dungeoncrawl.util.random.value;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import xiroc.dungeoncrawl.util.random.value.Constant;
import xiroc.dungeoncrawl.util.random.value.RandomValue;
import xiroc.dungeoncrawl.util.random.value.Range;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class RandomValueTest {

    @Test
    void decodesConstant() {
        JsonElement json = Constant.CODEC.encodeStart(JsonOps.INSTANCE, new Constant(42)).result().orElseThrow();

        RandomValue decoded = RandomValue.CODEC.parse(JsonOps.INSTANCE, json).result().orElseThrow();

        assertThat(decoded).isInstanceOf(Constant.class);
    }

    @Test
    void decodesRange() {
        JsonElement json = Range.CODEC.encodeStart(JsonOps.INSTANCE, new Range(24, 72)).result().orElseThrow();

        RandomValue decoded = RandomValue.CODEC.parse(JsonOps.INSTANCE, json).result().orElseThrow();

        assertThat(decoded).isInstanceOf(Range.class);
    }

    @Test
    void encodesConstant() {
        Constant instance = new Constant(42);

        JsonElement expected = Constant.CODEC.encodeStart(JsonOps.INSTANCE, instance).result().orElseThrow();

        JsonElement actual = RandomValue.CODEC.encodeStart(JsonOps.INSTANCE, instance).result().orElseThrow();

        assertThat(actual).isNotNull();
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void encodesRange() {
        Range instance = new Range(24, 72);

        JsonElement expected = Range.CODEC.encodeStart(JsonOps.INSTANCE, instance).result().orElseThrow();

        JsonElement actual = RandomValue.CODEC.encodeStart(JsonOps.INSTANCE, instance).result().orElseThrow();

        assertThat(actual).isNotNull();
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

}
