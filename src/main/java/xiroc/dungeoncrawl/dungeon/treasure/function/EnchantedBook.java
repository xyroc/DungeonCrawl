package xiroc.dungeoncrawl.dungeon.treasure.function;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import xiroc.dungeoncrawl.dungeon.monster.RandomEquipment;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;

public class EnchantedBook extends LootFunction {

    public int stage;

    public EnchantedBook(ILootCondition[] conditionsIn, int stage) {
        super(conditionsIn);
        this.stage = stage;
    }

    @Override
    public ItemStack doApply(ItemStack stack, LootContext context) {
        return EnchantmentHelper.addRandomEnchantment(context.getRandom(), new ItemStack(Items.BOOK),
                10 + stage * 3, stage > 2);
    }

    @Override
    public LootFunctionType func_230425_b_() {
        return Treasure.ENCHANTED_BOOK;
    }

    public static class Serializer extends LootFunction.Serializer<EnchantedBook> {

        public Serializer() {
            super();
        }

        @Override
        public void func_230424_a_(JsonObject p_230424_1_, EnchantedBook p_230424_2_, JsonSerializationContext p_230424_3_) {
            super.func_230424_a_(p_230424_1_, p_230424_2_, p_230424_3_);
            p_230424_1_.addProperty("stage", p_230424_2_.stage);
        }

        @Override
        public EnchantedBook deserialize(JsonObject object, JsonDeserializationContext deserializationContext,
                                         ILootCondition[] conditionsIn) {
            return new EnchantedBook(conditionsIn, JSONUtils.getInt(object, "stage", 1));
        }

    }

}
