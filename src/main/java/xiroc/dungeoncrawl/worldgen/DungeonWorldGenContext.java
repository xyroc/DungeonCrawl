package xiroc.dungeoncrawl.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
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
public record DungeonWorldGenContext(Delegate<PrimaryTheme> primaryTheme, Delegate<SecondaryTheme> secondaryTheme, int foundationHeight, int level) {
    public static final Codec<DungeonWorldGenContext> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            DatapackRegistries.PRIMARY_THEME.delegateCodec().fieldOf("primary_theme").forGetter(DungeonWorldGenContext::primaryTheme),
            DatapackRegistries.SECONDARY_THEME.delegateCodec().fieldOf("secondary_theme").forGetter(DungeonWorldGenContext::secondaryTheme),
            Codec.INT.fieldOf("foundation_height").forGetter(DungeonWorldGenContext::foundationHeight),
            Codec.INT.fieldOf("level").forGetter(DungeonWorldGenContext::level)
    ).apply(builder, DungeonWorldGenContext::new));
}
