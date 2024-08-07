package xiroc.dungeoncrawl.dungeon.component;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.IdMapper;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.component.feature.ChestComponent;
import xiroc.dungeoncrawl.dungeon.component.feature.FlowerPotComponent;
import xiroc.dungeoncrawl.dungeon.component.feature.FurnaceComponent;
import xiroc.dungeoncrawl.dungeon.component.feature.SarcophagusComponent;
import xiroc.dungeoncrawl.dungeon.component.feature.SpawnerComponent;
import xiroc.dungeoncrawl.dungeon.component.feature.TNTChestComponent;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;

import java.util.Random;

public interface DungeonComponent {
    private static IdMapper<Decoder<? extends DungeonComponent>> gatherDecoders() {
        IdMapper<Decoder<? extends DungeonComponent>> map = new IdMapper<>();
        map.addMapping(BlueprintComponent.CODEC, 1);
        map.addMapping(StaircaseComponent.CODEC, 2);
        map.addMapping(TunnelComponent.CODEC, 3);
        map.addMapping(EntranceComponent.CODEC, 4);

        map.addMapping(ChestComponent.CODEC, 10);
        map.addMapping(TNTChestComponent.CODEC, 11);
        map.addMapping(SpawnerComponent.CODEC, 12);
        map.addMapping(SarcophagusComponent.CODEC, 13);

        map.addMapping(FurnaceComponent.CODEC, 20);

        map.addMapping(FlowerPotComponent.CODEC, 30);
        return map;
    }

    IdMapper<Decoder<? extends DungeonComponent>> DECODERS = gatherDecoders();

    Codec<DungeonComponent> CODEC = new Codec<>() {
        private static DataResult<Decoder<? extends DungeonComponent>> decoder(int type) {
            var decoder = DECODERS.byId(type);
            if (decoder == null) {
                return DataResult.error("Invalid component type: " + type);
            }
            return DataResult.success(decoder);
        }

        private static final String KEY_TYPE = "type";
        private static final String KEY_DATA = "data";

        @Override
        public <T> DataResult<Pair<DungeonComponent, T>> decode(DynamicOps<T> dynamicOps, T t) {
            return dynamicOps.getMap(t)
                    .flatMap(map -> Codec.INT.decode(dynamicOps, map.get(KEY_TYPE))
                            .flatMap(type -> decoder(type.getFirst())
                                    .flatMap(dec -> dec.decode(dynamicOps, map.get(KEY_DATA)))))
                    .map(pair -> Pair.of(pair.getFirst(), pair.getSecond())); // Converts Pair<? extends DungeonComponent, T> to Pair<DungeonComponent, T>
        }

        @Override
        public <T> DataResult<T> encode(DungeonComponent component, DynamicOps<T> dynamicOps, T t) {
            var map = dynamicOps.mapBuilder();
            map.add(KEY_TYPE, Codec.INT.encodeStart(dynamicOps, component.componentType()));
            map.add(KEY_DATA, component.encode(dynamicOps));
            return map.build(t);
        }
    };

    void generate(LevelAccessor level, BoundingBox worldGenBounds, Random random, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage);

    BoundingBoxBuilder boundingBox();

    int componentType();

    <T> DataResult<T> encode(DynamicOps<T> ops);
}
