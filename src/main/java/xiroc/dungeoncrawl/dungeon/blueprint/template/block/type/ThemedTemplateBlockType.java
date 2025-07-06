package xiroc.dungeoncrawl.dungeon.blueprint.template.block.type;

import com.mojang.serialization.MapCodec;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.BlockChooser;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;

public record ThemedTemplateBlockType(BlockChooser chooser,
                                      MapCodec<ThemedTemplateBlockType> codec) implements TemplateBlockType {

    @Override
    public BlockStateProvider chooseProvider(PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme) {
        return chooser.chooseProvider(primaryTheme, secondaryTheme);
    }

    public static class Factory {
        private final MapCodec<ThemedTemplateBlockType> codec = MapCodec.unit(this::create);
        private final ThemedTemplateBlockType type;

        public Factory(BlockChooser blockChooser) {
            this.type = new ThemedTemplateBlockType(blockChooser, codec);
        }

        public MapCodec<ThemedTemplateBlockType> getCodec() {
            return codec;
        }

        public ThemedTemplateBlockType create() {
            return type;
        }
    }
}
