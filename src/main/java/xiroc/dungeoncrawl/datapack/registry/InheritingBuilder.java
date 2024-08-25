package xiroc.dungeoncrawl.datapack.registry;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public abstract class InheritingBuilder<T, B extends InheritingBuilder<T, B>> {
    private static final String KEY_PARENT = "inherit";
    private static final String KEY_REPLACE = "replace";
    private static final boolean REPLACE_BY_DEFAULT = true;

    @Nullable
    protected ResourceLocation parent = null;
    protected boolean replace = REPLACE_BY_DEFAULT;

    public InheritingBuilder<T, B> parent(@Nullable ResourceLocation parent) {
        this.parent = parent;
        return this;
    }

    public InheritingBuilder<T, B> replace(boolean replace) {
        this.replace = replace;
        return this;
    }

    @Nullable
    public ResourceLocation parent() {
        return parent;
    }

    public boolean replace() {
        return replace;
    }

    public abstract B inherit(B from);

    public abstract T build();

    public void deserializeBase(JsonObject json) {
        parent = json.has(KEY_PARENT) ? new ResourceLocation(json.get(KEY_PARENT).getAsString()) : null;
        replace = json.has(KEY_REPLACE) ? json.get(KEY_REPLACE).getAsBoolean() : REPLACE_BY_DEFAULT;
    }

    public void serializeBase(JsonObject json) {
        if (parent != null) {
            json.addProperty(KEY_PARENT, parent.toString());
        }
        if (replace != REPLACE_BY_DEFAULT) {
            json.addProperty(KEY_REPLACE, replace);
        }
    }

    public static <T, B extends InheritingBuilder<T, B>> B inheritOrReplace(B primary, B secondary) {
        return primary.replace() ? primary : primary.inherit(secondary);
    }

    @Nullable
    public static <T, B extends InheritingBuilder<T, B>> B inheritOrReplaceOrChoose(@Nullable B primary, @Nullable B secondary) {
        if (secondary != null && primary != null) {
            return inheritOrReplace(primary, secondary);
        }
        return choose(primary, secondary);
    }

    @Nullable
    public static <T> T choose(@Nullable T primary, @Nullable T secondary) {
        return primary != null ? primary : secondary;
    }
}
