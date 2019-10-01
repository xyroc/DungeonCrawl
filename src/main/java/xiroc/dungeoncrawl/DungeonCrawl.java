package xiroc.dungeoncrawl;

/*
GENRERAL LICENSE FOR DungeonCrawl v1.0

(1) DungeonCrawl is the intellectual property of XYROC (otherwise known as XIROC1337). Distribution of the compiled mod on any other site than curseforge.com, minecraft.curseforge.com, xiroc.ovh or minecraftforum.net is strictly forbidden. Further, all sites included in the following list are NOT allowed to redistribute the mod or profit from it in any way:
https://raw.githubusercontent.com/StopModReposts/Illegal-Mod-Sites/master/SITES.md
Redistributing this mod on the above mentioned illegal sites is a violation of copyright.

(2) Modpack creators are only allowed to include this mod in FREE-TO-PLAY modpacks. Including this mod in modpacks that require payment to become playable or accessible for the user is forbidden.

(3) You are allowed to read, use and share the Source Code of this mod, for example to create similar projects. However, completely copying the mod or copying large parts of the source (= more than 20%) without the explicit approval of the mod author is forbidden.

DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved
 */

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.Dungeon;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelBlock;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelRegistry;
import xiroc.dungeoncrawl.part.block.BlockRegistry;
import xiroc.dungeoncrawl.util.EventManager;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

@Mod(DungeonCrawl.MODID)
public class DungeonCrawl {

	public static final String MODID = "dungeoncrawl";
	public static final String NAME = "Dungeon Crawl";
	public static final String VERSION = "1.4.0";

	public static final Logger LOGGER = LogManager.getLogger(NAME);

	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public DungeonCrawl() {
		LOGGER.info("Here we go!");
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new EventManager());
//		MinecraftForge.EVENT_BUS.register(new DungeonSegmentTestHelper());
		Dungeon.DUNGEON.setRegistryName(locate(Dungeon.NAME.toLowerCase(Locale.ROOT)));
		ForgeRegistries.FEATURES.register(Dungeon.DUNGEON);
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		LOGGER.info("Common Setup");
		ModLoadingContext.get().registerConfig(Type.COMMON, Config.CONFIG);
		Config.load(FMLPaths.CONFIGDIR.get().resolve("dungeon_crawl.toml"));

		DungeonSegmentModelBlock.load();
		IBlockPlacementHandler.load();
		BlockRegistry.load();
	}

	private void clientSetup(final FMLClientSetupEvent event) {
		LOGGER.info("Client Setup");
	}

	public static String getDate() {
		return new SimpleDateFormat().format(new Date());
	}

	/*
	 * Doesnt work
	 */
	@SubscribeEvent
	public void onRegisterFeature(RegistryEvent.Register<Feature<?>> event) {
		DungeonCrawl.LOGGER.info("Feature Registry Event: {}", event);
//		event.getRegistry().register(Dungeon.DUNGEON);
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
