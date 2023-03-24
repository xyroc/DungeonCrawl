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

package xiroc.dungeoncrawl.data.themes;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.data.JsonDataProvider;
import xiroc.dungeoncrawl.datapack.DatapackDirectories;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;

import java.util.function.BiConsumer;

public class SecondaryThemes extends JsonDataProvider<SecondaryTheme> {
    public SecondaryThemes(DataGenerator generator) {
        super(generator, "Secondary Themes", DatapackDirectories.SECONDARY_THEMES.path(), SecondaryTheme::serialize);
    }

    @Override
    public void collect(BiConsumer<ResourceLocation, SecondaryTheme> collector) {
        // TODO: add secondary themes
    }

    private static void create(BiConsumer<ResourceLocation, SecondaryTheme> collector, SecondaryTheme theme) {
        collector.accept(theme.key(), theme);
    }
}