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
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.misc.Banner;

public class Shield extends LootFunction {

    public Shield(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    public ItemStack doApply(ItemStack stack, LootContext context) {
        return Banner.createShield(context.getRandom());
    }

    public static class Serializer extends LootFunction.Serializer<Shield> {

        public Serializer() {
            super(DungeonCrawl.locate("shield"), Shield.class);
        }

        @Override
        public Shield deserialize(JsonObject object, JsonDeserializationContext deserializationContext,
                                  ILootCondition[] conditionsIn) {
            return new Shield(conditionsIn);
        }

    }

}
