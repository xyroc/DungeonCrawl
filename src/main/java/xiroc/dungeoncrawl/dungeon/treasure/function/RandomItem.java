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
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.treasure.RandomSpecialItem;
import xiroc.dungeoncrawl.theme.Theme;

public class RandomItem extends LootFunction {

	public int stage;

	public RandomItem(ILootCondition[] conditionsIn, int stage) {
		super(conditionsIn);
	}

	@Override
	public ItemStack doApply(ItemStack stack, LootContext context) {
		return RandomSpecialItem.generate(context.getWorld(), context.getRandom(),
				Theme.BIOME_TO_THEME_MAP.getOrDefault(
						context.getWorld().func_226691_t_(context.get(LootParameters.POSITION)).getRegistryName().toString(),
						0),
				stage - 1);
	}

	public static class Serializer extends LootFunction.Serializer<RandomItem> {

		public Serializer() {
			super(DungeonCrawl.locate("random_item"), RandomItem.class);
		}

		@Override
		public void serialize(JsonObject object, RandomItem functionClazz,
				JsonSerializationContext serializationContext) {
			object.add("stage", DungeonCrawl.GSON.toJsonTree(functionClazz.stage));
		}

		@Override
		public RandomItem deserialize(JsonObject object, JsonDeserializationContext deserializationContext,
				ILootCondition[] conditionsIn) {
			return new RandomItem(conditionsIn, DungeonCrawl.GSON.fromJson(object.get("stage"), Integer.class));
		}

	}

}
