package xiroc.dungeoncrawl.dungeon.treasure.function;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
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
                16 + context.getRandom().nextInt(17));
    }

    @Override
    public LootFunctionType func_230425_b_() {
        return Treasure.MATERIAL_BLOCKS;
    }

    public static class Serializer extends LootFunction.Serializer<MaterialBlocks> {

        public Serializer() {
            super();
        }

        @Override
        public MaterialBlocks deserialize(JsonObject object, JsonDeserializationContext deserializationContext,
                                          ILootCondition[] conditionsIn) {
            return new MaterialBlocks(conditionsIn);
        }

    }

}
