package xiroc.dungeoncrawl.dungeon.treasure.function;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import xiroc.dungeoncrawl.dungeon.treasure.RandomSpecialItem;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;

public class RandomItem extends LootFunction {

    public int stage;

    public RandomItem(ILootCondition[] conditionsIn, int stage) {
        super(conditionsIn);
        this.stage = stage;
    }

    @Override
    public ItemStack doApply(ItemStack stack, LootContext context) {
        return RandomSpecialItem.generate(context.getWorld(), context.getRandom(),
                Theme.BIOME_TO_THEME_MAP.getOrDefault(
                        context.getWorld().getBiome(context.get(LootParameters.POSITION)).getRegistryName().toString(),
                        0),
                stage - 1);
    }

    @Override
    public LootFunctionType func_230425_b_() {
        return Treasure.RANDOM_ITEM;
    }

    public static class Serializer extends LootFunction.Serializer<RandomItem> {

        public Serializer() {
            super();
        }

        @Override
        public void func_230424_a_(JsonObject p_230424_1_, RandomItem p_230424_2_, JsonSerializationContext p_230424_3_) {
            super.func_230424_a_(p_230424_1_, p_230424_2_, p_230424_3_);
            p_230424_1_.addProperty("stage", p_230424_2_.stage);
        }

        @Override
        public RandomItem deserialize(JsonObject object, JsonDeserializationContext deserializationContext,
                                      ILootCondition[] conditionsIn) {
            return new RandomItem(conditionsIn, JSONUtils.getInt(object, "stage", 1));
        }

    }

}
