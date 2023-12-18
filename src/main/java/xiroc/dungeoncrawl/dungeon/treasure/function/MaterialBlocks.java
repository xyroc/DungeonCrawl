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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;
import xiroc.dungeoncrawl.theme.SecondaryTheme;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.Range;

import java.util.List;

public class MaterialBlocks extends LootItemConditionalFunction {
    public static final Codec<MaterialBlocks> CODEC = RecordCodecBuilder.create((builder) -> commonFields(builder).apply(builder, MaterialBlocks::new));

    private static final Range AMOUNT = new Range(16, 64);

    public MaterialBlocks(List<LootItemCondition> conditions) {
        super(conditions);
    }

    @Override
    public ItemStack run(ItemStack stack, LootContext context) {
        if (context.hasParam(LootContextParams.ORIGIN)) {
            Vec3 origin = context.getParam(LootContextParams.ORIGIN);
            BlockPos chestPosition = new BlockPos((int) (origin.x - 0.5), (int) (origin.y - 0.5), (int) (origin.z - 0.5));
            BlockEntity chest = context.getLevel().getBlockEntity(chestPosition);
            if (chest != null && chest.getPersistentData().contains(DungeonCrawl.MOD_ID, 10)) {
                Tuple<Theme, SecondaryTheme> themes = Loot.getLootInformation(chest.getPersistentData());
                return new ItemStack(getMaterial(themes.getA(), themes.getB(), context.getLevel(), chestPosition, context.getRandom()), AMOUNT.nextInt(context.getRandom()));
            }
        }
        return new ItemStack(Blocks.STONE_BRICKS, AMOUNT.nextInt(context.getRandom()));
    }

    private static Block getMaterial(Theme theme, SecondaryTheme secondaryTheme, LevelAccessor world, BlockPos pos, RandomSource rand) {
        if (rand.nextBoolean()) {
            return theme.material.get(world, pos, rand).getBlock();
        } else {
            return secondaryTheme.material.get(world, pos, rand).getBlock();
        }
    }

    public static LootItemConditionalFunction.Builder<?> materialBlocks() {
        return simpleBuilder(MaterialBlocks::new);
    }

    @Override
    public LootItemFunctionType getType() {
        return Loot.MATERIAL_BLOCKS;
    }
}
