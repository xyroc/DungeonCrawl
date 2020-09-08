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

package xiroc.dungeoncrawl.dungeon.treasure.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import xiroc.dungeoncrawl.DungeonCrawl;

public class EnchantedBook extends LootFunction {

    public int lootLevel;

    public EnchantedBook(ILootCondition[] conditionsIn, int stage) {
        super(conditionsIn);
        this.lootLevel = stage;
    }

    @Override
    public ItemStack doApply(ItemStack stack, LootContext context) {
        return EnchantmentHelper.addRandomEnchantment(context.getRandom(), new ItemStack(Items.BOOK),
                10 + lootLevel * 3, lootLevel > 2);
    }

    public static class Serializer extends LootFunction.Serializer<EnchantedBook> {

        public Serializer() {
            super(DungeonCrawl.locate("enchanted_book"), EnchantedBook.class);
        }

        @Override
        public void serialize(JsonObject object, EnchantedBook functionClazz,
                              JsonSerializationContext serializationContext) {
            object.add("loot_level", DungeonCrawl.GSON.toJsonTree(functionClazz.lootLevel));
        }

        @Override
        public EnchantedBook deserialize(JsonObject object, JsonDeserializationContext deserializationContext,
                                         ILootCondition[] conditionsIn) {
            return new EnchantedBook(conditionsIn, object.get("loot_level").getAsInt());
        }

    }

}
