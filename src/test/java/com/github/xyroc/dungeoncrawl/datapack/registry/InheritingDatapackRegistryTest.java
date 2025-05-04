package com.github.xyroc.dungeoncrawl.datapack.registry;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import xiroc.dungeoncrawl.datapack.DatapackDirectory;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistryBuilder;
import xiroc.dungeoncrawl.datapack.registry.InheritingBuilder;
import xiroc.dungeoncrawl.datapack.registry.InheritingDatapackRegistry;
import xiroc.dungeoncrawl.exception.DatapackLoadException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class InheritingDatapackRegistryTest {

    private static class InheritingStringBuilder extends InheritingBuilder<String, InheritingStringBuilder> {
        private final StringBuilder builder;

        private InheritingStringBuilder(String base) {
            this.builder = new StringBuilder(base);
        }

        @Override
        public InheritingStringBuilder inherit(InheritingStringBuilder from) {
            builder.append(from.builder);
            return this;
        }

        @Override
        public String build() {
            return builder.toString();
        }

        private static class Serializer implements JsonSerializer<InheritingStringBuilder>, JsonDeserializer<InheritingStringBuilder> {
            private static final String KEY_BASE = "base";

            @Override
            public InheritingStringBuilder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject object = json.getAsJsonObject();
                return new InheritingStringBuilder(object.get(KEY_BASE).getAsString());
            }

            @Override
            public JsonElement serialize(InheritingStringBuilder src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject object = new JsonObject();
                object.addProperty(KEY_BASE, src.builder.toString());
                return object;
            }
        }
    }

    private static final DatapackDirectory DIR = new DatapackDirectory("test");
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(InheritingStringBuilder.class, InheritingBuilder.WrappedSerializer.of(new InheritingStringBuilder.Serializer()))
            .create();

    private InheritingDatapackRegistry<String, InheritingStringBuilder> registry;
    private ResourceManager resourceManager;

    @BeforeEach
    void init() {
        registry = new DatapackRegistryBuilder<String>(DIR)
                .inheriting((reader) -> GSON.fromJson(reader, InheritingStringBuilder.class));
        resourceManager = mock();
    }

    @Test
    void throwsOnSelfInheritance() throws IOException {
        ResourceLocation testItemAbsoluteKey = DIR.resource("test", "self_inheritance.json");
        ResourceLocation testItemRelativeKey = DIR.key(testItemAbsoluteKey, ".json");
        Resource testItemResource = mock();

        when(testItemResource.getInputStream()).thenReturn(IOUtils.toInputStream(
                GSON.toJson(new InheritingStringBuilder("self_inheritance_test_instance").addParent(testItemRelativeKey)),
                Charsets.UTF_8));
        when(resourceManager.listResources(Mockito.any(), Mockito.any())).thenReturn(List.of(testItemAbsoluteKey));
        when(resourceManager.getResources(testItemAbsoluteKey)).thenReturn(List.of(testItemResource));

        assertThatThrownBy(() -> registry.reload(resourceManager))
                .isInstanceOf(DatapackLoadException.class)
                .message()
                .contains(testItemRelativeKey.toString());
    }

    @Test
    void throwsOnInheritanceCycle() throws IOException {
        ResourceLocation itemAKey = DIR.resource("test", "inheritance_cycle_item_a.json");
        ResourceLocation itemBKey = DIR.resource("test", "inheritance_cycle_item_b.json");
        ResourceLocation itemARelativeKey = DIR.key(itemAKey, ".json");
        ResourceLocation itemBRelativeKey = DIR.key(itemBKey, ".json");

        Resource itemAResource = mock();
        Resource itemBResource = mock();

        when(itemAResource.getInputStream()).thenReturn(IOUtils.toInputStream(
                GSON.toJson(new InheritingStringBuilder("item_a").addParent(itemBRelativeKey)),
                Charsets.UTF_8
        ));

        when(itemBResource.getInputStream()).thenReturn(IOUtils.toInputStream(
                GSON.toJson(new InheritingStringBuilder("item_b").addParent(itemARelativeKey)),
                Charsets.UTF_8
        ));

        when(resourceManager.listResources(any(), any())).thenReturn(List.of(
                itemAKey, itemBKey
        ));

        when(resourceManager.getResources(itemAKey)).thenReturn(List.of(itemAResource));
        when(resourceManager.getResources(itemBKey)).thenReturn(List.of(itemBResource));

        assertThatThrownBy(() -> registry.reload(resourceManager)).isInstanceOf(DatapackLoadException.class)
                .message()
                .contains("Cycle")
                .contains(itemARelativeKey.toString())
                .contains(itemBRelativeKey.toString());
    }

    @Test
    void throwsOnMissingEntry() throws IOException {
        ResourceLocation itemKey = DIR.resource("test", "item.json");

        ResourceLocation itemRelativeKey = DIR.key(itemKey, ".json");
        ResourceLocation nonexistentItemRelativeKey = new ResourceLocation("test:no");

        Resource itemResource = mock();

        when(itemResource.getInputStream()).thenReturn(IOUtils.toInputStream(
                GSON.toJson(new InheritingStringBuilder("some_string").addParent(nonexistentItemRelativeKey)),
                Charsets.UTF_8
        ));

        when(resourceManager.listResources(any(), any())).thenReturn(List.of(itemKey));
        when(resourceManager.getResources(itemKey)).thenReturn(List.of(itemResource));

        assertThatThrownBy(() -> registry.reload(resourceManager)).isInstanceOf(DatapackLoadException.class)
                .message()
                .contains(itemRelativeKey.toString())
                .contains(nonexistentItemRelativeKey.toString());
    }

    @Test
    void layeredResourcesLoadInCorrectOrder() throws IOException {
        ResourceLocation itemKey = DIR.resource("test", "item.json");
        ResourceLocation itemRelativeKey = DIR.key(itemKey, ".json");

        Resource itemResourceTop = mock();
        Resource itemResourceMiddle = mock();
        Resource itemResourceBottom = mock();

        when(itemResourceBottom.getInputStream()).thenReturn(IOUtils.toInputStream(
                GSON.toJson(new InheritingStringBuilder("Bot").replace(false)),
                Charsets.UTF_8));
        when(itemResourceMiddle.getInputStream()).thenReturn(IOUtils.toInputStream(
                GSON.toJson(new InheritingStringBuilder("Mid").replace(false)),
                Charsets.UTF_8));
        when(itemResourceTop.getInputStream()).thenReturn(IOUtils.toInputStream(
                GSON.toJson(new InheritingStringBuilder("Top").replace(false)),
                Charsets.UTF_8));

        when(resourceManager.listResources(any(), any())).thenReturn(List.of(itemKey));
        when(resourceManager.getResources(itemKey)).thenReturn(List.of(
                itemResourceBottom,
                itemResourceMiddle,
                itemResourceTop
        ));

        registry.reload(resourceManager);

        assertThat(registry.delegateOrThrow(itemRelativeKey).get()).isEqualTo("TopMidBot");
    }

}
