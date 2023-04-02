package xiroc.dungeoncrawl.dungeon.blueprint.anchor;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.util.random.ArrayUrn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public interface AnchorProvider {
    AnchorProvider EMPTY = (type) -> null;

    static AnchorProvider of(ResourceLocation anchorType, List<Anchor> entries) {
        if (entries.isEmpty()) {
            return EMPTY;
        }
        return new AnchorProvider() {
            private final ArrayUrn<Anchor> urn = new ArrayUrn<>(entries.toArray(new Anchor[0]), false);

            @Nullable
            @Override
            public ArrayUrn<Anchor> anchors(ResourceLocation type) {
                if (anchorType.equals(type)) {
                    return urn;
                }
                return null;
            }
        };
    }

    static AnchorProvider of(Map<ResourceLocation, List<Anchor>> entries) {
        if (entries.isEmpty()) {
            return EMPTY;
        }
        if (entries.size() == 1) {
            var entry = entries.entrySet().iterator().next();
            return of(entry.getKey(), entry.getValue());
        }

        ImmutableMap.Builder<ResourceLocation, ArrayUrn<Anchor>> builder = ImmutableMap.builder();
        entries.forEach((key, value) -> builder.put(key, new ArrayUrn<>(value.toArray(new Anchor[0]), false)));

        return new AnchorProvider() {
            private final ImmutableMap<ResourceLocation, ArrayUrn<Anchor>> anchors = builder.build();

            @Nullable
            @Override
            public ArrayUrn<Anchor> anchors(ResourceLocation type) {
                return anchors.get(type);
            }
        };
    }

    /**
     * @return The urn that holds all anchors of the specified type, or null if there is none
     */
    @Nullable
    ArrayUrn<Anchor> anchors(ResourceLocation type);
}