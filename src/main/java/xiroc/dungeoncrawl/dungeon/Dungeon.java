package xiroc.dungeoncrawl.dungeon;

import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import java.util.function.Function;

import com.mojang.datafixers.Dynamic;

import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.DungeonCrawl;

public class Dungeon extends Structure<NoFeatureConfig> {

	// MineshaftStructure MineshaftPieces Chunk Feature WoodlandMansionStructure
	// Structures IStructureProcessorType DungeonsFeature

	public static final String NAME = "DCDungeon";
	public static final Dungeon DUNGEON = new Dungeon(NoFeatureConfig::deserialize);
	public static final Structure<NoFeatureConfig> DUNGEON_FEATURE = registerFeature(NAME.toLowerCase(Locale.ROOT), DUNGEON);
	public static final Structure<?> DUNGEON_STRUCTURE = registerStructure(NAME, DUNGEON_FEATURE);

	public static final IStructurePieceType ENTRANCE_BUILDER = IStructurePieceType.register(DungeonPieces.EntranceBuilder::new, "DUNGEON_ENTR_BLDR");
	public static final IStructurePieceType ROOM = IStructurePieceType.register(DungeonPieces.Room::new, "DUNGEON_ROOM");
	public static final IStructurePieceType CORRIDOR = IStructurePieceType.register(DungeonPieces.Corridor::new, "DUNGEON_CRRDR");
	public static final IStructurePieceType STAIRSTOP = IStructurePieceType.register(DungeonPieces.StairsTop::new, "DUNGEON_STTP");
	public static final IStructurePieceType STAIRS = IStructurePieceType.register(DungeonPieces.Stairs::new, "DUNGEON_STRS");
	public static final IStructurePieceType STAIRSBOT = IStructurePieceType.register(DungeonPieces.StairsBot::new, "DUNGEON_STBT");

	public Dungeon(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51427_1_) {
		super(p_i51427_1_);
	}

	@Override
	public boolean hasStartAt(ChunkGenerator<?> chunkGen, Random rand, int chunkPosX, int chunkPosZ) {
		((SharedSeedRandom) rand).setLargeFeatureSeed(chunkGen.getSeed(), chunkPosX, chunkPosZ);
//		DungeonCrawl.LOGGER.info("hasStartAt [" + chunkPosX + " , " + chunkPosZ + "]");
		for (Biome biome : chunkGen.getBiomeProvider().getBiomesInSquare(chunkPosX * 16 + 9, chunkPosZ * 16 + 9, 32)) {
			if (!chunkGen.hasStructure(biome, DUNGEON_FEATURE)) {
				return false;
			}
		}
		return rand.nextDouble() < 0.0025;
	}

	@Override
	public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
//		DungeonCrawl.LOGGER.info("place [" + (pos.getX() >> 4) + ", " + (pos.getZ() >> 4) + "]");
		return super.place(worldIn, generator, rand, pos, config);
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

		public Start(Structure<?> p_i51341_1_, int chunkX, int chunkZ, Biome biomeIn, MutableBoundingBox boundsIn, int referenceIn, long seed) {
			super(p_i51341_1_, chunkX, chunkZ, biomeIn, boundsIn, referenceIn, seed);
		}

		@Override
		public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
			ChunkPos chunkpos = new ChunkPos(chunkX, chunkZ);
			long now = System.currentTimeMillis();
			DungeonBuilder builder = new DungeonBuilder(generator, chunkpos, rand);
			this.components.addAll(builder.build());
			this.recalculateStructureSize();
			DungeonCrawl.LOGGER.info("Built dungeon logic for [" + chunkX + ", " + chunkZ + "]" + "(" + (System.currentTimeMillis() - now) + " ms) (" + this.components.size() + " pieces)");
		}

		@Override
		public void generateStructure(IWorld worldIn, Random rand, MutableBoundingBox structurebb, ChunkPos pos) {
//			DungeonCrawl.LOGGER.info("Generating Structure [" + pos.x + ", " + pos.z + "] " + structurebb.minX + " " + structurebb.minY + " " + structurebb.minZ + " ; " + structurebb.maxX + " " + structurebb.maxY + " " + structurebb.maxZ);
			synchronized (this.components) {
				Iterator<StructurePiece> iterator = this.components.iterator();

				while (iterator.hasNext()) {
					StructurePiece structurepiece = iterator.next();
//					DungeonCrawl.LOGGER.info("Piece " + structurepiece.getBoundingBox().minX + " " + structurepiece.getBoundingBox().minY + " " + structurepiece.getBoundingBox().minZ + " ; " + structurepiece.getBoundingBox().maxX + " "
//							+ structurepiece.getBoundingBox().maxY + " " + structurepiece.getBoundingBox().maxZ);
					if (structurepiece.getBoundingBox().intersectsWith(structurebb) && !structurepiece.addComponentParts(worldIn, rand, structurebb, pos)) {
						iterator.remove();
					}
				}

				this.recalculateStructureSize();
			}
			/*
			 * for (StructurePiece piece : this.components) piece.buildComponent(piece,
			 * this.components, rand);
			 */
			// this.func_214628_a(generator.getSeaLevel(), rand, 10);
//			synchronized (components) {
//				DungeonCrawl.LOGGER.info("Processing a total of " + components.size() + " pieces...");
//				Iterator<StructurePiece> iterator = components.iterator();
//
//				while (iterator.hasNext()) {
//					StructurePiece structurepiece = iterator.next();
//					structurepiece.addComponentParts(worldIn, rand, structurebb, pos);
//					iterator.remove();
//				}
//				DungeonCrawl.LOGGER.info("-/-");
//				this.recalculateStructureSize();
//			}
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