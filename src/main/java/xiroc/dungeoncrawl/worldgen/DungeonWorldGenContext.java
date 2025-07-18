package xiroc.dungeoncrawl.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;

/**
 * Holds the information necessary to generate any {@code DungeonPiece}.
 *
 * @param primaryTheme the primary theme.
 * @param secondaryTheme the secondary theme.
 * @param foundationHeight the y level above the foundation. {@code Integer.MIN_VALUE} means there is no foundation.
 * @param level the dungeon layer.
 */
public record DungeonWorldGenContext(Holder<PrimaryTheme> primaryTheme, Holder<SecondaryTheme> secondaryTheme, int foundationHeight, int level) {
    public static final Codec<DungeonWorldGenContext> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            PrimaryTheme.HOLDER_CODEC.fieldOf("primary_theme").forGetter(DungeonWorldGenContext::primaryTheme),
            SecondaryTheme.HOLDER_CODEC.fieldOf("secondary_theme").forGetter(DungeonWorldGenContext::secondaryTheme),
            Codec.INT.fieldOf("foundation_height").forGetter(DungeonWorldGenContext::foundationHeight),
            Codec.INT.fieldOf("level").forGetter(DungeonWorldGenContext::level)
    ).apply(builder, DungeonWorldGenContext::new));
}
