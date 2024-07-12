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

package xiroc.dungeoncrawl.dungeon.misc;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

import java.util.ArrayList;
import java.util.List;

public class Banner {
    private static final int PATTERN_COUNT = 3;

    /**
     * Creates the BlockEntityTag for banners or shields which contains a list of
     * random patterns.
     */
    public static BannerPatternLayers createPatterns(RandomSource rand, RegistryAccess registryAccess) {
        Registry<BannerPattern> bannerPatterns = registryAccess.registryOrThrow(Registries.BANNER_PATTERN);
        List<BannerPatternLayers.Layer> layers = new ArrayList<>();
        for (int i = 0; i < PATTERN_COUNT; ++i) {
            Holder<BannerPattern> pattern = bannerPatterns.getRandom(rand).orElseThrow();
            layers.add(new BannerPatternLayers.Layer(pattern, DyeColor.byId(rand.nextInt(16))));
        }
        return new BannerPatternLayers(layers);
    }
}
