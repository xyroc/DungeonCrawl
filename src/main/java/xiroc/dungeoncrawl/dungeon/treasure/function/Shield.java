package xiroc.dungeoncrawl.dungeon.treasure.function;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

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
