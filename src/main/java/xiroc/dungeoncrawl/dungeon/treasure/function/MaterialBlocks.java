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
import net.minecraft.world.IWorld;
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
    public ItemStack run(ItemStack stack, LootContext context) {
        TileEntity chest = context.getParamOrNull(LootParameters.BLOCK_ENTITY);
        if (context.hasParam(LootParameters.ORIGIN) && chest != null && chest.hasLevel() & chest.getTileData().contains(DungeonCrawl.MOD_ID, 10)) {
            Tuple<Theme, Theme.SecondaryTheme> themes = Loot.getLootInformation(chest.getTileData());
            return new ItemStack(ForgeRegistries.BLOCKS.getValue(getMaterial(themes.getA(), themes.getB(), chest.getLevel(), new BlockPos(context.getParamOrNull(LootParameters.ORIGIN)), context.getRandom())),
                    16 + context.getRandom().nextInt(49));
        } else {
            return new ItemStack(Blocks.STONE_BRICKS, 16 + context.getRandom().nextInt(49));
        }
    }

    private static ResourceLocation getMaterial(Theme theme, Theme.SecondaryTheme secondaryTheme, IWorld world, BlockPos pos, Random rand) {
        return rand.nextBoolean()
                ? theme.material.get(world, pos).getBlock().getRegistryName()
                : secondaryTheme.material.get(world, pos).getBlock().getRegistryName();
    }

    @Override
    public LootFunctionType getType() {
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
