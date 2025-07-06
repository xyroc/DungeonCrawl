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

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class EquipmentHelper {
    public static ItemStack enchantAndDamage(Item item, RandomSource rand, int stage, RegistryAccess registryAccess) {
        ItemStack itemStack = EnchantmentHelper.enchantItem(rand, new ItemStack(item), 10 + 3 * stage, registryAccess,
                registryAccess.registryOrThrow(Registries.ENCHANTMENT).getTag(EnchantmentTags.NON_TREASURE));
        if (itemStack.isDamageableItem()) {
            itemStack.setDamageValue(rand.nextInt(Math.max(1, item.getMaxDamage(itemStack) / 2)));
        }
        return itemStack;
    }

    public static ItemStack createArmorPiece(Item item, RandomSource random, int stage, RegistryAccess registryAccess) {
        ItemStack armorPiece = enchantAndDamage(item, random, stage, registryAccess);
        if (armorPiece.is(ItemTags.DYEABLE)) {
            DyedItemColor colorComponent = new DyedItemColor(random.nextInt(0x1000000), true);
            armorPiece.set(DataComponents.DYED_COLOR, colorComponent);
        }
        return armorPiece;
    }
}
