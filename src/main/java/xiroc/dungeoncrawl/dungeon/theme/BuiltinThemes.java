package xiroc.dungeoncrawl.dungeon.theme;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import xiroc.dungeoncrawl.datapack.DatapackNamespaces;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;

import java.util.function.BiConsumer;

public interface BuiltinThemes {
    ResourceLocation DEFAULT = key("default");

    /*
     * These builtin themes will only be used in case a theme can't be found
     * and the default theme can't be found either.
     */
    PrimaryTheme DEFAULT_PRIMARY = new PrimaryTheme.Builder()
            .masonry(new SingleBlock(Blocks.COBBLESTONE))
            .pillar(new SingleBlock(Blocks.STONE_BRICKS))
            .floor(new SingleBlock(Blocks.GRAVEL))
            .fluid(new SingleBlock(Blocks.WATER))
            .fencing(new SingleBlock(Blocks.IRON_BARS))
            .stairs(new SingleBlock(Blocks.COBBLESTONE_STAIRS))
            .slab(new SingleBlock(Blocks.COBBLESTONE_SLAB))
            .wall(new SingleBlock(Blocks.COBBLESTONE_WALL)).build();

    SecondaryTheme DEFAULT_SECONDARY = new SecondaryTheme.Builder()
            .material(new SingleBlock(Blocks.OAK_PLANKS))
            .pillar(new SingleBlock(Blocks.OAK_LOG))
            .stairs(new SingleBlock(Blocks.OAK_STAIRS))
            .slab(new SingleBlock(Blocks.OAK_SLAB))
            .door(new SingleBlock(Blocks.OAK_DOOR))
            .trapdoor(new SingleBlock(Blocks.OAK_TRAPDOOR))
            .fence(new SingleBlock(Blocks.OAK_FENCE))
            .fenceGate(new SingleBlock(Blocks.OAK_FENCE_GATE))
            .button(new SingleBlock(Blocks.OAK_BUTTON))
            .pressurePlate(new SingleBlock(Blocks.OAK_PRESSURE_PLATE)).build();

    static void registerPrimary(BiConsumer<ResourceLocation, PrimaryTheme> primaryThemes) {
        primaryThemes.accept(DEFAULT, DEFAULT_PRIMARY);
    }

    static void registerSecondary(BiConsumer<ResourceLocation, SecondaryTheme> secondaryThemes) {
        secondaryThemes.accept(DEFAULT, DEFAULT_SECONDARY);
    }

    private static ResourceLocation key(String path) {
        return new ResourceLocation(DatapackNamespaces.BUILT_IN, path);
    }
}