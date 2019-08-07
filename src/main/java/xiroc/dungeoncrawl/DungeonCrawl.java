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
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xiroc.dungeoncrawl.dungeon.Dungeon;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelRegistry;
import xiroc.dungeoncrawl.part.block.BlockRegistry;
import xiroc.dungeoncrawl.util.EventManager;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

@Mod(DungeonCrawl.MODID)
public class DungeonCrawl {

	public static final String MODID = "dungeoncrawl";
	public static final String NAME = "Dungeon Crawl";
	public static final String VERSION = "1.1.0";

	public static final Logger LOGGER = LogManager.getLogger(NAME);

	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	/**
	 * Contains all biomes that should not generate dungeons at all. DO NOT PUT
	 * BIOME_OVERWORLD_BLACKLIST ENTRIES IN HERE!
	 */
	public static List<String> BIOME_BLACKLIST = Lists.newArrayList("minecraft:the_end", "minecraft:nether", "minecraft:small_end_islands", "minecraft:end_midlands", "minecraft:end_highlands", "minecraft:end_barrens", "minecraft:the_void");

	/**
	 * Contains biomes in the overworld where dungeons should not be generated.
	 * These biomes might contain small dungeon parts from neighbour biomes tho.
	 * Removed: river, frozen_river and beach
	 */
	public static List<String> BIOME_OVERWORLD_BLACKLIST = Lists.newArrayList("minecraft:ocean", "minecraft:deep_ocean", "minecraft:warm_ocean", "minecraft:lukewarm_ocean", "minecraft:cold_ocean", "minecraft:deep_warm_ocean",
			"minecraft:deep_lukewarm_ocean", "minecraft:deep_cold_ocean", "minecraft:deep_frozen_ocean");

	public DungeonCrawl() {
		LOGGER.info("Here we go!");
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new EventManager());
		// MinecraftForge.EVENT_BUS.register(new DungeonSegmentTestHelper());
		Feature.STRUCTURES.put(Dungeon.NAME.toLowerCase(Locale.ROOT), Dungeon.DUNGEON_FEATURE);
		IBlockPlacementHandler.load();
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
