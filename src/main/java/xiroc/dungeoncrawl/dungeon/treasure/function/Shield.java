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
import net.minecraft.loot.conditions.ILootCondition;
import xiroc.dungeoncrawl.dungeon.misc.Banner;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;

public class Shield extends LootFunction {

    public Shield(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    public ItemStack doApply(ItemStack stack, LootContext context) {
        return Banner.createShield(context.getRandom());
    }

    @Override
    public LootFunctionType func_230425_b_() {
        return Treasure.SHIELD;
    }

    public static class Serializer extends LootFunction.Serializer<Shield> {

        public Serializer() {
            super();
        }

        @Override
        public Shield deserialize(JsonObject object, JsonDeserializationContext deserializationContext,
                                  ILootCondition[] conditionsIn) {
            return new Shield(conditionsIn);
        }

    }

}
