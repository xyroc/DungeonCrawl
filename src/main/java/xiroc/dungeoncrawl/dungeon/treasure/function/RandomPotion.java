package xiroc.dungeoncrawl.dungeon.treasure.function;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.treasure.TreasureItems;

public class RandomPotion extends LootFunction {
	
	public int stage;

	public RandomPotion(ILootCondition[] conditionsIn, int stage) {
		super(conditionsIn);
		this.stage = stage;
	}

	@Override
	public ItemStack doApply(ItemStack stack, LootContext context) {
		return TreasureItems.getRandomSpecialPotion(context.getRandom(), stage - 1);
	}
	
	public static class Serializer extends LootFunction.Serializer<RandomPotion>{

		public Serializer() {
			super(DungeonCrawl.locate("random_potion"), RandomPotion.class);
		}
		
		@Override
		public void serialize(JsonObject object, RandomPotion functionClazz,
				JsonSerializationContext serializationContext) {
			object.add("stage", DungeonCrawl.GSON.toJsonTree(functionClazz.stage));
		}

		@Override
		public RandomPotion deserialize(JsonObject object, JsonDeserializationContext deserializationContext,
				ILootCondition[] conditionsIn) {
			return new RandomPotion(conditionsIn, DungeonCrawl.GSON.fromJson(object.get("stage"), Integer.class));
		}
		
	}

}
