package xiroc.dungeoncrawl.dungeon.treasure.function;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.theme.ThemeItems;

public class MaterialBlocks extends LootFunction {

	public MaterialBlocks(ILootCondition[] conditionsIn) {
		super(conditionsIn);
	}

	@Override
	public ItemStack doApply(ItemStack stack, LootContext context) {
		String biome = context.getWorld().getBiome(context.get(LootParameters.POSITION)).getRegistryName().toString();
		return new ItemStack(
				ForgeRegistries.BLOCKS.getValue(ThemeItems.getMaterial(Theme.BIOME_TO_THEME_MAP.getOrDefault(biome, 0),
						Theme.BIOME_TO_SUBTHEME_MAP.getOrDefault(biome, 0))),
				context.getRandom().nextInt(7));
	}

	public static class Serializer extends LootFunction.Serializer<MaterialBlocks> {

		public Serializer() {
			super(DungeonCrawl.locate("material_blocks"), MaterialBlocks.class);
		}

		@Override
		public MaterialBlocks deserialize(JsonObject object, JsonDeserializationContext deserializationContext,
				ILootCondition[] conditionsIn) {
			return new MaterialBlocks(conditionsIn);
		}

	}

}
