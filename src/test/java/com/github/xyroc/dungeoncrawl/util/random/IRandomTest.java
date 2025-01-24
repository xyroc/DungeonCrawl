package com.github.xyroc.dungeoncrawl.util.random;

import org.jetbrains.annotations.TestOnly;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import xiroc.dungeoncrawl.util.random.IRandom;

import static org.assertj.core.api.Assertions.*;

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

}
