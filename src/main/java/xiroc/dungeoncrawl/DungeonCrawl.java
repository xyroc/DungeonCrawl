package xiroc.dungeoncrawl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xiroc.dungeoncrawl.build.block.BlockRegistry;

@Mod(DungeonCrawl.MODID)
public class DungeonCrawl {

	public static final String MODID = "dungeoncrawl";
	public static final String NAME = "Dungeon Crawl";
	public static final String VERSION = "1.0.0";

	public static final Logger LOGGER = LogManager.getLogger(NAME);

	public DungeonCrawl() {
		LOGGER.info("Here we go!");
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
		BlockRegistry.load();
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		LOGGER.info("Common Setup");
	}

	private void clientSetup(final FMLClientSetupEvent event) {
		LOGGER.info("Client Setup");
	}

	public static String getDate() {
		return new SimpleDateFormat().format(new Date());
	}

}
