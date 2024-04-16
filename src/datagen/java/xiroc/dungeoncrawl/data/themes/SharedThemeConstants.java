package xiroc.dungeoncrawl.data.themes;

import net.minecraft.world.level.block.Blocks;
import xiroc.dungeoncrawl.dungeon.block.provider.RandomBlock;

class SharedThemeConstants {

    public static final RandomBlock HELL_MATERIAL = RandomBlock.builder()
            .add(Blocks.NETHER_BRICKS, 200)
            .add(Blocks.NETHERRACK, 20)
            .add(Blocks.SOUL_SAND, 15)
            .add(Blocks.NETHER_WART_BLOCK, 10)
            .add(Blocks.COAL_BLOCK, 5).build();

}
