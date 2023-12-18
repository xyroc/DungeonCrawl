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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;
import xiroc.dungeoncrawl.dungeon.treasure.RandomItems;

import java.util.List;

public class Shield extends LootItemConditionalFunction {
    public static final Codec<Shield> CODEC = RecordCodecBuilder.create((builder) -> commonFields(builder)
            .and(Codec.INT.fieldOf(Loot.KEY_LOOT_LEVEL).forGetter(shield -> shield.lootLevel))
            .apply(builder, Shield::new));

    public final int lootLevel;

    public Shield(List<LootItemCondition> conditions, int lootLevel) {
        super(conditions);
        this.lootLevel = Math.max(0, lootLevel);
    }

    @Override
    public ItemStack run(ItemStack stack, LootContext context) {
        return RandomItems.createShield(context.getRandom(), lootLevel);
    }

    public static LootItemConditionalFunction.Builder<?> shield(int lootLevel) {
        return simpleBuilder(conditions -> new Shield(conditions, lootLevel));
    }

    @Override
    public LootItemFunctionType getType() {
        return Loot.SHIELD;
    }
}
