package xiroc.dungeoncrawl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xiroc.dungeoncrawl.build.block.BlockRegistry;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelRegistry;
import xiroc.dungeoncrawl.util.EventManager;

@Mod(DungeonCrawl.MODID)
public class DungeonCrawl {

	public static final String MODID = "dungeoncrawl";
	public static final String NAME = "Dungeon Crawl";
	public static final String VERSION = "1.0.0";

	public static final Logger LOGGER = LogManager.getLogger(NAME);

	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public DungeonCrawl() {
		LOGGER.info("Here we go!");
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new EventManager());
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

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		if (event.getWorld().isRemote())
			return;
		DungeonSegmentModelRegistry.load(((ServerWorld) event.getWorld()).getServer().getResourceManager());
	}

	public static ResourceLocation locate(String path) {
		return new ResourceLocation(MODID, path);
	}

}
