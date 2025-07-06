package xiroc.dungeoncrawl.dungeon.blueprint.template.block.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;

import java.util.Optional;

public record FixedTemplateBlockType(@Nullable BlockStateProvider provider) implements TemplateBlockType {
    public static final MapCodec<FixedTemplateBlockType> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(BlockStateProvider.CODEC.optionalFieldOf("block").forGetter(type -> Optional.ofNullable(type.provider)))
                    .apply(instance, (optionalProvider) ->
                            new FixedTemplateBlockType(optionalProvider.orElse(null))));

    @Override
    public BlockStateProvider chooseProvider(PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme) {
        return provider;
    }

    @Override
    public MapCodec<? extends TemplateBlockType> codec() {
        return CODEC;
    }
}
