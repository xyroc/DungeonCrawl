/*
        Dungeon Crawl, a procedural dungeon generator for Minecraft 1.14 and later.
        Copyright (C) 2020

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package xiroc.dungeoncrawl.dungeon.monster;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import xiroc.dungeoncrawl.config.Config;

import java.util.ArrayList;
import java.util.List;

public class EquipmentHelper {
    /**
     * Creates a randomized item stack from the given item.
     * If possible, the item is enchanted, damaged, dyed, and given banner patterns.
     * The enchantment level depends on the stage.
     *
     * @param item           The item to instantiate.
     * @param random         The source of randomness.
     * @param stage          The dungeon stage the item is generated it.
     * @param registryAccess Registry access for enchanting and banner patterns.
     * @return The resulting item stack.
     */
    public static ItemStack randomizedItem(Item item, RandomSource random, int stage, RegistryAccess registryAccess) {
        ItemStack itemStack = EnchantmentHelper.enchantItem(random, new ItemStack(item), 10 + 3 * stage, registryAccess,
                registryAccess.registryOrThrow(Registries.ENCHANTMENT).getTag(EnchantmentTags.NON_TREASURE));
        if (itemStack.isDamageableItem()) {
            itemStack.setDamageValue(random.nextInt(Math.max(1, item.getMaxDamage(itemStack) / 2)));
        }
        if (itemStack.is(ItemTags.DYEABLE)) {
            DyedItemColor colorComponent = new DyedItemColor(random.nextInt(0x1000000), true);
            itemStack.set(DataComponents.DYED_COLOR, colorComponent);
        }
        if (itemStack.has(DataComponents.BANNER_PATTERNS)) {
            itemStack.set(DataComponents.BANNER_PATTERNS, createPatterns(random, registryAccess));
        }
        if (itemStack.is(ItemTags.TRIMMABLE_ARMOR) && random.nextFloat() <= Config.ARMOR_TRIM_CHANCE.get()) {
            itemStack.set(DataComponents.TRIM, createArmorTrim(random, registryAccess));
        }
        return itemStack;
    }

    private static final int PATTERN_LAYERS = 3;

    /**
     * Creates an assortment of random banner patterns from the banner patterns registry.
     *
     * @param rand           The source of randomness.
     * @param registryAccess Access to the banner pattern registry.
     * @return The banner patterns
     */
    public static BannerPatternLayers createPatterns(RandomSource rand, RegistryAccess registryAccess) {
        Registry<BannerPattern> bannerPatterns = registryAccess.registryOrThrow(Registries.BANNER_PATTERN);
        List<BannerPatternLayers.Layer> layers = new ArrayList<>();
        for (int i = 0; i < PATTERN_LAYERS; ++i) {
            Holder<BannerPattern> pattern = bannerPatterns.getRandom(rand).orElseThrow();
            layers.add(new BannerPatternLayers.Layer(pattern, DyeColor.byId(rand.nextInt(16))));
        }
        return new BannerPatternLayers(layers);
    }

    /**
     * Creates a random armor trim.
     *
     * @param random The source of randomness.
     * @param registryAccess Registry access to get all armor trims materials and patterns.
     * @return The resulting armor trim.
     */
    private static ArmorTrim createArmorTrim(RandomSource random,  RegistryAccess registryAccess) {
        Registry<TrimMaterial> trimMaterials = registryAccess.registryOrThrow(Registries.TRIM_MATERIAL);
        Registry<TrimPattern> trimPatters = registryAccess.registryOrThrow(Registries.TRIM_PATTERN);
        return new ArmorTrim(trimMaterials.getRandom(random).orElseThrow(), trimPatters.getRandom(random).orElseThrow());
    }
}
