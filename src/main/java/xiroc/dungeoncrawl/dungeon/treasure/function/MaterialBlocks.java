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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.Range;

import java.util.Random;

public class MaterialBlocks extends LootItemConditionalFunction {

    private static final Range AMOUNT = new Range(16, 64);

    public MaterialBlocks(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    public ItemStack run(ItemStack stack, LootContext context) {
        if (context.hasParam(LootContextParams.ORIGIN)) {
            BlockPos pos = new BlockPos(context.getParamOrNull(LootContextParams.ORIGIN));
            BlockEntity chest = context.getLevel().getBlockEntity(pos);
            if (chest != null && chest.getTileData().contains(DungeonCrawl.MOD_ID, 10)) {
                Tuple<Theme, Theme.SecondaryTheme> themes = Loot.getLootInformation(chest.getTileData());
                return new ItemStack(ForgeRegistries.BLOCKS.getValue(getMaterial(themes.getA(), themes.getB(), context.getLevel(), pos, context.getRandom())),
                        AMOUNT.nextInt(context.getRandom()));
            }
        }
        return new ItemStack(Blocks.STONE_BRICKS, AMOUNT.nextInt(context.getRandom()));
    }

    private static ResourceLocation getMaterial(Theme theme, Theme.SecondaryTheme secondaryTheme, LevelAccessor world, BlockPos pos, Random rand) {
        return rand.nextBoolean()
                ? theme.material.get(world, pos).getBlock().getRegistryName()
                : secondaryTheme.material.get(world, pos).getBlock().getRegistryName();
    }

    public static LootItemConditionalFunction.Builder<?> materialBlocks() {
        return simpleBuilder(MaterialBlocks::new);
    }

    @Override
    public LootItemFunctionType getType() {
        return Loot.MATERIAL_BLOCKS;
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<MaterialBlocks> {

        public Serializer() {
            super();
        }

        @Override
        public MaterialBlocks deserialize(JsonObject object, JsonDeserializationContext deserializationContext,
                                          LootItemCondition[] conditionsIn) {
            return new MaterialBlocks(conditionsIn);
        }

    }

}
