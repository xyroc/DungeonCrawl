package xiroc.dungeoncrawl.dungeon;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;

import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.api.event.DungeonPlacementCheckEvent;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.config.ObfuscationValues;

public class Dungeon extends Structure<NoFeatureConfig> {

	public static final Set<Biome.Category> ALLOWED_CATEGORIES = ImmutableSet.<Biome.Category>builder()
			.add(Biome.Category.BEACH).add(Biome.Category.DESERT).add(Biome.Category.EXTREME_HILLS)
			.add(Biome.Category.FOREST).add(Biome.Category.ICY).add(Biome.Category.JUNGLE).add(Biome.Category.MESA)
			.add(Biome.Category.PLAINS).add(Biome.Category.RIVER).add(Biome.Category.SAVANNA).add(Biome.Category.SWAMP)
			.add(Biome.Category.TAIGA).add(Biome.Category.RIVER).build();

	public static final Set<Biome.Category> OVERWORLD_CATEGORIES = ImmutableSet.<Biome.Category>builder()
			.addAll(ALLOWED_CATEGORIES).add(Biome.Category.MUSHROOM).add(Biome.Category.OCEAN).build();

	public static final String NAME = DungeonCrawl.MODID + ":dungeon";
	public static final Dungeon DUNGEON = new Dungeon(NoFeatureConfig::deserialize);

	public static final int SIZE = 15;

	public Dungeon(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51427_1_) {
		super(p_i51427_1_);
	}

	public ChunkPos getStartPositionForPosition(ChunkGenerator<?> chunkGenerator, Random random, int x, int z,
			int spacingOffsetsX, int spacingOffsetsZ) {
		int i = 15; // 15
		int j = i - 5; // 10
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
			for (Biome biome : chunkGen.getBiomeProvider().getBiomesInSquare(chunkPosX * 16 - SIZE / 2 * 9,
					chunkPosZ * 16 - SIZE / 2 * 9, 9 * SIZE)) {
				if (!Config.IGNORE_OVERWORLD_BLACKLIST.get() && !chunkGen.hasStructure(biome, DUNGEON)) {
					return false;
				}
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
		return 0;
	}

	public static class Start extends StructureStart {

		public Start(Structure<?> p_i51341_1_, int chunkX, int chunkZ, Biome biomeIn, MutableBoundingBox boundsIn,
				int referenceIn, long seed) {
			super(p_i51341_1_, chunkX, chunkZ, biomeIn, boundsIn, referenceIn, seed);
		}

		@Override
		public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ,
				Biome biomeIn) {
			/*
			 * Some Reflection stuff. I dont like this but it is the only way I know
			 * currently.
			 */
			try {
				Field world = ChunkGenerator.class.getDeclaredField(ObfuscationValues.CHUNKGEN_WORLD);

				world.setAccessible(true);

				Field modifierField = Field.class.getDeclaredField("modifiers");
				modifierField.setAccessible(true);
				modifierField.setInt(world, world.getModifiers() & ~Modifier.FINAL);

				DungeonCrawl.LOGGER.debug("Checking [{}, {}]", chunkX, chunkZ);

				IWorld iWorld = (IWorld) world.get(generator);

				if (!(iWorld instanceof ServerWorld))
					return;

				ServerWorld serverWorld = (ServerWorld) iWorld;
				BlockPos spawn = serverWorld.getSpawnPoint();

				int spawnChunkX = spawn.getX() % 16, spawnChunkZ = spawn.getZ() % 16, chunkSize = SIZE / 2;

				if (serverWorld.getDimension().getType() != DimensionType.OVERWORLD
						|| DungeonCrawl.EVENT_BUS
								.post(new DungeonPlacementCheckEvent(serverWorld, biomeIn, chunkX, chunkZ))
						|| spawnChunkX - chunkX < chunkSize && spawnChunkX - chunkX > -chunkSize
						|| spawnChunkZ - chunkZ < chunkSize / 2 && spawnChunkZ - chunkZ > -chunkSize)
					return;

				/* Undoing everything */

				modifierField.setInt(world, Modifier.PRIVATE | Modifier.FINAL); // TODO Does this work as intended?
				modifierField.setAccessible(false);
				world.setAccessible(false);

			} catch (SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
				DungeonCrawl.LOGGER.error(
						"Failed to access the chunkGen world through reflection. This might result in dungeons getting generated near the spawn chunk.");
				e.printStackTrace();
			}

			ChunkPos chunkpos = new ChunkPos(chunkX, chunkZ);
			long now = System.currentTimeMillis();
			DungeonBuilder builder = new DungeonBuilder(generator, chunkpos, rand);
			this.components.addAll(builder.build());
			this.recalculateStructureSize();
			DungeonCrawl.LOGGER.info("Created the dungeon layout for [{}, {}] ({} ms) ({} pieces).", chunkX, chunkZ,
					(System.currentTimeMillis() - now), this.components.size());
		}

		@Override
		public void generateStructure(IWorld worldIn, Random rand, MutableBoundingBox structurebb, ChunkPos pos) {
			if (!Config.IGNORE_DIMENSION.get() && !(worldIn.getDimension().getType() == DimensionType.OVERWORLD)) {
				DungeonCrawl.LOGGER.info(
						"Cancelling the generation of an existing Dungeon because it is not in the overworld. To avoid this, set \"ignore_dimension\" in the config to true. Dimension: {}",
						worldIn.getDimension().getType());
				return;
			}
			super.generateStructure(worldIn, rand, structurebb, pos);
		}

	}

}