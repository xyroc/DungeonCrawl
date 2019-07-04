package xiroc.dungeoncrawl.dungeon;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonPieces.DungeonPiece;

public class Dungeon extends Structure<NoFeatureConfig> {

	// MineshaftStructure MineshaftPieces

	public static final IStructurePieceType ROOM = IStructurePieceType.register(DungeonPieces.Room::new, "ROOM");
	public static final IStructurePieceType CORRIDOR = IStructurePieceType.register(DungeonPieces.Corridor::new, "CRRDR");
	public static final IStructurePieceType STAIRSTOP = IStructurePieceType.register(DungeonPieces.StairsTop::new, "STTP");
	public static final IStructurePieceType STAIRS = IStructurePieceType.register(DungeonPieces.Stairs::new, "STRS");
	public static final IStructurePieceType STAIRSBOT = IStructurePieceType.register(DungeonPieces.StairsBot::new, "STBT");

	public Dungeon(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51427_1_) {
		super(p_i51427_1_);
	}

	@Override
	public boolean hasStartAt(ChunkGenerator<?> chunkGen, Random rand, int chunkPosX, int chunkPosZ) {
		return rand.nextDouble() < 0.001;

		/*
		 * Biome biome = chunkGen.getBiomeProvider().getBiome(new BlockPos((chunkPosX <<
		 * 4) + 9, 0, (chunkPosZ << 4) + 9)); if (chunkGen.hasStructure(biome, this)) {
		 * DungeonCrawl.LOGGER.info("roll"); } else {
		 * DungeonCrawl.LOGGER.info("no structure for biome at " + chunkPosX + ",  " +
		 * chunkPosZ); return false; }
		 */
	}

	@Override
	public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
		if (!worldIn.getWorldInfo().isMapFeaturesEnabled()) {
			DungeonCrawl.LOGGER.info("no Map Features");
			return false;
		} else {
			int i = pos.getX() >> 4;
			int j = pos.getZ() >> 4;
			int k = i << 4;
			int l = j << 4;
			ChunkPos chunkpos = new ChunkPos(pos.getX() / 16, pos.getZ() / 16);
			if (hasStartAt(generator, rand, chunkpos.x, chunkpos.z)) {
				Dungeon.Start structurestart = new Dungeon.Start(this, chunkpos.x, chunkpos.z, worldIn.getBiome(pos), new MutableBoundingBox(0, 0, 0, 128, 128, 128), 0, worldIn.getSeed());
				structurestart.generateStructure(worldIn, rand, new MutableBoundingBox(k, l, k + 15, l + 15), new ChunkPos(i, j));
			}
			return true;
		}
	}

	@Override
	public IStartFactory getStartFactory() {
		return Dungeon.Start::new;
	}

	@Override
	public String getStructureName() {
		return "DungeonCrawlDungeon";
	}

	@Override
	public int getSize() {
		return 8;
	}

	public static class Start extends StructureStart {

		private List<DungeonPiece> pieces = Lists.newArrayList();

		ChunkGenerator<?> generator;

		public Start(Structure<?> p_i51341_1_, int chunkX, int chunkZ, Biome biomeIn, MutableBoundingBox boundsIn, int referenceIn, long seed) {
			super(p_i51341_1_, chunkX, chunkZ, biomeIn, boundsIn, referenceIn, seed);
		}

		@Override
		public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
			this.generator = generator;

		}

		@Override
		public void generateStructure(IWorld worldIn, Random rand, MutableBoundingBox structurebb, ChunkPos pos) {
			DungeonBuilder builder = new DungeonBuilder((WorldGenRegion) worldIn, pos, rand);
			this.pieces.addAll(builder.build());
			/*
			 * for (StructurePiece piece : this.components) piece.buildComponent(piece,
			 * this.components, rand);
			 */
			// this.func_214628_a(generator.getSeaLevel(), rand, 10);
			synchronized (pieces) {
				DungeonCrawl.LOGGER.info("processing a total of " + pieces.size() + " pieces...");
				Iterator<DungeonPiece> iterator = pieces.iterator();

				while (iterator.hasNext()) {
					DungeonPiece structurepiece = iterator.next();
					structurepiece.addComponentParts(worldIn, rand, structurebb, pos);
					iterator.remove();
				}
				DungeonCrawl.LOGGER.info("-/-");
				this.recalculateStructureSize();
			}
		}

	}

}