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

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;

import java.util.Random;

public class RandomEquipment {
    public static ItemStack createItemStack(Item item, Random rand, int stage) {
        ItemStack itemStack = EnchantmentHelper.enchantItem(rand, new ItemStack(item), 10 + 3 * stage, false);
        if (itemStack.isDamageableItem()) {
            itemStack.setDamageValue(rand.nextInt(Math.max(1, item.getMaxDamage(itemStack) / 2)));
        }
        return itemStack;
    }

    public static ItemStack createArmorPiece(Item item, Random random, int stage) {
        ItemStack armorPiece = createItemStack(item, random, stage);
        if (item instanceof DyeableArmorItem) {
            CompoundTag nbt = armorPiece.getOrCreateTag();
            CompoundTag display = nbt.getCompound("display");
            display.putInt("color", random.nextInt(0x1000000));
            nbt.put("display", display);
        }
        return armorPiece;
    }

    public static Item getItem(ResourceLocation resourceLocation) {
        if (ForgeRegistries.ITEMS.containsKey(resourceLocation))
            return ForgeRegistries.ITEMS.getValue(resourceLocation);
        DungeonCrawl.LOGGER.warn("Failed to get {} from the item registry.", resourceLocation.toString());
        return null;
    }
}
