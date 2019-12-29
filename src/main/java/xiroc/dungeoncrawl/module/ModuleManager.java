package xiroc.dungeoncrawl.module;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;

public class ModuleManager {

	public static final Logger LOGGER = LogManager.getLogger("Dungeon Crawl/Module Manager");

	private static final ArrayList<Module> MODULES = Lists.newArrayList();

	public static void load() {
		int size = MODULES.size();
		if (size <= 0)
			return;
		if (size == 1)
			LOGGER.info("There is one module present");
		else
			LOGGER.info("There are {} modules present", size);
		
		int successful = 0, failed = 0;
		
		moduleLoop: for (Module module : MODULES) {
			for (String modId : module.requiredMods) {
				if (!ModList.get().isLoaded(modId))
					continue moduleLoop;
			}
			LOGGER.info("Loading module {}", module.name);
			if (module.load())
				successful++;
			else {
				LOGGER.error("The module {} failed to load.", module.name);
				failed++;
			}
		}
		
		LOGGER.info("Successfully loaded {} , {} failed.", successful + (size > 1 ? " Modules" : " Module"),
				failed);

	}

	public static boolean registerModule(Module module) {
		return MODULES.add(module);
	}

	public static abstract class Module {

		public String name;
		public String[] requiredMods;
		public int version;

		public Module(ResourceLocation name, String... requiredMods) {
			this.name = name.toString();
			this.requiredMods = requiredMods;
			this.version = 0;
		}

		public abstract boolean load();

	}

}
