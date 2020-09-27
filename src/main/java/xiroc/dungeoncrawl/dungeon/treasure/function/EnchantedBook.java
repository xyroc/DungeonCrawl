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
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.conditions.ILootCondition;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;

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

    @Override
    public LootFunctionType getFunctionType() {
        return Treasure.ENCHANTED_BOOK;
    }

    public static class Serializer extends LootFunction.Serializer<EnchantedBook> {

        public Serializer() {
            super();
        }

        @Override
        public void serialize(JsonObject p_230424_1_, EnchantedBook p_230424_2_, JsonSerializationContext p_230424_3_) {
            super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
            p_230424_1_.addProperty("stage", p_230424_2_.lootLevel);
        }


        @Override
        public EnchantedBook deserialize(JsonObject object, JsonDeserializationContext deserializationContext,
                                         ILootCondition[] conditionsIn) {
            return new EnchantedBook(conditionsIn, object.get("loot_level").getAsInt());
        }

    }

}
