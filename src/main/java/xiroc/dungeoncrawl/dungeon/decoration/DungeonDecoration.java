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

package xiroc.dungeoncrawl.dungeon.decoration;


import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.neoforge.registries.RegisterEvent;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.init.ModRegistries;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;

import java.util.function.Function;

public interface DungeonDecoration {
    static void register(RegisterEvent.RegisterHelper<MapCodec<? extends DungeonDecoration>> registry) {
        registry.register(DungeonCrawl.locate("scattered"), ScatteredDecoration.CODEC);
    }

    Codec<DungeonDecoration> CODEC = ModRegistries.DECORATION_TYPE
            .byNameCodec()
            .dispatch(DungeonDecoration::type, Function.identity());

    MapCodec<? extends DungeonDecoration> type();

    /**
     * Decorates the designated working area, which is assumed to be within valid bounds.
     *
     * @param level           The level the working area is located in.
     * @param workingArea     The area to decorate. Must be within valid bounds.
     * @param worldGenContext The dungeon world generation context for the location this decoration is called in.
     * @param random          A source of randomness.
     */
    void decorate(LevelAccessor level, BoundingBox workingArea, DungeonWorldGenContext worldGenContext, RandomSource random);
}