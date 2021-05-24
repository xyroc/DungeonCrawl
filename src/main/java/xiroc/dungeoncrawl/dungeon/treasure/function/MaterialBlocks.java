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
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.Random;

public class MaterialBlocks extends LootFunction {

    public MaterialBlocks(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    public ItemStack doApply(ItemStack stack, LootContext context) {
        TileEntity chest = context.get(LootParameters.BLOCK_ENTITY);
        if (context.has(LootParameters.ORIGIN)) {
            if (chest != null && chest.getTileData().contains(DungeonCrawl.MOD_ID, 10)) {
                Tuple<Theme, Theme.SecondaryTheme> themes = Loot.getLootInformation(chest.getTileData());
                return new ItemStack(ForgeRegistries.BLOCKS.getValue(getMaterial(themes.getA(), themes.getB(), new BlockPos(context.get(LootParameters.ORIGIN)), context.getRandom())),
                        16 + context.getRandom().nextInt(49));
            } else {
                Random random = context.getRandom();
                Theme theme;
                Theme.SecondaryTheme secondaryTheme;
                ResourceLocation biome = context.getWorld().getBiome(new BlockPos(context.get(LootParameters.ORIGIN))).getRegistryName();
                if (biome != null) {
                    theme = Theme.randomTheme(biome.toString(), context.getRandom());
                    secondaryTheme = Theme.randomSecondaryTheme(biome.toString(), context.getRandom());
                } else {
                    theme = Theme.getDefaultTheme();
                    secondaryTheme = Theme.getDefaultSubTheme();
                }
                return new ItemStack(ForgeRegistries.BLOCKS.getValue(getMaterial(theme, secondaryTheme, new BlockPos(context.get(LootParameters.ORIGIN)), random)), 16 + context.getRandom().nextInt(49));
            }
        } else {
            return new ItemStack(Blocks.STONE_BRICKS, 16 + context.getRandom().nextInt(49));
        }
    }

    private static ResourceLocation getMaterial(Theme theme, Theme.SecondaryTheme secondaryTheme, BlockPos pos, Random rand) {
        return rand.nextBoolean() ? theme.material.get(pos).getBlock().getRegistryName()
                : secondaryTheme.material.get(pos).getBlock().getRegistryName();
    }

    @Override
    public LootFunctionType getFunctionType() {
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
