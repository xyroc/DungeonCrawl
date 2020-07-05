package xiroc.dungeoncrawl.module;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class Modules {

    public static final Logger LOGGER = LogManager.getLogger("Dungeon Crawl/Module Manager");

    private static final ArrayList<Class<? extends Module>> MODULES = Lists.newArrayList();
    private static final HashMap<Class<? extends Module>, String[]> MOD_REQUIREMENTS = new HashMap<Class<? extends Module>, String[]>();

    public static void load() {
        int size = MODULES.size();
        if (size <= 0)
            return;
        if (size == 1)
            LOGGER.info("There is one module present");
        else
            LOGGER.info("There are {} modules present", size);

        int successful = 0, failed = 0;
        moduleLoop:
        for (Class<? extends Module> moduleClass : MODULES) {
            for (String modId : MOD_REQUIREMENTS.get(moduleClass)) {
                if (!ModList.get().isLoaded(modId))
                    continue moduleLoop;
            }
            try {
                Module module = moduleClass.newInstance();
                LOGGER.info("Loading module {}", module.name);
                if (module.load())
                    successful++;
                else {
                    LOGGER.error("The module {} failed to load.", module.name);
                    failed++;
                }
            } catch (Exception e) {
                LOGGER.error("An error occurred while trying to load {}", moduleClass.toString());
            }

            LOGGER.info("Successfully loaded {} , {} failed.", successful + (size > 1 ? " Modules" : " Module"),
                    failed);
        }

        LOGGER.info("Successfully loaded {} , {} failed.", successful + (size > 1 ? " Modules" : " Module"), failed);

    }

    public static boolean registerModule(Class<? extends Module> module, String[] requiredMods) {
        MOD_REQUIREMENTS.put(module, requiredMods);
        return MODULES.add(module);
    }

    public static abstract class Module {

        public String name;
        public int version;

        public Module(ResourceLocation name, String... requiredMods) {
            this.name = name.toString();
            this.version = 0;
        }

        public abstract boolean load();

    }

}
