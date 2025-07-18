package xiroc.dungeoncrawl.dungeon.type.level;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.util.StorageHelper;
import xiroc.dungeoncrawl.util.random.IRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record CorridorStyle(ImmutableList<IRandom<Holder<Blueprint>>> segments, IRandom<Holder<Blueprint>> sideSegments) {
    public interface Codecs {
        Codec<CorridorStyle> DIRECT = Builder.CODEC.comapFlatMap(StorageHelper.tryToApply(Builder::build), Builder::new);
        Codec<IRandom.Builder<CorridorStyle>> RANDOM_BUILDER = IRandom.makeBuilderCodec(DIRECT, "style", null);
        Codec<IRandom<CorridorStyle>> RANDOM = IRandom.makeCodec(RANDOM_BUILDER);
    }

    public static class Builder {
        public static final Codec<Builder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Blueprint.RANDOM_HOLDER_CODEC.listOf().fieldOf("segments").forGetter(builder -> builder.segments),
                Blueprint.RANDOM_HOLDER_CODEC.fieldOf("side_segments").forGetter(builder -> builder.sideSegments)
        ).apply(instance, Builder::new));

        private final List<IRandom<Holder<Blueprint>>> segments;

        @Nullable
        private IRandom<Holder<Blueprint>> sideSegments = null;

        public Builder() {
            this(new ArrayList<>(), null);
        }

        private Builder(CorridorStyle instance) {
            this(instance.segments(), instance.sideSegments());
        }

        private Builder(List<IRandom<Holder<Blueprint>>> segments, @Nullable IRandom<Holder<Blueprint>> sideSegments) {
            this.segments = segments;
            this.sideSegments = sideSegments;
        }

        public Builder segment(IRandom<Holder<Blueprint>> segment) {
            segments.add(segment);
            return this;
        }

        public Builder sideSegments(@Nullable IRandom<Holder<Blueprint>> sideSegments) {
            this.sideSegments = sideSegments;
            return this;
        }

        public CorridorStyle build() {
            Objects.requireNonNull(sideSegments, "No side segment blueprints were provided");
            return new CorridorStyle(ImmutableList.copyOf(segments), sideSegments);
        }
    }
}
