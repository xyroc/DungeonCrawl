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

package xiroc.dungeoncrawl.init;

import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.Dungeon;

public class ModStructures {

    public static final DeferredRegister<StructureFeature<?>> STRUCTURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, DungeonCrawl.MOD_ID);

    public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> DUNGEON = STRUCTURES.register("dungeon", Dungeon::new);

    public static void init() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        STRUCTURES.register(modEventBus);
    }

}
