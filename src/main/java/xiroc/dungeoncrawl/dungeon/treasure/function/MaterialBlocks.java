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
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.theme.ThemeItems;

public class MaterialBlocks extends LootFunction {

    public MaterialBlocks(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    public ItemStack doApply(ItemStack stack, LootContext context) {
        Biome biome = context.getWorld().getBiome(new BlockPos(context.get(LootParameters.field_237457_g_)));
        ResourceLocation biomeName = context.getWorld().func_241828_r().getRegistry(Registry.BIOME_KEY).getKey(biome);

        if (biomeName != null) {
            return new ItemStack(
                    ForgeRegistries.BLOCKS.getValue(ThemeItems.getMaterial(Theme.BIOME_TO_THEME_MAP.getOrDefault(biomeName.toString(), 0),
                            Theme.BIOME_TO_SUBTHEME_MAP.getOrDefault(biomeName.toString(), 0))),
                    16 + context.getRandom().nextInt(17));
        } else {
            DungeonCrawl.LOGGER.warn("MaterialBlocks: Couldn't find a registry name for biome {} - Proceeding with the default theme.", biome.toString());
            return new ItemStack(
                    ForgeRegistries.BLOCKS.getValue(ThemeItems.getMaterial(0, 0)),
                    16 + context.getRandom().nextInt(17));
        }

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
