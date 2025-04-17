package xiroc.dungeoncrawl.dungeon.blueprint.template.block;

import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;

import java.util.function.Function;

/**
 * Supplies a {@code BlockStateProvider} based on the {@code PrimaryTheme} and {@code SecondaryTheme} when calling {@code chooseProvider()}.
 * Used for theming purposes such as mapping a {@code TemplateBlock} to a {@code BlockStateProvider}.
 */
public interface BlockChooser {
    BlockStateProvider chooseProvider(PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme);

    static BlockChooser fromPrimaryTheme(Function<PrimaryTheme, BlockStateProvider> chooser) {
        return (primaryTheme, secondaryTheme) -> chooser.apply(primaryTheme);
    }

    static BlockChooser fromSecondaryTheme(Function<SecondaryTheme, BlockStateProvider> chooser) {
        return (primaryTheme, secondaryTheme) -> chooser.apply(secondaryTheme);
    }
}
