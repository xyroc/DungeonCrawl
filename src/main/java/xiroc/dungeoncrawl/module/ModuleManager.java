package xiroc.dungeoncrawl.module;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.ModList;

public class ModuleManager {

	public static final Logger LOGGER = LogManager.getLogger();

	private static final ArrayList<Tuple<Module, String[]>> MODULES = Lists.newArrayList();

	public static void load() {
		int size = MODULES.size();
		if (size == 0)
			return;
		LOGGER.info("Loading {} ", size + (size > 1 ? "Modules" : "Module"));
		int successful = 0, failed = 0;
		for (Tuple<Module, String[]> data : MODULES) {
			for (String modId : data.getB()) {
				if (!ModList.get().isLoaded(modId))
					continue;
			}
			Module module = data.getA();
			LOGGER.info("Loading module {}", module.name);
			if (module.load())
				successful++;
			else {
				LOGGER.error("The module {} failed to load.", module.name);
				failed++;
			}
			LOGGER.info("Successfully loaded {} , {} failed.", successful + (size > 1 ? "Modules" : "Module"), failed);
		}

	}

	public static void registerModule(Module module, String[] requiredMods) {
		MODULES.add(new Tuple<ModuleManager.Module, String[]>(module, requiredMods));
	}

	public static abstract class Module {

		String name;

		public Module(ResourceLocation name) {
			this.name = name.toString();
		}

		public abstract boolean load();

	}

}
