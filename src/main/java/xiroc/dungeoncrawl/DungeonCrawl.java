package xiroc.dungeoncrawl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.config.JsonConfig;
import xiroc.dungeoncrawl.dungeon.DataReloadListener;
import xiroc.dungeoncrawl.dungeon.Dungeon;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelBlock;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.module.BOPCompatModule;
import xiroc.dungeoncrawl.module.Modules;
import xiroc.dungeoncrawl.theme.WeightedThemeRandomizer;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;
import xiroc.dungeoncrawl.util.Tools;
import xiroc.dungeoncrawl.util.WeightedIntegerEntry;

import java.text.SimpleDateFormat;
import java.util.Date;

/* GENRERAL LICENSE FOR DungeonCrawl v1.0
 * 
(1) DungeonCrawl is the intellectual property of XYROC (otherwise known as XIROC1337).
    Distribution of the compiled mod on any other site than curseforge.com, minecraft.curseforge.com, xiroc.ovh or minecraftforum.net is strictly forbidden.
    Further, all sites included in the following list are NOT allowed to redistribute the mod or profit from it in any way:
    https://stopmodreposts.org/sites.html
    Redistributing this mod on the above mentioned illegal sites is a violation of copyright.
    
(2) Modpack creators are only allowed to include this mod in FREE-TO-PLAY modpacks. 
    Including this mod in modpacks that require payment to become playable or accessible for the user is forbidden.
    
(3) You are allowed to read, use and share the Source Code of this mod, for example to create similar projects.
    However, completely copying the mod or copying large parts of the source (= more than 20%) without the explicit approval of the mod author is forbidden.
    
DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

@Mod(DungeonCrawl.MODID)
public class DungeonCrawl {

    public static final String MODID = "dungeoncrawl";
    public static final String NAME = "Dungeon Crawl";
    public static final String VERSION = "2.0.0";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(DungeonModel.Metadata.class, new DungeonModel.Metadata.Deserializer())
            .registerTypeAdapter(WeightedThemeRandomizer.class, new WeightedThemeRandomizer.Deserializer())
            .registerTypeAdapter(WeightedIntegerEntry.class, new WeightedIntegerEntry.Deserializer())
            .setPrettyPrinting().create();

    public static IEventBus EVENT_BUS;

    public DungeonCrawl() {
        LOGGER.info("Here we go! Launching Dungeon Crawl {}...", VERSION);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

        ForgeRegistries.FEATURES
                .register(Dungeon.DUNGEON.setRegistryName(new ResourceLocation(Dungeon.NAME.toLowerCase())));
        StructurePieceTypes.registerAll();
        Treasure.init();

        EVENT_BUS = Bus.MOD.bus().get();

        Modules.registerModule(BOPCompatModule.class, new String[]{"biomesoplenty"});
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Common Setup");
        ModLoadingContext.get().registerConfig(Type.COMMON, Config.CONFIG);
        Config.load(FMLPaths.CONFIGDIR.get().resolve("dungeon_crawl.toml"));

        if (Config.ENABLE_TOOLS.get()) {
            MinecraftForge.EVENT_BUS.register(new Tools());
        }

        DungeonModelBlock.createProviders();
        IBlockPlacementHandler.load();
        DungeonBlocks.load();

        DungeonCrawl.LOGGER.info("Adding features and structures");

        for (Biome biome : ForgeRegistries.BIOMES) {
            if (Dungeon.OVERWORLD_CATEGORIES.contains(biome.getCategory())) {
                biome.addFeature(Decoration.UNDERGROUND_STRUCTURES, Biome.createDecoratedFeature(Dungeon.DUNGEON,
                        NoFeatureConfig.NO_FEATURE_CONFIG, Placement.NOPE, NoPlacementConfig.NO_PLACEMENT_CONFIG));
                if (!JsonConfig.BIOME_OVERWORLD_BLACKLIST.contains(biome.getRegistryName().toString())
                        && Dungeon.ALLOWED_CATEGORIES.contains(biome.getCategory())) {
                    DungeonCrawl.LOGGER.info("Generation Biome: " + biome.getRegistryName());
                    biome.addStructure(Dungeon.DUNGEON, NoFeatureConfig.NO_FEATURE_CONFIG);
                }
            }
        }

        Modules.load();
    }

    @SubscribeEvent
    public void onServerStart(FMLServerAboutToStartEvent event) {
        event.getServer().getResourceManager().addReloadListener(new DataReloadListener());
    }

    public static String getDate() {
        return new SimpleDateFormat().format(new Date());
    }

    public static ResourceLocation locate(String path) {
        return new ResourceLocation(MODID, path);
    }

}
