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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;
import xiroc.dungeoncrawl.dungeon.treasure.RandomItems;


public class RandomItem extends LootItemConditionalFunction {

    public int lootLevel;

    public RandomItem(LootItemCondition[] conditionsIn, int lootLevel) {
        super(conditionsIn);
        this.lootLevel = Math.max(0, lootLevel);
    }

    @Override
    public ItemStack run(ItemStack stack, LootContext context) {
        if (context.hasParam(LootContextParams.ORIGIN)) {
            return RandomItems.generate(context.getRandom(), lootLevel);
        } else {
            return ItemStack.EMPTY;
        }
    }

    public static LootItemConditionalFunction.Builder<?> randomItem(int lootLevel) {
        return simpleBuilder(conditions -> new RandomItem(conditions, lootLevel));
    }

    @Override
    public LootItemFunctionType getType() {
        return Loot.RANDOM_ITEM;
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<RandomItem> {

        public Serializer() {
            super();
        }

        @Override
        public void serialize(JsonObject p_230424_1_, RandomItem p_230424_2_, JsonSerializationContext p_230424_3_) {
            super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
            p_230424_1_.addProperty(Loot.LOOT_LEVEL, p_230424_2_.lootLevel);
        }

        @Override
        public RandomItem deserialize(JsonObject object, JsonDeserializationContext deserializationContext,
                                      LootItemCondition[] conditionsIn) {
            return new RandomItem(conditionsIn, object.get(Loot.LOOT_LEVEL).getAsInt());
        }

    }

}
