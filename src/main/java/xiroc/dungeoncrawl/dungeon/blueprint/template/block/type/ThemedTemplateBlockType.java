package xiroc.dungeoncrawl.dungeon.blueprint.template.block.type;

import com.mojang.serialization.Codec;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.BlockChooser;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;

public record ThemedTemplateBlockType(BlockChooser chooser,
                                      Codec<ThemedTemplateBlockType> codec) implements TemplateBlockType {

    @Override
    public BlockStateProvider chooseProvider(PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme) {
        return chooser.chooseProvider(primaryTheme, secondaryTheme);
    }

    public static class Factory {
        private final Codec<ThemedTemplateBlockType> codec = Codec.unit(this::create);
        private final ThemedTemplateBlockType type;

        public Factory(BlockChooser blockChooser) {
            this.type = new ThemedTemplateBlockType(blockChooser, codec);
        }

        public Codec<ThemedTemplateBlockType> getCodec() {
            return codec;
        }

        public ThemedTemplateBlockType create() {
            return type;
        }
    }
}
