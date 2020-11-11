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
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.treasure.RandomItems;
import xiroc.dungeoncrawl.theme.Theme;

public class RandomItem extends LootFunction {

    public int lootLevel;

    public RandomItem(ILootCondition[] conditionsIn, int lootLevel) {
        super(conditionsIn);
        this.lootLevel = Math.max(0, lootLevel);
    }

    @Override
    public ItemStack doApply(ItemStack stack, LootContext context) {
        return RandomItems.generate(context.getWorld(), context.getRandom(),
                Theme.BIOME_TO_THEME_MAP.getOrDefault(
                        context.getWorld().getBiome(context.get(LootParameters.POSITION)).getRegistryName().toString(),
                        0), lootLevel);
    }

    public static class Serializer extends LootFunction.Serializer<RandomItem> {

        public Serializer() {
            super(DungeonCrawl.locate("random_item"), RandomItem.class);
        }

        @Override
        public void serialize(JsonObject object, RandomItem functionClazz,
                              JsonSerializationContext serializationContext) {
            object.add("loot_level", DungeonCrawl.GSON.toJsonTree(functionClazz.lootLevel));
        }

        @Override
        public RandomItem deserialize(JsonObject object, JsonDeserializationContext deserializationContext,
                                      ILootCondition[] conditionsIn) {
            return new RandomItem(conditionsIn, object.get("loot_level").getAsInt() - 1);
        }

    }

}
