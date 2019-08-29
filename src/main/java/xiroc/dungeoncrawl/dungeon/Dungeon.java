package xiroc.dungeoncrawl.dungeon;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Locale;
import java.util.Random;
import java.util.function.Function;

import com.mojang.datafixers.Dynamic;

import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelRegistry;
import xiroc.dungeoncrawl.util.Config;

public class Dungeon extends Structure<NoFeatureConfig> {

	public static final String NAME = "DCDungeon";
	public static final Dungeon DUNGEON = new Dungeon(NoFeatureConfig::deserialize);
	public static final Structure<NoFeatureConfig> DUNGEON_FEATURE = registerFeature(
			DungeonCrawl.MODID + ":" + NAME.toLowerCase(Locale.ROOT), DUNGEON);
	public static final Structure<?> DUNGEON_STRUCTURE = registerStructure(NAME, DUNGEON_FEATURE);

	public static final IStructurePieceType ENTRANCE_BUILDER = IStructurePieceType
			.register(DungeonPieces.EntranceBuilder::new, "DUNGEON_ENTR_BLDR");
	public static final IStructurePieceType ROOM = IStructurePieceType.register(DungeonPieces.Room::new,
			"DUNGEON_ROOM");
	public static final IStructurePieceType CORRIDOR = IStructurePieceType.register(DungeonPieces.Corridor::new,
			"DUNGEON_CRRDR");
	public static final IStructurePieceType STAIRSTOP = IStructurePieceType.register(DungeonPieces.StairsTop::new,
			"DUNGEON_STTP");
	public static final IStructurePieceType STAIRS = IStructurePieceType.register(DungeonPieces.Stairs::new,
			"DUNGEON_STRS");
	public static final IStructurePieceType STAIRSBOT = IStructurePieceType.register(DungeonPieces.StairsBot::new,
			"DUNGEON_STBT");
	public static final IStructurePieceType HOLE = IStructurePieceType.register(DungeonPieces.Hole::new,
			"DUNGEON_HOLE");
	public static final IStructurePieceType CORRIDOR_ROOM = IStructurePieceType
			.register(DungeonPieces.CorridorRoom::new, "DUNGEON_CRRDR_ROOM");
	public static final IStructurePieceType CORRIDOR_TRAP = IStructurePieceType
			.register(DungeonPieces.CorridorTrap::new, "DUNGEON_TRAP");
	public static final IStructurePieceType PART = IStructurePieceType.register(DungeonPieces.Part::new,
			"DUNGEON_PART");
	public static final IStructurePieceType HOLE_TRAP = IStructurePieceType.register(DungeonPieces.HoleTrap::new,
			"DUNGEON_HOLE_TRAP");

	public Dungeon(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51427_1_) {
		super(p_i51427_1_);
	}

	public ChunkPos getStartPositionForPosition(ChunkGenerator<?> chunkGenerator, Random random, int x, int z,
			int spacingOffsetsX, int spacingOffsetsZ) {
		int i = 15;
		int j = 10;
		int k = x + i * spacingOffsetsX;
		int l = z + i * spacingOffsetsZ;
		int i1 = k < 0 ? k - i + 1 : k;
		int j1 = l < 0 ? l - i + 1 : l;
		int k1 = i1 / i;
		int l1 = j1 / i;
		((SharedSeedRandom) random).setLargeFeatureSeedWithSalt(chunkGenerator.getSeed(), k1, l1, 10387319);
		k1 = k1 * i;
		l1 = l1 * i;
		k1 = k1 + (random.nextInt(i - j) + random.nextInt(i - j)) / 2;
		l1 = l1 + (random.nextInt(i - j) + random.nextInt(i - j)) / 2;
		return new ChunkPos(k1, l1);
	}

	@Override
	public boolean hasStartAt(ChunkGenerator<?> chunkGen, Random rand, int chunkPosX, int chunkPosZ) {
		ChunkPos chunkpos = this.getStartPositionForPosition(chunkGen, rand, chunkPosX, chunkPosZ, 0, 0);
		if (chunkPosX == chunkpos.x && chunkPosZ == chunkpos.z) {
			for (Biome biome : chunkGen.getBiomeProvider().getBiomesInSquare(chunkPosX * 16 + 64, chunkPosZ * 16 + 64,
					128)) {
				if (!Config.IGNORE_OVERWORLD_BLACKLIST.get() && !chunkGen.hasStructure(biome, DUNGEON))
					return false;
			}
			return rand.nextFloat() < Config.DUNGEON_PROBABLILITY.get();
		} else {
			return false;
		}
	}

	@Override
	public IStartFactory getStartFactory() {
		return Dungeon.Start::new;
	}

	@Override
	public String getStructureName() {
		return NAME;
	}

	@Override
	public int getSize() {
		return 8;
	}

	public static class Start extends StructureStart {

		public Start(Structure<?> p_i51341_1_, int chunkX, int chunkZ, Biome biomeIn, MutableBoundingBox boundsIn,
				int referenceIn, long seed) {
			super(p_i51341_1_, chunkX, chunkZ, biomeIn, boundsIn, referenceIn, seed);
		}

		@Override
		public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ,
				Biome biomeIn) {
			ChunkPos chunkpos = new ChunkPos(chunkX, chunkZ);
			long now = System.currentTimeMillis();
			DungeonBuilder builder = new DungeonBuilder(generator, chunkpos, rand);
			this.components.addAll(builder.build());
			this.recalculateStructureSize();
			DungeonCrawl.LOGGER.info("Built dungeon logic for [{}, {}] ({} ms) ({} pieces)", chunkX, chunkZ,
					(System.currentTimeMillis() - now), this.components.size());
		}

		@Override
		public void generateStructure(IWorld worldIn, Random rand, MutableBoundingBox structurebb, ChunkPos pos) {
			if (!Config.IGNORE_DIMENSION.get() && !(worldIn.getDimension().getType() == DimensionType.OVERWORLD)) {
				DungeonCrawl.LOGGER.warn("Refusing to generate a placed Dungeon in {} because it is not in OVERWORLD.",
						worldIn.getDimension().getType());
				return;
			}
			DungeonSegmentModelRegistry.load(((ServerWorld) worldIn.getWorld()).getServer().getResourceManager());
			super.generateStructure(worldIn, rand, structurebb, pos);
		}

	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	public static <C extends IFeatureConfig, F extends Feature<C>> F registerFeature(String key, F value) {
		return (F) (Registry.<Feature<?>>register(Registry.FEATURE, key, value));
	}

	public static Structure<?> registerStructure(String key, Structure<?> p_215141_1_) {
		return Registry.register(Registry.STRUCTURE_FEATURE, key.toLowerCase(Locale.ROOT), p_215141_1_);
	}

}