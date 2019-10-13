package xiroc.dungeoncrawl.dungeon.treasure.function;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.monster.RandomEquipment;

public class EnchantedBook extends LootFunction {

	public int stage;

	public EnchantedBook(ILootCondition[] conditionsIn, int stage) {
		super(conditionsIn);
		this.stage = stage;
	}

	@Override
	public ItemStack doApply(ItemStack stack, LootContext context) {
		ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
		Enchantment enchantment = xiroc.dungeoncrawl.dungeon.treasure.EnchantedBook
				.getRandomEnchantment(context.getRandom(), stage);
		RandomEquipment.enchantItem(book, context.getRandom(), enchantment, RandomEquipment.getStageMultiplier(stage));
		return book;
	}

	public static class Serializer extends LootFunction.Serializer<EnchantedBook> {

		public Serializer() {
			super(DungeonCrawl.locate("enchanted_book"), EnchantedBook.class);
		}

		@Override
		public void serialize(JsonObject object, EnchantedBook functionClazz,
				JsonSerializationContext serializationContext) {
			object.add("stage", DungeonCrawl.GSON.toJsonTree(functionClazz.stage));
		}

		@Override
		public EnchantedBook deserialize(JsonObject object, JsonDeserializationContext deserializationContext,
				ILootCondition[] conditionsIn) {
			return new EnchantedBook(conditionsIn, DungeonCrawl.GSON.fromJson(object.get("stage"), Integer.class));
		}

	}

}
