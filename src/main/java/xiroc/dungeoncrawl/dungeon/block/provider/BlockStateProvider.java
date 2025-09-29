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

package xiroc.dungeoncrawl.dungeon.block.provider;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.RegisterEvent;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.block.provider.pattern.CheckerboardPattern;
import xiroc.dungeoncrawl.init.ModRegistries;
import xiroc.dungeoncrawl.util.StorageHelper;

import java.util.function.Function;

public interface BlockStateProvider {
    static void register(RegisterEvent.RegisterHelper<MapCodec<? extends BlockStateProvider>> registry) {
        registry.register(DungeonCrawl.locate("block"), SingleBlock.VERBOSE_CODEC);
        registry.register(DungeonCrawl.locate("random_block"), RandomBlock.VERBOSE_CODEC);
        registry.register(DungeonCrawl.locate("checkerboard"), CheckerboardPattern.CODEC);
    }

    Codec<BlockStateProvider> VERBOSE_CODEC = Codec.lazyInitialized(() -> ModRegistries.BLOCK_STATE_PROVIDER_TYPE
            .byNameCodec()
            .dispatch(BlockStateProvider::type, Function.identity()));

    Codec<BlockStateProvider> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<BlockStateProvider, T>> decode(DynamicOps<T> dynamicOps, T t) {
            if (dynamicOps.getStringValue(t).isSuccess()) {
                return SingleBlock.COMPACT_CODEC.decode(dynamicOps, t).map(StorageHelper::repack);
            }
            if (dynamicOps.getList(t).isSuccess()) {
                return RandomBlock.COMPACT_CODEC.decode(dynamicOps, t).map(StorageHelper::repack);
            }
            return VERBOSE_CODEC.decode(dynamicOps, t);
        }

        @Override
        public <T> DataResult<T> encode(BlockStateProvider blockStateProvider, DynamicOps<T> dynamicOps, T t) {
            if (blockStateProvider instanceof SingleBlock singleBlock) {
                return SingleBlock.COMPACT_CODEC.encode(singleBlock, dynamicOps, t);
            }
            if (blockStateProvider instanceof RandomBlock randomBlock) {
                return RandomBlock.COMPACT_CODEC.encode(randomBlock, dynamicOps, t);
            }
            return VERBOSE_CODEC.encode(blockStateProvider, dynamicOps, t);
        }
    };

    BlockState get(BlockPos pos, RandomSource random);

    MapCodec<? extends BlockStateProvider> type();
}
