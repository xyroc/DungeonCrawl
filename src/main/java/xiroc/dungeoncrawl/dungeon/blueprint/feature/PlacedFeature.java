package xiroc.dungeoncrawl.dungeon.blueprint.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.type.ChestFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.type.FlowerPotFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.type.SarcophagusFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.type.SpawnerFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.type.TNTChestFeature;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;

import java.util.ArrayList;
import java.util.Random;

public interface PlacedFeature {
    void place(WorldGenLevel level, Random random, BoundingBox worldGenBounds, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage);

    Types TYPES = new Types();

    int CHEST = TYPES.register(ChestFeature.CODEC);
    int SPAWNER = TYPES.register(SpawnerFeature.CODEC);
    int TNT_CHEST = TYPES.register(TNTChestFeature.CODEC);
    int FLOWER_POT = TYPES.register(FlowerPotFeature.CODEC);
    int SARCOPHAGUS = TYPES.register(SarcophagusFeature.CODEC);

    static Codec<PlacedFeature> codec(int type) {
        return TYPES.codec(type);
    }

    class Types {
        private final ArrayList<Codec<PlacedFeature>> codecs = new ArrayList<>();

        private int register(Codec<PlacedFeature> codec) {
            final int id = codecs.size();
            codecs.add(codec);
            return id;
        }

        Codec<PlacedFeature> codec(int id) {
            return codecs.get(id);
        }
    }
}
